package com.family.pl.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.family.pl.constant.TaskConstants;
import com.family.pl.domain.PlJob;
import com.family.pl.domain.Task;
import com.family.pl.service.TaskService;
import com.ruoyi.common.utils.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.annotation.Resources;
import java.text.SimpleDateFormat;
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
            context.getScheduler().addJob(context.getJobDetail(), true, true);
            return;
        }

        // 获取任务
        JobDetail jobDetail = context.getJobDetail();
        Long jobId = PlScheduleUtils.getJobId(jobDetail);
        Integer remindTime = PlScheduleUtils.getRemindByTime(jobDetail);
        Task task = taskService.getById(jobId);

        // 获取上次任务执行时间
        Date previousFireTime = context.getTrigger().getPreviousFireTime();
        Date startTime = null;
        // 上次任务执行时间加上remainderTime和remainderDate
        if (previousFireTime != null) {
            startTime = TaskDateUtils.PlusTimeReminder(previousFireTime, remindTime);
            startTime = TaskDateUtils.PlusDateReminder(startTime, remindTime);
        }
        // 将startTime格式化为yyyy-MM-dd
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdfDate.format(startTime);

        // 格式化为 HH:mm:ss
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        String time = sdfTime.format(startTime);

        // 查询数据库中是否有该任务
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotNull(task.getTaskTimeBegin())) {
            List<Map> list = new ArrayList<>();
            queryWrapper.eq(Task::getTaskDate, date).eq(Task::getTaskTimeBegin, time)
                    .eq(Task::getIsComplete, TaskConstants.TASK_COMMPLETE)
                    .eq(Task::getId, jobId);
            Task one = taskService.getOne(queryWrapper);
            if (StringUtils.isNotNull(one)) {
                return;
            }
        } else {
            queryWrapper.eq(Task::getTaskDate, date).eq(Task::getIsComplete, TaskConstants.TASK_COMMPLETE)
                    .eq(Task::getId, jobId);
            Task one = taskService.getOne(queryWrapper);
            if (StringUtils.isNotNull(one)) {
                return;
            }
        }

        PlJobInvokeUtil.invokeMethod(plJob);
    }

}
