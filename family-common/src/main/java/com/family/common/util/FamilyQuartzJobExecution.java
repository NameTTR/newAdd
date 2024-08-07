package com.family.common.util;

import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.util.AbstractQuartzJob;
import com.ruoyi.quartz.util.JobInvokeUtil;
import com.ruoyi.quartz.util.QuartzJobExecution;
import org.quartz.JobExecutionContext;

/**
 * <p>
 * 定时任务处理（允许并发执行）
 * </p>
 *
 * @author Name
 * @date 2024/8/7 18:16
 */
public class FamilyQuartzJobExecution extends AbstractQuartzJob {
    @Override
    protected void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception
    {
        FamilyJobInvokeUtil.invokeMethod(sysJob, context);
    }
}