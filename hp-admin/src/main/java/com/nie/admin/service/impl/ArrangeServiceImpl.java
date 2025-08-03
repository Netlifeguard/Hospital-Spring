package com.nie.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nie.admin.mapper.ArrangeMapper;
import com.nie.admin.service.ArrangeService;
import com.nie.feign.dto.Arrange;
import com.nie.feign.dto.Doctor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArrangeServiceImpl extends ServiceImpl<ArrangeMapper, Arrange> implements ArrangeService {
    private final ArrangeMapper arrangeMapper;

    @Override
    public boolean isArranged(String arId) {
        LambdaQueryWrapper<Arrange> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Arrange::getArId, arId);
        Arrange arrange1 = this.getOne(lambdaQueryWrapper);
        if (arrange1 == null)
            return true;
        return false;
    }

    @Override
    public int insertArrange(Arrange arrange) {
        return arrangeMapper.insertArrange(arrange);
    }

    @Override
    public List<Doctor> getArrangeList(String arTime, String dSection) {
        return arrangeMapper.getArrangeList(arTime, dSection);
    }
}
