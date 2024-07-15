package com.family.re.service;

import com.family.re.domain.po.RePool;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.job.TaskException;
import org.quartz.SchedulerException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 奖品池表 服务类
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-03
 */
public interface IRePoolService extends IService<RePool> {


    /**
     * 删除奖品池列表
     * @param prizePoolId 奖品池id
     * @return 删除信息
     */
    Integer deletePrize(Long prizePoolId);

    /**
     * 添加奖品池
     * @param pool 奖品池类，传递用户需要填写的数据
     * @return 添加信息
     */
    AjaxResult addPrizePool(RePool pool);

    /**
     * 修改奖品池
     * @param rePool 奖品池类，传递用户需要修改的数据
     * @return 修改信息
     */
    AjaxResult revisionPool(RePool rePool);

    //定时执行的模块(未开启)
    void reJob() throws SchedulerException, TaskException;

    //定时执行添加的模块(未开启)
    void addReachPool();
}
