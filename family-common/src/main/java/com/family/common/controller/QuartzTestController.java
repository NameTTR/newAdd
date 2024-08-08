package com.family.common.controller;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.ScheduleConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.mapper.SysJobMapper;
import com.ruoyi.quartz.util.ScheduleUtils;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Name
 * @date 2024/8/7 18:31
 */
@RestController
@RequestMapping("/family/quartz")
public class QuartzTestController {

    @Autowired
    private SysJobMapper jobMapper;

    @Autowired
    private Scheduler scheduler;

    @PostMapping("/test")
    public AjaxResult test(JobExecutionContext context) throws SchedulerException, TaskException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        SysJob sysJob = SysJob.createJob("测试任务", "family-test",  "com.family.common.task.Test.test(org.quartz.JobExecutionContext)","0/5 * * * * ?");
        sysJob.setTimes(3);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", "测试参数");
        dataMap.put("test", "测试");
        int rows = jobMapper.insertJob(sysJob);
        if (rows > 0)
        {
            ScheduleUtils.createScheduleJob(scheduler, sysJob, dataMap);
        }

        return AjaxResult.success();
    }
}