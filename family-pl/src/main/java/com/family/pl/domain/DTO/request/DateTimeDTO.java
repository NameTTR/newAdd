package com.family.pl.domain.VO.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 名称：DateTimeVO类
 * 功能：用于表示带有日期和时间信息的值对象
 * 作者：Name
 * 日期：2024/6/2 14:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateTimeVO {

    /**
     * 任务ID
     */
    private Long TaskId;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
}