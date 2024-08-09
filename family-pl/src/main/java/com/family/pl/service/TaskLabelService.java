package com.family.pl.service;

import com.family.pl.domain.TaskLabel;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 针对表【pl_task_label(任务标签表)】的数据库操作Service
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
public interface TaskLabelService extends IService<TaskLabel> {

    /**
     * 根据任务ID查询任务标签列表。
     *
     * @param taskId 任务ID，用于精确匹配任务标签中的任务ID。
     * @return 返回匹配到的任务标签列表。如果不存在匹配的标签，则返回空列表。
     */
    List<TaskLabel> selectLabelsByTaskId(Long taskId);
}
