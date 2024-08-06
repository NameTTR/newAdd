package com.family.pl.utils;

import com.family.pl.constant.TaskConstants;
import com.family.pl.domain.PlJob;
import com.ruoyi.common.constant.ScheduleConstants;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.common.exception.job.TaskException.Code;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import org.quartz.*;

import java.text.ParseException;
import java.time.LocalDateTime;

/**
 * <p>
 * 定时任务工具类
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-5
 */
public class PlScheduleUtils {
    /**
     * 得到quartz任务类
     *
     * @param plJob 执行计划
     * @return 具体执行任务类
     */
    private static Class<? extends Job> getQuartzJobClass(PlJob plJob) {
        boolean isConcurrent = "0".equals(plJob.getConcurrent());
        return isConcurrent ? PlQuartzJobExecution.class : PlQuartzDisallowConcurrentExecution.class;
    }

    /**
     * 构建任务触发对象
     */
    public static TriggerKey getTriggerKey(Long jobId, String jobGroup) {
        return TriggerKey.triggerKey(ScheduleConstants.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 构建任务触发对象
     */
    public static TriggerKey getTriggerKey(Long jobId) {
        return TriggerKey.triggerKey(ScheduleConstants.TASK_CLASS_NAME + jobId);
    }

    /**
     * 构建任务键对象
     */
    public static JobKey getJobKey(Long jobId, String jobGroup) {
        return JobKey.jobKey(ScheduleConstants.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 构建任务键对象
     */
    public static JobKey getJobKey(Long jobId) {
        return JobKey.jobKey(ScheduleConstants.TASK_CLASS_NAME + jobId);
    }

    /**
     * 创建定时任务
     */
    public static void createScheduleJob(Scheduler scheduler, PlJob job) throws SchedulerException, TaskException, ParseException {
        Long startTime = System.currentTimeMillis();
        Class<? extends Job> jobClass = getQuartzJobClass(job);
        // 构建job信息
        Long jobId = job.getJobId();
        JobDetail jobDetail = JobBuilder.newJob(jobClass).storeDurably().withIdentity(getJobKey(jobId)).build();

        // 放入参数，运行时的方法可以获取
        jobDetail.getJobDataMap().put(TaskConstants.TASK_SKIP, false);
        jobDetail.getJobDataMap().put(ScheduleConstants.TASK_PROPERTIES, job);
        jobDetail.getJobDataMap().put(TaskConstants.TASK_REMIND_TIME, job.getRemindByTime());
        jobDetail.getJobDataMap().put(TaskConstants.TASK_REMIND_DATE, job.getRemindByDate());
        jobDetail.getJobDataMap().put(TaskConstants.Task_ID, job.getTaskId());

        // 表达式调度构建器
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
        cronScheduleBuilder = handleCronScheduleMisfirePolicy(job, cronScheduleBuilder);

        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();

        TaskDateUtils.calculateStartTime(job.getStartTime(), job.getRemindByDate(), job.getRemindByDate());
        // 判断是否存在
        if (scheduler.checkExists(getJobKey(jobId))) {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(getJobKey(jobId));
        }


        // 按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = null;
        if (StringUtils.isNotNull(job.getRepeatEnd())) {
            trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(jobId))
                    .endAt(job.getRepeatEnd())
                    .startAt(job.getStartTime())
                    .withSchedule(cronScheduleBuilder).build();
        } else {
            trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(jobId))
                    .startAt(job.getStartTime())
                    .withSchedule(cronScheduleBuilder).build();
        }

        // 判断任务是否过期
        if (StringUtils.isNotNull(PlCronUtils.getNextExecution(job.getCronExpression()))) {
            // 执行调度任务
            scheduler.scheduleJob(jobDetail, trigger);
        }

        // 暂停任务
        if (job.getStatus().equals(ScheduleConstants.Status.PAUSE.getValue())) {
            scheduler.pauseJob(PlScheduleUtils.getJobKey(jobId));
        }
        Long endTime = System.currentTimeMillis();
        System.out.println(
                "\n**************************************\n"
                + "\n任务创建耗时："
                + (endTime - startTime)
                + "ms\n"
                + "**************************************\n");
    }

    /**
     * 设置定时任务策略
     */
    public static CronScheduleBuilder handleCronScheduleMisfirePolicy(PlJob job, CronScheduleBuilder cb)
            throws TaskException {
        switch (job.getMisfirePolicy()) {
            case ScheduleConstants.MISFIRE_DEFAULT:
                return cb;
            case ScheduleConstants.MISFIRE_IGNORE_MISFIRES:
                return cb.withMisfireHandlingInstructionIgnoreMisfires();
            case ScheduleConstants.MISFIRE_FIRE_AND_PROCEED:
                return cb.withMisfireHandlingInstructionFireAndProceed();
            case ScheduleConstants.MISFIRE_DO_NOTHING:
                return cb.withMisfireHandlingInstructionDoNothing();
            default:
                throw new TaskException("The task misfire policy '" + job.getMisfirePolicy()
                        + "' cannot be used in cron schedule tasks", Code.CONFIG_ERROR);
        }
    }

    /**
     * 检查包名是否为白名单配置
     *
     * @param invokeTarget 目标字符串
     * @return 结果
     */
    public static boolean whiteList(String invokeTarget) {
        String packageName = StringUtils.substringBefore(invokeTarget, "(");
        int count = StringUtils.countMatches(packageName, ".");
        if (count > 1) {
            return StringUtils.containsAnyIgnoreCase(invokeTarget, TaskConstants.JOB_WHITELIST_STR);
        }
        Object obj = SpringUtils.getBean(StringUtils.split(invokeTarget, ".")[0]);
        String beanPackageName = obj.getClass().getPackage().getName();
        return StringUtils.containsAnyIgnoreCase(beanPackageName, TaskConstants.JOB_WHITELIST_STR)
                && !StringUtils.containsAnyIgnoreCase(beanPackageName, TaskConstants.JOB_ERROR_STR);
    }

    /**
     * 获取任务的jobId
     *
     * @param jobDetail 任务
     * @return 执行类
     */
    public static Long getJobId(JobDetail jobDetail) {
        return Long.valueOf(jobDetail.getKey().getName().replace(ScheduleConstants.TASK_CLASS_NAME, ""));
    }

    /**
     * 获取任务的remindByTime
     *
     * @param jobDetail 任务
     * @return 提醒时间
     */
    public static Integer getRemindByTime(JobDetail jobDetail) {
        return jobDetail.getJobDataMap().getInt(TaskConstants.TASK_REMIND_TIME);
    }

    /**
     * 获取任务的remindByDate
     *
     * @param jobDetail 任务
     * @return 提醒时间
     */
    public static Integer getRemindByDate(JobDetail jobDetail) {
        return jobDetail.getJobDataMap().getInt(TaskConstants.TASK_REMIND_DATE);
    }
}
