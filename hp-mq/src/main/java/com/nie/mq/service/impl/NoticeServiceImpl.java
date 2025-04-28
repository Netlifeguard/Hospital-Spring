package com.nie.mq.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nie.common.tools.RedisKeyPrefix;
import com.nie.common.tools.RedisOPS;
import com.nie.feign.dto.Sysmessage;
import com.nie.mq.mapper.NoticeMapper;
import com.nie.mq.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Sysmessage> implements NoticeService {
    private final NoticeMapper noticeMapper;
    private final RedisOPS redisOPS;

    @Override
    public void addNotice(Sysmessage sysmessage) {
        baseMapper.insert(sysmessage);
    }

    @Override
    public List<Sysmessage> findAllNotice() {
        LambdaQueryWrapper<Sysmessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select();
        final List<Sysmessage> sysmessages = baseMapper.selectList(queryWrapper);

        return sysmessages;
    }

    @Override
    public int removeById(int msgId) {
        return baseMapper.deleteById(msgId);
    }
}
