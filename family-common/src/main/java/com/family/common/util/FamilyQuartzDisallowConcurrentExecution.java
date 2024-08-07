package com.family.common.util;

import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.util.AbstractQuartzJob;
import com.ruoyi.quartz.util.QuartzDisallowConcurrentExecution;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

/**
 * <p>
 * 定时任务处理（禁止并发执行）
 * </p>
 *
 * @author Name
 * @date 2024/8/7 18:16
 */
@DisallowConcurrentExecution
public class FamilyQuartzDisallowConcurrentExecution extends AbstractQuartzJob {
    @Override
    protected void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception
    {
        FamilyJobInvokeUtil.invokeMethod(sysJob, context);
    }
}