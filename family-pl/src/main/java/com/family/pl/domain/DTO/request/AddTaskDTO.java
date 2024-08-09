package com.family.pl.domain.DTO.request;

import com.family.pl.domain.TaskLabel;
import com.family.pl.domain.TaskRemind;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.family.pl.domain.Task;

import java.util.List;

/**
 * <p>
 * 数据传输对象，用于添加任务的场景。
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-6
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddTaskDTO {
    /**
     * 任务
     */
    private Task task;

    /**
     * 任务提醒
     */
    private List<TaskRemind> taskRemindList;

    /**
     * 任务标签
     */
    private List<TaskLabel> taskLabelList;

    /**
     * 子任务
     */
    private List<AddTaskDTO> childTaskDTOList;
}
