package com.family.pl.utils;

import com.family.pl.domain.PlJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

/**
 * 定时任务处理（禁止并发执行）
 * 
 * @author ruoyi
 *
 */
@DisallowConcurrentExecution
public class PlQuartzDisallowConcurrentExecution extends PlAbstractQuartzJob
{
    @Override
    protected void doExecute(JobExecutionContext context, PlJob plJob) throws Exception
    {
        PlJobInvokeUtil.invokeMethod(plJob);
    }
}
