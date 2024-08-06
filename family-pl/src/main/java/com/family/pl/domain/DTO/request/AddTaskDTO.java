package com.family.pl.domain.VO.request;

import com.family.pl.domain.TaskLabel;
import com.family.pl.domain.TaskRemind;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.family.pl.domain.Task;

import java.util.List;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/7/6 23:54
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddTaskVO {
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

    /**
     * 子任务
     */
    private List<AddTaskVO> addTaskVOS;
}
