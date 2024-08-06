package com.family.pl.domain.DTO.response;

import com.family.pl.domain.TaskLabel;
import com.family.pl.domain.TaskRemind;
import com.family.pl.domain.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>
 * 用于表示选择子任务的数据传输对象。
 * </p>
 * @author 高俊炜
 * @since 2024-7-9
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SelectTaskDTO {
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
    private List<SelectChildTaskDTO> childTaskDTOList;
}