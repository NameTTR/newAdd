package com.family.pl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.pl.domain.TaskRemind;
import com.family.pl.service.TaskRemindService;
import com.family.pl.mapper.TaskRemindMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 针对表【pl_task_remind(任务提醒表)】的数据库操作Service实现
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
@Service
public class TaskRemindServiceImpl extends ServiceImpl<TaskRemindMapper, TaskRemind>
        implements TaskRemindService {

    /**
     * 根据任务ID查询任务提醒列表。
     *
     * @param taskId 任务ID，用于查询与之关联的任务提醒。
     * @return 返回任务提醒的列表，这些提醒都与给定的任务ID相关联。
     */
    @Override
    public List<TaskRemind> selectRemindsByTaskId(Long taskId) {
        // 创建Lambda查询包装器，用于构建查询条件。
        LambdaQueryWrapper<TaskRemind> queryWrapper = new LambdaQueryWrapper<>();
        // 设置查询条件：任务提醒的任务ID等于传入的taskId。
        queryWrapper.eq(TaskRemind::getTaskId, taskId);
        // 根据构建的查询条件，查询并返回满足条件的任务提醒列表。
        return baseMapper.selectList(queryWrapper);
    }
}




