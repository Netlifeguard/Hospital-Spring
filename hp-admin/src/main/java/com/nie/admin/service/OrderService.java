package com.nie.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nie.admin.pojo.GenderList;
import com.nie.admin.pojo.SectionList;
import com.nie.feign.dto.Orders;

import java.util.List;

public interface OrderService extends IService<Orders> {
    List<GenderList> getGenderNums();

    List<SectionList> getSectionNums(String start, String end);

    boolean addOrder(int pId, int dId, String oStart, String arId);
}
