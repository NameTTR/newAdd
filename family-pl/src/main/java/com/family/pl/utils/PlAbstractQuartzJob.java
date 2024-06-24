package com.family.pl.utils;

import com.family.pl.domain.PlJob;
import com.family.pl.domain.PlJobLog;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.ScheduleConstants;
import com.ruoyi.common.utils.ExceptionUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 抽象quartz调用
 *
 * @author ruoyi
 */
@PersistJobDataAfterExecution
public abstract class PlAbstractQuartzJob implements Job
{
    private static final Logger log = LoggerFactory.getLogger(PlAbstractQuartzJob.class);

    /**
     * 线程本地变量
     */
    private static ThreadLocal<Date> threadLocal = new ThreadLocal<>();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        PlJob plJob = new PlJob();
        BeanUtils.copyBeanProp(plJob, context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES));
        try
        {
            before(context, plJob);
            if (plJob != null)
            {
                doExecute(context, plJob);
            }
            after(context, plJob, null);
        }
        catch (Exception e)
        {
            log.error("任务执行异常  - ：", e);
            after(context, plJob, e);
        }
    }

    /**
     * 执行前
     *
     * @param context 工作执行上下文对象
     * @param plJob 系统计划任务
     */
    protected void before(JobExecutionContext context, PlJob plJob)
    {
        threadLocal.set(new Date());
    }

    /**
     * 执行后
     *
     * @param context 工作执行上下文对象
     * @param plJob 系统计划任务
     */
    protected void after(JobExecutionContext context, PlJob plJob, Exception e)
    {
        Date startTime = threadLocal.get();
        threadLocal.remove();

        final PlJobLog plJobLog = new PlJobLog();
        plJobLog.setJobName(plJob.getJobName());
        plJobLog.setJobGroup(plJob.getJobGroup());
        plJobLog.setInvokeTarget(plJob.getInvokeTarget());
        plJobLog.setStartTime(startTime);
        plJobLog.setStopTime(new Date());
        long runMs = plJobLog.getStopTime().getTime() - plJobLog.getStartTime().getTime();
        plJobLog.setJobMessage(plJobLog.getJobName() + " 总共耗时：" + runMs + "毫秒");
        if (e != null)
        {
            plJobLog.setStatus(Constants.FAIL);
            String errorMsg = StringUtils.substring(ExceptionUtil.getExceptionMessage(e), 0, 2000);
            plJobLog.setExceptionInfo(errorMsg);
        }
        else
        {
            plJobLog.setStatus(Constants.SUCCESS);
        }

        // 写入数据库当中
        //SpringUtils.getBean(ISysJobLogService.class).addJobLog(sysJobLog);
    }

    /**
     * 执行方法，由子类重载
     *
     * @param context 工作执行上下文对象
     * @param plJob 系统计划任务
     * @throws Exception 执行过程中的异常
     */
    protected abstract void doExecute(JobExecutionContext context, PlJob plJob) throws Exception;
}
