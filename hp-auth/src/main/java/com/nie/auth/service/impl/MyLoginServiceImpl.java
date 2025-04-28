package com.nie.auth.service.impl;

import com.nie.auth.pojo.MyLogin;
import com.nie.auth.mapper.MyLoginMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nie.auth.service.IMyLoginService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author nie
 * @since 2025-02-03
 */
@Service
public class MyLoginServiceImpl extends ServiceImpl<MyLoginMapper, MyLogin> implements IMyLoginService {

}
