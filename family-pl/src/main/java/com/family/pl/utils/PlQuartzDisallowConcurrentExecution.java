package com.family.pl.utils;

import com.family.pl.constant.TaskConstants;
import com.family.pl.domain.PlJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;

/**
 * 定时任务处理（禁止并发执行）
 * 
 * @author ruoyi
 *
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class PlQuartzDisallowConcurrentExecution extends PlAbstractQuartzJob
{
    @Override
    protected void doExecute(JobExecutionContext context, PlJob plJob) throws Exception
    {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        if(jobDataMap.getBoolean(TaskConstants.TASK_SKIP)){
            jobDataMap.put(TaskConstants.TASK_SKIP, false);
            context.getScheduler().addJob(context.getJobDetail(), true, true);
            return;
        }
        PlJobInvokeUtil.invokeMethod(plJob);
    }
}
