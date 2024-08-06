package com.family.pl.domain.DTO.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * <p>
 * 间隔日期数据传输对象。
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntervalDateDTO {
    /**
     * 开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    /**
     * 截止日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    /**
     * 第几周
     */
    private Integer week;
}