package com.nie.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.Gson;
import com.nie.admin.pojo.Admin;
import com.nie.admin.service.AdminService;
import com.nie.admin.service.OrderService;
import com.nie.common.tools.*;
import com.nie.feign.client.AuthClient;
import com.nie.feign.client.CheckClient;
import com.nie.feign.client.DoctorClient;
import com.nie.feign.client.PatientClient;
import com.nie.feign.dto.Doctor;
import com.nie.feign.dto.Orders;
import com.nie.feign.dto.PageDTO;
import com.nie.feign.dto.Patient;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    private final DoctorClient doctorClient;

    private final PatientClient patientClient;

    private final OrderService orderService;

    private final CheckClient checkClient;

    private final StringRedisTemplate stringRedisTemplate;

    private final RateLimiter rateLimiter = RateLimiter.create(5.0);

    private final AuthClient authClient;

    private final RedisOPS redisOPS;


    @GetMapping("/bucket")
    public void bucket() {
        rateLimiter.acquire();
        System.out.println(666);
    }


    @PostMapping("/login")
    public ResponseData adminLogin(
            @RequestParam("Id") int Id,
            @RequestParam("Password") String Password,
            @RequestParam("captcha") String captcha,
            @RequestParam("role") String role
    ) {

        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Admin::getAId, Id);
        lambdaQueryWrapper.eq(Admin::getAPassword, Password);
        Admin user = adminService.getOne(lambdaQueryWrapper);
        final Gson gson = new Gson();
        final String userjson = gson.toJson(user);

        final Map<String, String> login = authClient.login(Id, Password, captcha, role, userjson);


        if (login != null && !login.isEmpty()) {
            return ResponseData.success("登录成功", login);
        }
        return ResponseData.fail("登录失败，密码或账号或验证码错误");

    }

    @GetMapping("/logout")
    public Result logout() {
        final boolean b = redisOPS.deleteObject(RedisKeyPrefix.Logged_in_admin);
        if (b) {
            log.info("exit success");
            return Result.success("退出成功");
        }
        return Result.success("退出成功");
    }


    @PostMapping("/findAllDoctors")
    public Result findAllDoctors(@RequestBody PageDTO pageDTO) {

        Page<Doctor> pageInfo = doctorClient.findDoctors(pageDTO);

        return Result.success(pageInfo);
    }

    @PostMapping("/findAllPatients")
    public ResponseData findDoctors(@RequestBody PageDTO pageDTO) {

        Page<Patient> pageInfo = patientClient.findPatients(pageDTO);
        return ResponseData.success("所有病人", pageInfo);
    }

    @PostMapping("/addDoctor")
    public Result addDoctor(@RequestBody Doctor doctor) {
        System.out.println(doctor);
        doctorClient.addDoctor(doctor);
        return Result.success("新增成功");
    }

    @PostMapping("/modifyDoctor")
    public Result modifyDoctor(@RequestBody Doctor doctor) {
        boolean b = doctorClient.modifyDoctor(doctor);
        if (b)
            return Result.success("修改成功");
        return Result.success("修改失败");
    }

    @DeleteMapping("/deleteDoctor")
    public Result deleteDoctor(@RequestParam("dId") String dId) {
        boolean b = doctorClient.deleteDoctor(dId);
        if (b)
            return Result.success("成功删除");
        return Result.error("该医生未交接，不允许删除");
    }

    @GetMapping("/findDoctor")
    public Result findDoctor(@RequestParam("dId") String dId) {
        Doctor doctor = doctorClient.findDoctor(dId);
        return Result.success(doctor);
    }


    @DeleteMapping("/deletePatient")
    public Result deletePatient(@RequestParam("pId") int pId) {
        boolean patient = patientClient.deletePatient(pId);
        if (patient)
            return Result.success("成功删除");
        return Result.success("删除失败");
    }

    @PostMapping("/findAllOrders")
    public Result findAllOrders(@RequestBody PageDTO pageDTO) {
        Page<Orders> pageInfo = new Page<>(pageDTO.getPageNumber(), pageDTO.getSize());
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(pageDTO.getQuery() != null && !pageDTO.getQuery().isEmpty(), Orders::getPId, pageDTO.getQuery());
        orderService.page(pageInfo, lambdaQueryWrapper);
        return Result.success(pageInfo);
    }

    @GetMapping("/deleteOrder")
    public Result deleteOrder(int oId) {
        orderService.removeById(oId);
        return Result.success();
    }


}
