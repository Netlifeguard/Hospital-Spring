package com.nie.patient.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nie.patient.mapper.DoctorMapper;
import com.nie.patient.pojo.Doctor;
import com.nie.patient.service.DoctorService;
import org.springframework.stereotype.Service;

@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {

}
