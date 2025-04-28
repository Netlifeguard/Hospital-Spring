package com.nie.feign.dto;

import lombok.Data;

@Data
public class PageDtoTwo {
    private int dId;
    private int pageNumber;
    private int size;
    private int query;
}
