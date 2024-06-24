package com.family.pl.domain.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/6/2 14:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateTimeVO {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date time;
}