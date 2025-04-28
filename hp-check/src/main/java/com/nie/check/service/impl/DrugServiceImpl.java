package com.nie.check.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nie.check.mapper.DrugMapper;
import com.nie.check.service.DrugService;
import com.nie.feign.dto.Drug;
import org.springframework.stereotype.Service;

@Service
public class DrugServiceImpl extends ServiceImpl<DrugMapper, Drug> implements DrugService {

}
