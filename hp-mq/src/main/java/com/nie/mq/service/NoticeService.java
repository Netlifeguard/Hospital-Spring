package com.nie.mq.service;


import com.nie.feign.dto.Sysmessage;

import java.util.List;

public interface NoticeService {
    void addNotice(Sysmessage sysmessage);

    List<Sysmessage> findAllNotice();

    int removeById(int msgId);
}
