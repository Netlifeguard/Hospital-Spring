package com.nie.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nie.feign.dto.Arrange;
import com.nie.feign.dto.Doctor;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArrangeMapper extends BaseMapper<Arrange> {
    @Insert("INSERT INTO arrange (ar_id, ar_time, d_id) VALUES (#{arId}, #{arTime}, #{dId})")
    int insertArrange(Arrange arrange);

    @Select("select * from doctor d join arrange a" +
            " on d.d_id = a.d_id" +
            " where a.ar_time = #{arTime} and a.d_id in" +
            " (select d.d_id from doctor d where d.d_section = #{dSection})")
    List<Doctor> getArrangeList(@Param("arTime") String arTime, @Param("dSection") String dSection);

    @Insert("insert into arrange (ar_id,ar_time,d_id) values (#{arId},#{oStart},#{dId})")
    int addOrder2(int pId, int dId, String oStart, String arId);

}
