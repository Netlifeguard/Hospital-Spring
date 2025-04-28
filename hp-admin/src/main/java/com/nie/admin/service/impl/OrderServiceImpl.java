package com.nie.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nie.admin.mapper.ArrangeMapper;
import com.nie.admin.mapper.OrderMapper;
import com.nie.admin.pojo.GenderList;
import com.nie.admin.pojo.SectionList;
import com.nie.admin.service.OrderService;
import com.nie.feign.dto.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ArrangeMapper arrangeMapperr;

    @Override
    public List<GenderList> getGenderNums() {
        return orderMapper.getGenderNums();
    }

    @Override
    public List<SectionList> getSectionNums(String start, String end) {
        List<SectionList> nums = orderMapper.getSectionNums(start, end);
        return nums;
    }

    @Override
    public boolean addOrder(int pId, int dId, String oStart, String arId) {
        int order = orderMapper.addOrder(pId, dId, oStart, arId);
        String s = arId.substring(6);
        //int arrange = arrangeMapperr.addOrder2(pId, dId, s, arId);
        if (order > 0) {
            return true;
        }
        return false;
    }
}
