package com.family.pl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.pl.domain.TaskLabel;
import com.family.pl.service.TaskLabelService;
import com.family.pl.mapper.TaskLabelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 针对表【pl_task_label(任务标签表)】的数据库操作Service实现
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
@Service
public class TaskLabelServiceImpl extends ServiceImpl<TaskLabelMapper, TaskLabel>
        implements TaskLabelService {

    /**
     * 根据任务ID查询任务标签列表。
     *
     * @param taskId 任务ID，用于精确匹配任务标签中的任务ID。
     * @return 返回匹配到的任务标签列表。如果不存在匹配的标签，则返回空列表。
     */
    @Override
    public List<TaskLabel> selectLabelsByTaskId(Long taskId) {
        // 创建LambdaQueryWrapper实例，用于构建查询条件
        LambdaQueryWrapper<TaskLabel> queryWrapper = new LambdaQueryWrapper<>();
        // 设置查询条件为任务标签的taskId等于传入的taskId
        queryWrapper.eq(TaskLabel::getTaskId, taskId);
        // 调用baseMapper的selectList方法，根据查询条件查询并返回任务标签列表
        return baseMapper.selectList(queryWrapper);
    }
}




