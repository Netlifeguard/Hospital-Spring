package com.nie.patient.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nie.patient.pojo.Patient;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
}
