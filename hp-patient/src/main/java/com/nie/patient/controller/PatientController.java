package com.nie.patient.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.nie.common.interceptor.UserInfoInterceptor;
import com.nie.common.tools.*;
import com.nie.feign.client.AuthClient;
import com.nie.feign.dto.PageDTO;
import com.nie.patient.pojo.Doctor;
import com.nie.patient.pojo.Orders;
import com.nie.patient.pojo.Patient;
import com.nie.patient.service.DoctorService;
import com.nie.patient.service.OrderService;
import com.nie.patient.service.PatientService;
import com.nie.patient.tools.PdfUtil;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    private final DoctorService doctorService;

    private final OrderService orderService;

    private final StringRedisTemplate stringRedisTemplate;

    private final AuthClient authClient;
    private final RedisOPS redisOPS;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseData adminLogin(
            @RequestParam("Id") int Id,
            @RequestParam("Password") String Password,
            @RequestParam("captcha") String captcha,
            @RequestParam("role") String role,
            HttpSession session
    ) {
        System.out.println("patient thread ID: " + Thread.currentThread().getId());
        LambdaQueryWrapper<Patient> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Patient::getPId, Id);
        final Patient patient = patientService.getOne(lambdaQueryWrapper);

        final HashMap<String, String> map = new HashMap<>();

        String captcha1 = stringRedisTemplate.opsForValue().get("captcha");
        if (!captcha.equals(captcha1) || captcha.isEmpty()) {
            return ResponseData.fail("验证码错误");
        }

        final Object rpid = redisOPS.getCacheObject(RedisKeyPrefix.Logged_in_patient);
        if (rpid != null && (int) rpid == Id) {
            return ResponseData.fail("账号已经登录");
        }
        redisOPS.setCacheObject(RedisKeyPrefix.Logged_in_patient, patient.getPId());
        redisOPS.expire(RedisKeyPrefix.Logged_in_patient, 5);

        try {
            //将密码和账号封装成一个security的待验证对象，然后提交给authenticationManager
            final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(Id, Password);
            final Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("authentication : {}", authentication);
            map.put("pName", patient.getPName());
            map.put("pId", String.valueOf(patient.getPId()));
            String token = JwtUtil.getToken(map);
            map.put("token", token);
            return ResponseData.success("登录成功", map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseData.fail("登录失败，密码或账号或验证码错误");
//        LambdaQueryWrapper<Patient> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(Patient::getPId,Id);
//        lambdaQueryWrapper.eq(Patient::getPPassword,Password);
//        final Patient patient = patientService.getOne(lambdaQueryWrapper);
//        final Gson gson = new Gson();
//        final String userjson = gson.toJson(patient);
//
//        final Map<String, String> login = authClient.login(Id, Password, captcha, role,userjson);
//
//
//        if (login != null && !login.isEmpty()){
//            return ResponseData.success("登录成功",login);
//        }
//        return ResponseData.fail("登录失败，密码或账号或验证码错误");
    }


    @GetMapping("/logout")
    public Result logout() {
        final boolean b = redisOPS.deleteObject(RedisKeyPrefix.Logged_in_patient);
        if (b) {
            log.info("exit success");
            return Result.success("退出成功");
        }
        return Result.success("退出成功");
    }


    @PostMapping("/addPatient")
    public ResponseData addPatient(@RequestBody Patient patient) throws Exception {
        patientService.save(patient);
        return ResponseData.success("注册成功");
    }

    @GetMapping("/findDoctorBySection")
    public ResponseData findDoctorBySection(@RequestParam("dSection") String dSection) {
        LambdaQueryWrapper<Doctor> lambdaQueryWrapper = new LambdaQueryWrapper<Doctor>();
        lambdaQueryWrapper.eq(Doctor::getdSection, dSection);
        List<Doctor> doctors = doctorService.list(lambdaQueryWrapper);
        return ResponseData.success("所有医生", doctors);
    }

    @GetMapping("/findOrderByPid")
    public Result findOrderByPid(String pId) {
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getPId, pId);
        List<Orders> orders = orderService.list(lambdaQueryWrapper);
        return Result.success(orders);
    }

    @PostMapping("/findAllPatients")
    public Page<Patient> findDoctors(@RequestBody PageDTO pageDTO) {
        Page<Patient> pageInfo = new Page<>(pageDTO.getPageNumber(), pageDTO.getSize());
        LambdaQueryWrapper<Patient> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(pageDTO.getQuery() != null && !pageDTO.getQuery().isEmpty(), Patient::getPName, pageDTO.getQuery());
        patientService.page(pageInfo, lambdaQueryWrapper);

        return pageInfo;
    }

    @DeleteMapping("/deletePatient")
    public boolean deletePatient(@RequestParam("pId") int pId) {
        boolean remove = patientService.removeById(pId);
        return remove;
    }

    @GetMapping("/patientAge")
    public Result patientAge() {
        LambdaQueryWrapper<Patient> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(Patient::getPAge);
        List<Patient> list = patientService.list(lambdaQueryWrapper);
        List<Integer> collect = list.stream()
                .filter(Objects::nonNull)
                .map(patient -> patient.getPAge()).collect(Collectors.toList());
        return Result.success(collect);
    }

    @GetMapping("/findPatientById")
    public Result findPatientById(@RequestParam("pId") int pId) throws Exception {
        System.out.println("findp thread ID: " + Thread.currentThread().getId());
        Patient patient = patientService.getById(pId);
        log.info("myLocal : {}", UserInfoInterceptor.userInfoThreadLocal.get().toString());
        return Result.success(patient);
    }

    @GetMapping("/securityP")
    Patient securityP(@RequestParam("pId") int pId) {
        return patientService.getById(pId);
    }


    @PostMapping("/findPatientById2")
    public Result findPatientById2() {
        System.out.println("当前线程ID: " + Thread.currentThread().getId());
        LambdaQueryWrapper<Patient> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Patient::getPId, UserContest.getUserId());

        Patient patient = patientService.getOne(lambdaQueryWrapper);
        return Result.success(patient);
    }

    @GetMapping("/pdf")
    public void downloadPDF(HttpServletRequest request, HttpServletResponse response, int oId) throws Exception {
        Orders order = this.findOrderByOid(oId);
        PdfUtil.ExportPdf(request, response, order);
    }

    @GetMapping("/findOrderByOid")
    public Orders findOrderByOid(int oId) {
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Orders::getOId, oId);
        Orders orders = orderService.getOne(lambdaQueryWrapper);
        return orders;
    }


}
