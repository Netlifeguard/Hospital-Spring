package com.nie.patient.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nie.patient.mapper.OrderMapper;
import com.nie.patient.pojo.Orders;
import com.nie.patient.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

}
