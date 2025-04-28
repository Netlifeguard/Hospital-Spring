package com.nie.doctor.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.nie.common.tools.*;
import com.nie.doctor.pojo.Doctor;
import com.nie.doctor.service.DoctorService;
import com.nie.feign.client.AuthClient;
import com.nie.feign.client.PatientClient;
import com.nie.feign.dto.Orders;
import com.nie.feign.dto.PageDTO;
import com.nie.feign.dto.Patient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
@Tag(name = "医生管理", description = "医生管理相关接口文档")
public class DoctorController {

    private final DoctorService doctorService;
    private final PatientClient patientClient;
    private final AuthClient authClient;
    private final RedisOPS redisOPS;

    private final AuthenticationManager authenticationManager;
    private final StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "校验登录")
    @PostMapping("/login")
    public ResponseData adminLogin(
            @RequestParam("Id") int Id,
            @RequestParam("Password") String Password,
            @RequestParam("captcha") String captcha,
            @RequestParam("role") String role,
            HttpSession session
    ) {

        LambdaQueryWrapper<Doctor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Doctor::getdId, Id);
        lambdaQueryWrapper.eq(Doctor::getdPassword, Password);
        Doctor user = doctorService.getOne(lambdaQueryWrapper);

        final HashMap<String, String> map = new HashMap<>();

        String captcha1 = stringRedisTemplate.opsForValue().get("captcha");
        if (!captcha.equals(captcha1) || captcha.isEmpty()) {
            return ResponseData.fail("验证码错误");
        }

        final Object rdid = redisOPS.getCacheObject(RedisKeyPrefix.Logged_in_doctor);
        if (rdid != null && (int) rdid == Id) {
            return ResponseData.fail("账号已经登录");
        }
        redisOPS.setCacheObject(RedisKeyPrefix.Logged_in_doctor, user.getdId());
        redisOPS.expire(RedisKeyPrefix.Logged_in_doctor, 5);

        try {
            //将密码和账号封装成一个security的待验证对象，然后提交给authenticationManager
            final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(Id, Password);
            final Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Psession id :{}", session.getId());

            log.info("authentication : {}", authentication);
            map.put("dName", user.getdName());
            map.put("dId", String.valueOf(user.getdId()));
            String token = JwtUtil.getToken(map);
            map.put("token", token);
            return ResponseData.success("登录成功", map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseData.fail("登录失败，密码或账号或验证码错误");
//        final Gson gson = new Gson();
//        final String userjson = gson.toJson(user);
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
        final boolean b = redisOPS.deleteObject(RedisKeyPrefix.Logged_in_doctor);
        if (b) {
            log.info("exit success");
            return Result.success("退出成功");
        }
        return Result.success("退出成功");
    }

    @GetMapping("/securityD")
    Doctor securityD(@RequestParam("dId") int dId) {
        final Doctor doctor = doctorService.getById(dId);
        return doctor;
    }

    @Operation(summary = "根据部分查询医生")
    @GetMapping("/findDoctorBySection")
    public ResponseData findDoctorBySection(@RequestParam("dSection") String dSection) {
        LambdaQueryWrapper<Doctor> lambdaQueryWrapper = new LambdaQueryWrapper<Doctor>();
        lambdaQueryWrapper.eq(Doctor::getdSection, dSection);
        List<Doctor> doctors = doctorService.list(lambdaQueryWrapper);
        return ResponseData.success("所有医生", doctors);
    }

    @Operation(summary = "获取所有医生")
    @PostMapping("/findAllDoctors")
    public Page<Doctor> findDoctors(@RequestBody PageDTO pageDTO) {
        Page<Doctor> pageInfo = new Page<>(pageDTO.getPageNumber(), pageDTO.getSize());
        LambdaQueryWrapper<Doctor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(pageDTO.getQuery() != null && !pageDTO.getQuery().isEmpty(), Doctor::getdName, pageDTO.getQuery());
        doctorService.page(pageInfo, lambdaQueryWrapper);

        return pageInfo;
    }

    @Operation(summary = "admin：添加医生")
    @PostMapping("/addDoctor")
    void addDoctor(@RequestBody Doctor doctor) {
        doctorService.save(doctor);
    }

    @Operation(summary = "admin：修改医生")
    @PostMapping("/modifyDoctor")
    public boolean modifyDoctor(@RequestBody Doctor doctor) {
        boolean b = doctorService.updateById(doctor);
        return b;
    }

    @Operation(summary = "admin：删除医生")
    @DeleteMapping("/deleteDoctor")
    boolean deleteDoctor(@RequestParam("dId") String dId) {
        boolean b = doctorService.removeById(Integer.valueOf(dId));
        return b;
    }

    @GetMapping("/findDoctor")
    public Doctor findDoctor(@RequestParam("dId") String dId) {
        LambdaQueryWrapper<Doctor> lambdaQueryWrapper = new LambdaQueryWrapper<Doctor>();
        lambdaQueryWrapper.eq(Doctor::getdId, dId);
        Doctor doctor = doctorService.getOne(lambdaQueryWrapper);
        return doctor;
    }


    @Operation(summary = "获取医生信息，进行排版")
    @GetMapping("/findDoctorBySectionPage")
    public ResponseData findDoctorBySectionPage(
            @RequestParam("arrangeDate") String arrangeDate,
            @RequestParam("dSection") String dSection,
            @RequestParam("pageNumber") int pageNumber,
            @RequestParam("query") String query,
            @RequestParam("size") int size
    ) {
        Page<Doctor> pageInfo = new Page<>(pageNumber, size);
        LambdaQueryWrapper<Doctor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Doctor::getdSection, dSection);
        lambdaQueryWrapper.like(query != null && !query.isEmpty(), Doctor::getdName, query);
        lambdaQueryWrapper.orderByDesc(Doctor::getdName);
        doctorService.page(pageInfo, lambdaQueryWrapper);
        return ResponseData.success("成功", pageInfo);
    }


    @GetMapping("/findOrderByNull")
    public Result findOrderByNull(String oStart) {
        List<Orders> list = doctorService.getTodayList(oStart, UserContest.getUserId());
        return Result.success(list);
    }

    @Operation(summary = "查询病人")
    @GetMapping("/findPatientById")
    public Result findPatientById(@RequestParam("pId") int pId) {
        Patient patient = patientClient.findPatientById(pId);
        return Result.success(patient);
    }


    @GetMapping("/updateStar")
    public Result updateStar(int dId, int dStar) {
        LambdaUpdateWrapper<Doctor> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Doctor::getdId, dId)
                .set(Doctor::getdStar, dStar);
        doctorService.update(lambdaUpdateWrapper);
        return Result.success();
    }

}
