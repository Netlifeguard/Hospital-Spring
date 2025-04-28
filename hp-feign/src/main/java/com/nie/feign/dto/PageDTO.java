package com.nie.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageDTO {
    int pageNumber;
    int size;
    String query;

    public PageDTO() {
        // 默认构造函数
        this.pageNumber = 1;
        this.size = 8;
        this.query = ""; // 默认值
    }
}
