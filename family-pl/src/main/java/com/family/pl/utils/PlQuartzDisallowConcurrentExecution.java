package com.family.pl.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.family.pl.constant.TaskConstants;
import com.family.pl.domain.PlJob;
import com.family.pl.domain.Task;
import com.family.pl.service.TaskService;
import com.ruoyi.common.utils.StringUtils;
import org.quartz.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 定时任务处理（禁止并发执行）
 *
 * @author ruoyi
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class PlQuartzDisallowConcurrentExecution extends PlAbstractQuartzJob {
    @Resource
    private TaskService taskService;

    @Override
    protected void doExecute(JobExecutionContext context, PlJob plJob) throws Exception {
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

        PlJobInvokeUtil.invokeMethod(plJob);
    }

}
