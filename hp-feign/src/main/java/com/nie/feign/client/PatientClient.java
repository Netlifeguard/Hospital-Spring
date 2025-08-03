package com.nie.feign.client;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nie.feign.dto.PageDTO;
import com.nie.feign.dto.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("hp-patient")
public interface PatientClient {
    @PostMapping("/patient/findAllPatients")
    Page<Patient> findPatients(@RequestBody PageDTO pageDTO);

    @DeleteMapping("/patient/deletePatient")
    boolean deletePatient(@RequestParam("pId") int pId);

    @GetMapping("/patient/findPatientById")
    Patient findPatientById(@RequestParam("pId") int pId);

    @GetMapping("/patient/securityP")
    Patient securityP(@RequestParam("pId") int pId);
}
