package com.nie.check.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nie.feign.dto.Checks;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CheckMapper extends BaseMapper<Checks> {
}
