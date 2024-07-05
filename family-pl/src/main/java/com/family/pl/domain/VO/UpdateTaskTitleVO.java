package com.family.pl.domain.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/6/27 15:36
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateTaskTitleVO extends DateTimeVO{
    private String title;
}