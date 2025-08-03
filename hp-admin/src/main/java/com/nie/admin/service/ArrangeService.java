package com.nie.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nie.feign.dto.Arrange;
import com.nie.feign.dto.Doctor;

import java.util.List;

public interface ArrangeService extends IService<Arrange> {
    boolean isArranged(String arId);

    int insertArrange(Arrange arrange);

    List<Doctor> getArrangeList(String arTime, String dSection);

}
