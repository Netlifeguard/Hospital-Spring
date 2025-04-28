package com.nie.doctor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nie.doctor.pojo.Doctor;
import com.nie.feign.dto.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {
    @Select("SELECT o.o_id,o.p_id,p.p_name,d.d_name,o.o_start" +
            " from orders o " +
            " join doctor d on o.d_id=d.d_id" +
            " join patient p on o.p_id=p.p_id" +
            " where o.o_start" +
            " like CONCAT(#{oStart},'%')" +
            " and o.d_id = #{dId}")
    List<Orders> getTodayList(String oStart, int dId);
}
