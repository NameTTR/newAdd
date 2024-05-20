package com.family.pl.utils;

import com.family.pl.domain.PlJob;
import org.quartz.JobExecutionContext;

/**
 * 定时任务处理（允许并发执行）
 * 
 * @author ruoyi
 *
 */
public class PlQuartzJobExecution extends PlAbstractQuartzJob
{
    @Override
    protected void doExecute(JobExecutionContext context, PlJob plJob) throws Exception
    {
        PlJobInvokeUtil.invokeMethod(plJob);
    }
}
