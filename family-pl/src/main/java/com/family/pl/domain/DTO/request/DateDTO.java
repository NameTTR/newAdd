package com.family.pl.domain.VO.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/7/11 0:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateVO {
    @JsonFormat(pattern = "yyyy-MM-dd")
    public LocalDate date;
}