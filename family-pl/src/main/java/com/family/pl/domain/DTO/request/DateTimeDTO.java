package com.family.pl.domain.DTO.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * <p>
 * 日期时间数据传输对象类。
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateTimeDTO {

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
}