package com.family.pl.service;

import com.family.pl.domain.PlJob;
import com.family.pl.domain.Task;
import com.baomidou.mybatisplus.extension.service.IService;
import com.family.pl.domain.VO.AddChildTaskVO;
import com.family.pl.domain.VO.AddTaskVO;
import com.family.pl.domain.VO.DateTimeVO;
import com.ruoyi.common.exception.job.TaskException;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 名字
* @description 针对表【pl_task(任务表)】的数据库操作Service
* @createDate 2024-05-19 21:01:45
*/
@Service
public interface TaskService extends IService<Task> {

    /**
     * 添加任务
     *
     * @param addTaskVO
     * @return
     * @throws SchedulerException
     * @throws TaskException
     */
    int addTask(AddTaskVO addTaskVO) throws SchedulerException, TaskException;

    /**
     * 添加子任务
     *
     * @param addChildTaskVO
     * @return
     * @throws SchedulerException
     * @throws TaskException
     */
    int addChildTask(AddChildTaskVO addChildTaskVO) throws SchedulerException, TaskException;

    /**
     * 添加定时任务
     *
     * @param plJob
     * @return
     * @throws SchedulerException
     * @throws TaskException
     */
    int insertJob(PlJob plJob) throws SchedulerException, TaskException;

//    int deleteByID(Long id) throws SchedulerException;

    /**
     * 完成任务
     *
     * @param dateTimeVO
     * @return
     * @throws SchedulerException
     */
    int taskCompleteById(DateTimeVO dateTimeVO) throws SchedulerException;

    /**
     * 由完成的任务转为未完成的任务
     *
     * @param dateTimeVO
     * @return
     * @throws SchedulerException
     */
    int taskDisCompleteById(DateTimeVO dateTimeVO) throws SchedulerException;

    /**
     * 根据日期查询该日期任务
     *
     * @param dateTimeVO
     * @return 该日期任务
     */
    List<Task> selectTasks(DateTimeVO dateTimeVO);

//    List<Task> listAllCompletedTasks();
}
