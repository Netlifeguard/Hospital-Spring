package com.nie.check.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nie.feign.dto.Drug;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DrugMapper extends BaseMapper<Drug> {
}
