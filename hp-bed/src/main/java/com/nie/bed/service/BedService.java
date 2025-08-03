package com.nie.bed.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.nie.feign.dto.Bed;

public interface BedService extends IService<Bed> {
    int addBed(Bed bed);
}
