package com.nie.netty.pojo;

import lombok.Data;

@Data
public class AIMessage {
    private String role;
    private String content;
    private String sendTime;
}
