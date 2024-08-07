package com.family.common.task;

import com.ruoyi.common.constant.ScheduleConstants;
import com.ruoyi.quartz.domain.SysJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

/**
 * <p>
 * 需要执行的任务
 * </p>
 *
 * @author Name
 * @date 2024/8/7 18:35
 */
public class Test {
    public void test(JobExecutionContext context) {
        // 获取 JobDetail 对象
        JobDetail jobDetail = context.getJobDetail();

        // 从 JobDetail 中获取 JobDataMap
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        
        String o = (String)jobDataMap.get("name");
        String test = (String)jobDataMap.get("test");
        System.out.println( o + " " + test );

    }

}