package com.nie.feign.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;


@Data
public class MessageDTO {
    private String msgId;
    private String userId;
    private String content;
    private String sendTime;
}
