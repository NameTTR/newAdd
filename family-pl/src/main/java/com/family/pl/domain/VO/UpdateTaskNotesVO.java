package com.family.pl.domain.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/6/27 16:25
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateTaskNotesVO extends DateTimeVO{
    private String notes;
}