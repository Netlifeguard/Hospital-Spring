package com.nie.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nie.admin.pojo.GenderList;
import com.nie.admin.pojo.SectionList;
import com.nie.feign.dto.Orders;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
    @Select("SELECT p_gender  gender, COUNT(o_id)  nums " +
            "FROM patient p " +
            "JOIN orders o ON p.p_id = o.p_id " +
            "GROUP BY p_gender")
    List<GenderList> getGenderNums();

    @Select("SELECT d.d_section section, COUNT(o.d_id)  countNums " +
            "FROM doctor d " +
            "JOIN orders o ON d.d_id = o.d_id " +
            "WHERE o.o_start BETWEEN #{start} AND #{end} " +
            "GROUP BY d.d_section")
    List<SectionList> getSectionNums(String start, String end);

    @Insert("insert into orders (p_id,d_id,o_start) values (#{pId},#{dId},#{oStart})")
    int addOrder(int pId, int dId, String oStart, String arId);

}
