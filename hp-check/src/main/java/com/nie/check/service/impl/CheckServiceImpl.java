package com.nie.check.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.nie.check.mapper.CheckMapper;
import com.nie.check.service.CheckService;
import com.nie.feign.dto.Checks;
import org.springframework.stereotype.Service;

@Service
public class CheckServiceImpl extends ServiceImpl<CheckMapper, Checks> implements CheckService {

}
