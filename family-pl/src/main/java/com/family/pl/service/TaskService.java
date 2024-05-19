package com.family.pl.service;

import com.family.pl.domain.Task;
import com.baomidou.mybatisplus.extension.service.IService;
import com.family.pl.domain.VO.AddTaskVO;

/**
* @author 名字
* @description 针对表【pl_task(任务表)】的数据库操作Service
* @createDate 2024-05-19 21:01:45
*/
public interface TaskService extends IService<Task> {

    int addTask(AddTaskVO addTaskVO);
}
