package com.nie.auth.controller;

import com.alibaba.nacos.common.codec.Base64;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nie.auth.pojo.MyLogin;
import com.nie.auth.service.IMyLoginService;
import com.nie.common.tools.*;
import com.nie.feign.dto.*;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequestMapping("/auth")
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final StringRedisTemplate stringRedisTemplate;

    private final RedisOPS redisOPS;

    private final IMyLoginService myLoginService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final Gson gson;

    private final List<AIMessage> aiMsg = new ArrayList<>();

    @PostMapping("/login")
    public Map<String, String> adminLogin(
            @RequestParam("Id") int Id,
            @RequestParam("Password") String Password,
            @RequestParam("captcha") String captcha,
            @RequestParam("role") String role,
            @RequestParam("user") String user,
            HttpSession session
    ) {
        Map<String, String> map = new HashMap<>();
        String captcha1 = stringRedisTemplate.opsForValue().get("captcha");
        if (!captcha.equals(captcha1) || captcha.isEmpty())
            return map;

        final Gson gson = new Gson();
        if (role.equals("管理员")) {
            final Admin admin = gson.fromJson(user, Admin.class);
            if (!user.isEmpty() && !user.equals(null)) {
                map.put("aName", admin.getAName());
                map.put("aId", String.valueOf(admin.getAId()));
                String token = JwtUtil.getToken(map);
                map.put("token", token);

                redisOPS.setCacheObject(RedisKeyPrefix.Logged_in_admin, admin.getAId());
                redisOPS.expire(RedisKeyPrefix.Logged_in_admin, 5);
                return map;
            } else {
                return map;
            }
        }
        if (role.equals("医生")) {
            final Doctor doctor = gson.fromJson(user, Doctor.class);
            if (!user.isEmpty() && !user.equals(null)) {
                map.put("dName", doctor.getdName());
                map.put("dId", String.valueOf(doctor.getdId()));
                String token = JwtUtil.getToken(map);
                map.put("token", token);

                redisOPS.setCacheObject(RedisKeyPrefix.Logged_in_doctor, doctor.getdId());
                redisOPS.expire(RedisKeyPrefix.Logged_in_doctor, 5);
                return map;
            } else {
                return map;
            }
        }
        if (role.equals("患者")) {
            final Patient patient = gson.fromJson(user, Patient.class);
            if (!user.isEmpty() && !user.equals(null)) {
                map.put("pName", patient.getPName());
                map.put("pId", String.valueOf(patient.getPId()));
                String token = JwtUtil.getToken(map);
                map.put("token", token);

                redisOPS.setCacheObject(RedisKeyPrefix.Logged_in_patient, patient.getPId());
                redisOPS.expire(RedisKeyPrefix.Logged_in_patient, 5);

//                session.setAttribute("userP", user);
//                log.info("session id {},{}",session.getId(),session.getAttribute("userP"));
                return map;
            } else {
                return map;
            }
        }
        return map;

    }

    @GetMapping("/captcha")
    public void verifyCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 三个参数分别为宽、高、位数
        SpecCaptcha specCaptcha = new SpecCaptcha(120, 31, 5);
        // 设置字体
        specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        // 设置类型，纯数字、纯字母、字母数字混合
        specCaptcha.setCharType(Captcha.TYPE_ONLY_NUMBER);
        stringRedisTemplate.opsForValue().set("captcha", specCaptcha.text().toLowerCase(), 60, TimeUnit.SECONDS);

        // 输出图片流
        specCaptcha.out(response.getOutputStream());
    }

    @GetMapping("/register")
    public Result newLogin(@RequestParam("phone") String phone, @RequestParam("password") String password) {
        if (!StringUtils.hasText(phone) && !StringUtils.hasText(password)) {
            return Result.error("手机号或密码为空");
        }
        LambdaQueryWrapper<MyLogin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MyLogin::getPhone, phone);
        final MyLogin myLogin = myLoginService.getOne(lambdaQueryWrapper);
        if (myLogin != null)
            return Result.error("注册失败，该手机号已被注册");
        final MyLogin loginUser = new MyLogin();
        loginUser.setPhone(phone)
                .setPassword(bCryptPasswordEncoder.encode(password));
        myLoginService.save(loginUser);
        return Result.success("注册成功");
    }

    @GetMapping("/myLogin")
    public Result myLogin(@RequestParam("phone") String phone, @RequestParam("password") String password) {
        if (!StringUtils.hasText(phone) && !StringUtils.hasText(password)) {
            return Result.error("手机号或密码为空");
        }
        LambdaQueryWrapper<MyLogin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MyLogin::getPhone, phone);
        final MyLogin myLogin = myLoginService.getOne(lambdaQueryWrapper);
        if (myLogin == null)
            return Result.error("该手机号未注册");
        final boolean matches = bCryptPasswordEncoder.matches(password, myLogin.getPassword());
        if (!matches)
            return Result.error("密码错误");
        return Result.success("登录成功");
    }


    @PostMapping("/dsai")
    public Result dsai(@org.springframework.web.bind.annotation.RequestBody List<AIMessage> msgList, @RequestParam("aiModel") String aiModel) {
        log.info("aiModel: {}", aiModel);
        String apiKey = "sk-e49493afde514dd39b35e569b87011f4";
        String baseURL = "https://api.deepseek.com/chat/completions";
        log.info("msgList:{}", msgList.toString());
        try {
            final ChatRecords chatRecords = new ChatRecords();
            chatRecords.setMessages(msgList);
            if (StringUtils.hasText(aiModel)) {
                chatRecords.setModel(aiModel);
            }
            final String toJson = gson.toJson(chatRecords);
            log.info("toJson:{}", toJson);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(300, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .writeTimeout(300, TimeUnit.SECONDS)
                    .build();
            MediaType mediaType = MediaType.parse("application/json");

            RequestBody body = RequestBody.create(mediaType, toJson);
            Request request = new Request.Builder()
                    .url(baseURL)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .build();
            Response response = client.newCall(request).execute();
            //响应流被读取一次就被关闭了，后续就无法再读取，所以将读取的内容保存，供后面使用
            final String responseBody = response.body().string();
            log.info("responseBody:{}", responseBody);
            final JsonObject fromJson = gson.fromJson(responseBody, JsonObject.class);
            final JsonArray choices = fromJson.getAsJsonArray("choices");
            final JsonObject message = choices.get(0).getAsJsonObject().getAsJsonObject("message");
            final AIMessage aiMessage = gson.fromJson(message, AIMessage.class);
            log.info("aiMessage:{}", aiMessage);
            return Result.success(aiMessage);
        } catch (Exception e) {
            log.error("dsai error:{}", e.getMessage());
            return Result.error("请求失败");
        }
    }

}
