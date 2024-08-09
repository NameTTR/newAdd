package com.ruoyi.quartz.util;

import com.ruoyi.common.constant.ScheduleConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.mapper.SysJobMapper;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Name
 * @date 2024/8/8 16:49
 */
@Component
public class QuartzUtil {

    private static SysJobMapper staticJobMapper;

    private static Scheduler staticScheduler;

    @Autowired
    private SysJobMapper jobMapper;

    @Autowired
    private Scheduler scheduler;

    @PostConstruct
    public void init(){
        staticJobMapper = this.jobMapper;
        staticScheduler = this.scheduler;
    }

    /**
     * 创建任务
     *
     * @param job 待创建的任务对象，包含任务的所有属性和信息
     * @param dataMap 与任务相关的数据映射，用于存储任务执行所需的额外数据
     * @return 如果任务创建成功，返回true；否则返回false
     */
    public static boolean createJob(SysJob job, Map<String, Object> dataMap) throws SchedulerException, TaskException {
        // 在数据库中插入新的任务
        int rows = staticJobMapper.insertJob(job);
        // 如果数据库插入操作影响的行数大于0，表示任务成功创建
        if (rows > 0)
        {
            // 在调度器中创建并启动任务
            ScheduleUtils.createScheduleJob(staticScheduler, job, dataMap);
            // 返回true，表示任务创建成功
            return true;
        }
        // 如果任务未成功创建，返回false
        return false;
    }

    /**
     * 创建任务
     * 此方法负责将任务信息插入数据库，并在调度器中创建对应的调度任务
     * 它首先通过插入数据库操作确认任务创建是否成功，若成功则在调度器中创建任务
     *
     * @param job 待创建的任务对象，包含任务的所有信息
     * @return boolean 创建任务是否成功的标志
     */
    public static boolean createJob(SysJob job) throws SchedulerException, TaskException {

        // 插入任务信息到数据库，返回影响的行数
        int rows = staticJobMapper.insertJob(job);
        // 如果数据库插入操作影响的行数大于0，表示任务创建成功
        if (rows > 0)
        {
            // 在调度器中创建任务
            ScheduleUtils.createScheduleJob(staticScheduler, job);
            // 返回成功标志
            return true;
        }
        // 如果任务创建失败，返回失败标志
        return false;
    }

    /**
     * 暂停调度任务
     * 此方法用于将一个正在执行的调度任务暂停它首先更新数据库中任务的状态为暂停状态，
     * 然后在调度器中暂停该任务的执行
     *
     * @param job 要暂停的任务对象包含任务ID和任务组信息
     * @return 如果任务成功暂停，则返回true；如果更新数据库操作影响了0行（即未找到对应任务），则返回false
     */
    public static boolean pauseJob(SysJob job) throws SchedulerException {
        // 获取任务ID和组信息
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();

        // 更新任务状态为暂停
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());

        // 更新数据库中的任务信息
        int rows = staticJobMapper.updateJob(job);

        // 如果数据库更新操作成功，则在调度器中暂停任务
        if (rows > 0)
        {
            staticScheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
            return true;
        }
        return false;
    }

    /**
     * 暂停调度任务
     * 该方法通过任务ID查找并暂停相应的任务如果任务不存在或已经处于暂停状态，则返回false
     *
     * @param jobId 任务的ID
     * @return 暂停任务操作的成功与否，成功返回true，否则返回false
     */
    public static boolean pauseJob(Long jobId) throws SchedulerException {
        // 通过任务ID查询任务信息
        SysJob job = staticJobMapper.selectJobById(jobId);
        // 调用专门的方法来暂停任务，参数是查询到的任务对象
        return pauseJob(job);
    }

    /**
     * 恢复任务
     *
     * @param job 待恢复的任务
     * @return 如果任务恢复成功，则返回true；否则返回false
     */
    public static boolean resumeJob(SysJob job) throws SchedulerException {
        // 获取任务ID
        Long jobId = job.getJobId();
        // 获取任务组
        String jobGroup = job.getJobGroup();
        // 设置任务状态为正常
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        // 更新任务状态
        int rows = staticJobMapper.updateJob(job);
        // 判断是否更新成功
        if (rows > 0)
        {
            // 根据任务ID和组名恢复任务
            staticScheduler.resumeJob(ScheduleUtils.getJobKey(jobId, jobGroup));
            // 返回成功标志
            return true;
        }
        // 返回失败标志
        return false;
    }

    /**
     * 恢复任务
     *
     * @param jobId 待恢复的任务的ID
     * @return 如果任务恢复成功，则返回true；否则返回false
     * @throws SchedulerException 如果在任务恢复过程中出现异常
     */
    public static boolean resumeJob(Long jobId) throws SchedulerException {
        // 通过任务ID查询任务信息
        SysJob job = staticJobMapper.selectJobById(jobId);
        // 调用专门的方法来恢复任务，参数是查询到的任务对象
        return resumeJob(job);
    }

    /**
     * 删除定时任务
     * 此方法负责删除指定的定时任务它首先尝试从数据库中删除任务记录，
     * 如果删除成功（即影响的行数大于0），则从调度器中删除相应的任务
     *
     * @param job 待删除的定时任务对象，包含任务ID和任务组等信息
     * @return 如果任务删除成功，返回true；否则返回false
     */
    public static boolean deleteJob(SysJob job) throws SchedulerException {
        // 获取任务ID
        Long jobId = job.getJobId();
        // 获取任务组
        String jobGroup = job.getJobGroup();
        // 通过ID删除数据库中的任务记录，并返回影响的行数
        int rows = staticJobMapper.deleteJobById(jobId);
        // 如果数据库记录删除成功（即影响的行数大于0）
        if (rows > 0)
        {
            // 从调度器中删除任务
            staticScheduler.deleteJob(ScheduleUtils.getJobKey(jobId, jobGroup));
            // 返回删除成功标志
            return true;
        }
        // 返回删除失败标志
        return false;
    }

    /**
     * 删除定时任务
     * 此方法负责删除指定的定时任务它首先尝试从数据库中删除任务记录，
     * 如果删除成功（即影响的行数大于0），则从调度器中删除相应的任务
     *
     * @param jobId 待删除的定时任务的ID
     * @return 如果任务删除成功，返回true；否则返回false
     */
    public static boolean deleteJob(Long jobId) throws SchedulerException {
        // 通过任务ID查询任务信息
        SysJob job = staticJobMapper.selectJobById(jobId);
        // 调用专门的方法来删除任务，参数是查询到的任务对象
        return deleteJob(job);
    }

}