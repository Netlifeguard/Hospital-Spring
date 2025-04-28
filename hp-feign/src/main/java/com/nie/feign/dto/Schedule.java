package com.nie.feign.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Schedule {
    @TableId // 表示主键
    private Long id; // 自增主键

    private String departmentId; // 科室编号

    private LocalDate orderDate; // 挂号日期

    private Integer eTOn; // 08:30-09:30 剩余票数

    private Integer nTOt; // 09:30-10:30 剩余票数

    private Integer tTOe; // 10:30-11:30 剩余票数

    private Integer fTOf; // 14:30-15:30 剩余票数

    private Integer fTOs; // 15:30-16:30 剩余票数

    private Integer sTOs; // 16:30-17:30 剩余票数

    private LocalDateTime createdAt; // 创建时间

    private LocalDateTime updatedAt; // 更新时间
}
