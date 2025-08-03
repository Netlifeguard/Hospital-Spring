package com.nie.doctor.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nie.doctor.mapper.DoctorMapper;
import com.nie.doctor.pojo.Doctor;
import com.nie.doctor.service.DoctorService;
import com.nie.feign.dto.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {

    @Autowired
    private DoctorMapper doctorMapper;

    @Override
    public List<Orders> getTodayList(String oStart, int dId) {
        return doctorMapper.getTodayList(oStart, dId);
    }
}
