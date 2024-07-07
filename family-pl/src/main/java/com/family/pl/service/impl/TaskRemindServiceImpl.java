package com.family.pl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.pl.domain.TaskRemind;
import com.family.pl.service.TaskRemindService;
import com.family.pl.mapper.TaskRemindMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 名字
* @description 针对表【pl_task_remind(任务提醒表)】的数据库操作Service实现
* @createDate 2024-07-05 17:05:16
*/
@Service
public class TaskRemindServiceImpl extends ServiceImpl<TaskRemindMapper, TaskRemind>
    implements TaskRemindService{

    @Override
    public List<TaskRemind> selectRemindsByTaskId(Long taskId) {
        LambdaQueryWrapper<TaskRemind> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskRemind::getTaskId, taskId);
        return baseMapper.selectList(queryWrapper);
    }
}




