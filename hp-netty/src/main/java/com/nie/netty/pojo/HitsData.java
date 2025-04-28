package com.nie.netty.pojo;

import com.nie.feign.dto.MessageDTO;
import lombok.Data;

import java.util.List;

@Data
public class HitsData {
    long total;
    List<MessageDTO> hits;
}
