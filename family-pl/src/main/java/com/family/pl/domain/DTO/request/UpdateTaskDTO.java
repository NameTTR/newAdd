package com.family.pl.domain.DTO.request;

import com.family.pl.domain.Task;
import com.family.pl.domain.TaskLabel;
import com.family.pl.domain.TaskRemind;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>
 * 更新任务的数据传输对象。
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTaskDTO {
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
}