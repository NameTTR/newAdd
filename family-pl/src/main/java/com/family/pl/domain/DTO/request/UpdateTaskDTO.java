package com.family.pl.domain.VO.request;

import com.family.pl.domain.Task;
import com.family.pl.domain.TaskLabel;
import com.family.pl.domain.TaskRemind;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/7/7 22:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTaskVO {
    /**
     * 任务
     */
    private Task task;
    /**
     * 任务提醒
     */
    private List<TaskRemind> taskReminds;
    /**
     * 任务标签
     */
    private List<TaskLabel> taskLabels;
}