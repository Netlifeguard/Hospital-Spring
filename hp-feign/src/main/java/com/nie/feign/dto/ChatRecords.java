package com.nie.feign.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatRecords {
    private List<AIMessage> messages;
    private String model = "deepseek-chat";
    /**
     * @// TODO: 2025/2/7 开启API的流式传输，可以配合webscoket长连接来使用
     */
//    private boolean stream = true;
}
