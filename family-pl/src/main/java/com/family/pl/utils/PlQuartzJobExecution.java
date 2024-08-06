package com.family.pl.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.family.pl.constant.TaskConstants;
import com.family.pl.domain.PlJob;
import com.family.pl.domain.Task;
import com.family.pl.domain.TaskRemind;
import com.family.pl.service.TaskService;
import com.ruoyi.common.utils.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 定时任务处理（允许并发执行）
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
@Component
@PersistJobDataAfterExecution
public class PlQuartzJobExecution extends PlAbstractQuartzJob {
    @Autowired
    private TaskService taskService;

    @Override
    protected void doExecute(JobExecutionContext context, PlJob plJob) throws Exception {

        long stime = System.currentTimeMillis();
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        if (jobDataMap.getBoolean(TaskConstants.TASK_SKIP)) {
            jobDataMap.put(TaskConstants.TASK_SKIP, false);
            return;
        }

        //获取taskRemind参数
        Integer remindByTime = (Integer) jobDataMap.get(TaskConstants.TASK_REMIND_TIME);
        Integer remindByDate = (Integer) jobDataMap.get(TaskConstants.TASK_REMIND_DATE);

        //获取taskId
        Long taskId = (Long) jobDataMap.get(TaskConstants.Task_ID);

        //获取上次任务执行时间
        Date fireTime = context.getFireTime();
        //由Date转换为LocalDateTime
        LocalDateTime fireDateTime = TaskDateUtils.DateToLocalDateTime(fireTime);

        //计算该任务按时执行的时间
        LocalDateTime nowTaskDateTime = TaskDateUtils.calculateNowTaskDateTime(fireDateTime, remindByTime, remindByDate);

        //由LocalDateTime转换为LocalDate
        LocalDate nowTaskDate = nowTaskDateTime.toLocalDate();

        //检查数据库中relatedTaskId和taskDate
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getRelatedTaskId, taskId)
                .eq(Task::getIsComplete, TaskConstants.TASK_COMMPLETE)
                .eq(Task::getTaskDate, nowTaskDate);
        Task comTask = taskService.getOne(queryWrapper);
        if (StringUtils.isNotNull(comTask)) {
            return;
        }
        long etime = System.currentTimeMillis();

        System.out.println("\n----------------------------------------------------------\n" + "执行任务Id：" + taskId + "\n任务名称：" + plJob.getJobName()
                + "\n执行时间：" + LocalDateTime.now()
                + "\n耗时：" + (etime - stime) + "ms"
                + "\n----------------------------------------------------------\n");

        PlJobInvokeUtil.invokeMethod(plJob);
    }

}
