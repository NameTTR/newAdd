package com.family.pl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.pl.domain.TaskLabel;
import com.family.pl.service.TaskLabelService;
import com.family.pl.mapper.TaskLabelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 名字
* @description 针对表【pl_task_label(任务标签表)】的数据库操作Service实现
* @createDate 2024-07-05 17:05:16
*/
@Service
public class TaskLabelServiceImpl extends ServiceImpl<TaskLabelMapper, TaskLabel>
    implements TaskLabelService{

    @Override
    public List<TaskLabel> selectLabelsByTaskId(Long taskId) {
        LambdaQueryWrapper<TaskLabel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskLabel::getTaskId, taskId);
        return baseMapper.selectList(queryWrapper);
    }
}




