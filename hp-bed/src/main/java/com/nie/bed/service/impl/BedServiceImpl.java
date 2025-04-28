package com.nie.bed.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nie.bed.mapper.BedMapper;
import com.nie.bed.service.BedService;
import com.nie.feign.dto.Bed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BedServiceImpl extends ServiceImpl<BedMapper, Bed> implements BedService {
    @Autowired
    private BedMapper bedMapper;

    @Override
    public int addBed(Bed bed) {
        int i = bedMapper.addBad(bed);
        return i;
    }
}
