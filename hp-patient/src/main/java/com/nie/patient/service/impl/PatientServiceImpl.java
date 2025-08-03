package com.nie.patient.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nie.patient.mapper.PatientMapper;
import com.nie.patient.pojo.Patient;
import com.nie.patient.service.PatientService;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

}
