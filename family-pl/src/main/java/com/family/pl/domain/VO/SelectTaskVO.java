package com.family.pl.domain.VO;

import com.family.pl.domain.TaskLabel;
import com.family.pl.domain.TaskRemind;
import com.family.pl.domain.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 名称：任务VO类
 * 功能：用于表示完整的任务信息，包括任务的提醒、标签和子任务
 * 作者：Name
 * 日期：2024/7/5
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SelectTaskVO {
    /**
     * 任务
     */
    private Task task;

    /**
     * 任务提醒
     */
    private List<TaskRemind> TaskReminds;

    /**
     * 任务标签
     */
    private List<TaskLabel> TaskLabels;

    /**
     * 子任务
     */
    private List<SelectChildTaskVO> childTaskVOS;
}