package com.nie.doctor.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.nie.doctor.pojo.Doctor;
import com.nie.feign.dto.Orders;

import java.util.List;

public interface DoctorService extends IService<Doctor> {
    List<Orders> getTodayList(String oStart, int dId);
}
