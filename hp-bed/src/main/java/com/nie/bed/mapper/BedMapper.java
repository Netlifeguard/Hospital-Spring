package com.nie.bed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nie.feign.dto.Bed;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BedMapper extends BaseMapper<Bed> {
    @Insert("INSERT INTO bed (b_id, p_id, b_state, b_start, d_id, b_reason, version) " +
            "VALUES (#{bId}, #{pId}, #{bState}, #{bStart}, #{dId}, #{bReason}, #{version})")
    int addBad(Bed bed);


}
