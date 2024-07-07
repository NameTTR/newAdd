package com.family.pl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.family.pl.domain.Task;
import com.baomidou.mybatisplus.extension.service.IService;
import com.family.pl.domain.VO.AddTaskVO;
import com.family.pl.domain.VO.DateTimeVO;
import com.family.pl.domain.VO.SelectTaskVO;
import com.family.pl.domain.VO.UpdateTaskVO;
import com.ruoyi.common.exception.job.TaskException;
import org.quartz.SchedulerException;

import java.time.LocalDate;
import java.util.Map;

/**
* @author 名字
* @description 针对表【pl_task(任务表)】的数据库操作Service
* @createDate 2024-07-05 17:05:16
*/
public interface TaskService extends IService<Task> {

    IPage<SelectTaskVO> selectCompleteTasks(Integer pageNum, DateTimeVO dateTimeVO);

    IPage<SelectTaskVO> selectUncompleteTasks(Integer pageNum, DateTimeVO dateTimeVO);

    SelectTaskVO selectTaskById(Long taskId);

    int addTask(AddTaskVO addTaskVO) throws SchedulerException, TaskException;

    int comTask(DateTimeVO dateTimeVO);

    Double taskCompletion(LocalDate startDate, LocalDate endDate, Long userId);

    int delTask(Long taskId);

    int unComTask(Long taskId) throws SchedulerException, TaskException;

    int updateTask(UpdateTaskVO updateTaskVO) throws SchedulerException, TaskException;
}
