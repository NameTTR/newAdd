package com.family.pl.domain.VO.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/7/10 11:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntervalDateVO {
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