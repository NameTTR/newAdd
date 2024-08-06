package com.family.pl.service;

import com.family.pl.domain.TaskRemind;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 针对表【pl_task_remind(任务提醒表)】的数据库操作Service
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
public interface TaskRemindService extends IService<TaskRemind> {
    /**
     * 根据任务ID查询任务提醒列表。
     *
     * @param taskId 任务ID，用于查询与之关联的任务提醒。
     * @return 返回任务提醒的列表，这些提醒都与给定的任务ID相关联。
     */
    List<TaskRemind> selectRemindsByTaskId(Long taskId);
}
