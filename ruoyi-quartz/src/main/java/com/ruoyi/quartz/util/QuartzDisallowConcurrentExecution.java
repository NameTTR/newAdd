package com.ruoyi.quartz.util;

import com.ruoyi.common.constant.ScheduleConstants;
import com.ruoyi.common.utils.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import com.ruoyi.quartz.domain.SysJob;
import org.quartz.PersistJobDataAfterExecution;

/**
 * 定时任务处理（禁止并发执行）
 * 
 * @author ruoyi
 *
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class QuartzDisallowConcurrentExecution extends AbstractQuartzJob
{
    @Override
    protected void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception
    {
        JobInvokeUtil.invokeMethod(sysJob, context);
    }
}
