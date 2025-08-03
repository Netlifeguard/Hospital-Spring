package com.nie.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArrangeDTO {
    String arrangeDate;
    String dSection;

    int pageNumber;

    String query;

    int size;

    ArrangeDTO() {

    }
}
