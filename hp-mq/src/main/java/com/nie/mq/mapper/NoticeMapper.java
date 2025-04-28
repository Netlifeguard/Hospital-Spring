package com.nie.mq.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.nie.feign.dto.Sysmessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper extends BaseMapper<Sysmessage> {

}
