package com.family.pl.service;

import com.family.pl.domain.PlJob;
import com.family.pl.domain.Task;
import com.baomidou.mybatisplus.extension.service.IService;
import com.family.pl.domain.VO.AddChildTaskVO;
import com.family.pl.domain.VO.AddTaskVO;
import com.ruoyi.common.exception.job.TaskException;
import org.quartz.SchedulerException;

/**
* @author 名字
* @description 针对表【pl_task(任务表)】的数据库操作Service
* @createDate 2024-05-19 21:01:45
*/
public interface TaskService extends IService<Task> {

    int addTask(AddTaskVO addTaskVO) throws SchedulerException, TaskException;

    int addChildTask(AddChildTaskVO addChildTaskVO) throws SchedulerException, TaskException;

    int insertJob(PlJob plJob) throws SchedulerException, TaskException;
}
