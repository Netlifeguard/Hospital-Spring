package com.nie.feign.client;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nie.feign.dto.Doctor;
import com.nie.feign.dto.PageDTO;
import com.nie.feign.dto.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("hp-doctor")
public interface DoctorClient {
    @PostMapping("/doctor/findAllDoctors")
    Page<Doctor> findDoctors(@RequestBody PageDTO pageDTO);

    @PostMapping("/doctor/addDoctor")
    void addDoctor(@RequestBody Doctor doctor);

    @DeleteMapping("/doctor/deleteDoctor")
    boolean deleteDoctor(@RequestParam("dId") String dId);

    @GetMapping("/doctor/findDoctor")
    Doctor findDoctor(@RequestParam("dId") String dId);

    @PostMapping("/doctor/modifyDoctor")
    boolean modifyDoctor(@RequestBody Doctor doctor);

    @GetMapping("/doctor/securityD")
    Doctor securityD(@RequestParam("dId") int dId);
}
