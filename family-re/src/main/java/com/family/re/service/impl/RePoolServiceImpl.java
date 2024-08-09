package com.family.re.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.re.constant.RewardConstants;
import com.family.re.domain.po.RePool;
import com.family.re.domain.po.RePoolDetail;
import com.family.re.mapper.RePoolMapper;
import com.family.re.service.IRePoolService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.re.service.IRePrizeReachService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.service.ISysJobService;
import com.ruoyi.quartz.service.impl.SysJobServiceImpl;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;


/**
 * <p>
 * 奖品池表 服务实现类
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-03
 */
@Service
public class RePoolServiceImpl extends ServiceImpl<RePoolMapper, RePool> implements IRePoolService {


    private final IRePrizeReachService rePrizeReachService;
    private final SysJobServiceImpl sysJobServiceImpl;

    public RePoolServiceImpl(IRePrizeReachService rePrizeReachService, SysJobServiceImpl sysJobServiceImpl) {
        this.rePrizeReachService = rePrizeReachService;
        this.sysJobServiceImpl = sysJobServiceImpl;
    }


//    private void upDataRealityConditions() {
//        //获取当前时间
//        LocalDate date = LocalDate.now();
//
//        //查询当天在开始和结束时间之间的奖品池
//        List<RePool> rePools = lambdaQuery()
//                .eq(RePool::getUserId, 1)
//                .ge(RePool::getStarTime,date)
//                .le(RePool::getEndTime,date).list();
//
//        //遍历获取的奖品池
//        for (RePool rePool : rePools) {
//            int cycle = rePool.getRewardCycle();
//            LocalDate start = null;
//            LocalDate end = null;
//            switch (cycle) {
//                case 1: {
//                    //如果是日截的话，开始时间和结束时间都是当天
//                    start = end = LocalDate.now();
//                    break;
//                }
//                case 2:{
//                    //如果是周截的话，开始时间是周一，结束时间是周日
//                    LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
//                    LocalDate sunday = monday.plusWeeks(1).minusDays(1);
//
//                    //如果开始时间小于奖品池的开始时间，就取奖品池的开始时间，否则取开始时间
//                    start = monday.isBefore(rePool.getStarTime())?rePool.getStarTime():monday;
//                    //如果结束时间大于奖品池的结束时间，就取奖品池的结束时间，否则取结束时间
//                    end = sunday.isAfter(rePool.getEndTime())?rePool.getEndTime():sunday;
//                    break;
//                }
//                case 3:{
//                    //如果是月截的话，开始时间是当月的第一天，结束时间是当月的最后一天
//                    LocalDate firstDay = date.with(TemporalAdjusters.firstDayOfMonth());
//                    LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfMonth());
//
//                    //如果开始时间小于奖品池的开始时间，就取奖品池的开始时间，否则取开始时间
//                    start = firstDay.isBefore(rePool.getStarTime())?rePool.getStarTime():firstDay;
//                    //如果结束时间大于奖品池的结束时间，就取奖品池的结束时间，否则取结束时间
//                    end = lastDay.isAfter(rePool.getEndTime())?rePool.getEndTime():lastDay;
//                    break;
//                }
//                case 4:{
//                    //如果是年截的话，开始时间是当年的第一天，结束时间是当年的最后一天
//                    LocalDate firstDay = date.with(TemporalAdjusters.firstDayOfYear());
//                    LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfYear());
//
//                    //如果开始时间小于奖品池的开始时间，就取奖品池的开始时间，否则取开始时间
//                    start = firstDay.isBefore(rePool.getStarTime())?rePool.getStarTime():firstDay;
//                    //如果结束时间大于奖品池的结束时间，就取奖品池的结束时间，否则取结束时间
//                    end = lastDay.isAfter(rePool.getEndTime())?rePool.getEndTime():lastDay;
//                    break;
//                }
//            }
//            //调用方法获取实际完成情况
//            rePool.setRealityConditions(task.getRealityConditions(start,end,rePool.getCreatedUserId()));
//        }
//            updateBatchById(rePools);
//    }


    /**
     * 删除奖品池列表
     * @param prizePoolId 奖品池id
     * @return 删除信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deletePrize(Long prizePoolId) {

        try {
            //删除奖品池里面的奖品
            if(!Db.lambdaUpdate(RePoolDetail.class)
                    .eq(RePoolDetail::getPoolId, prizePoolId).remove()
                    &&Db.lambdaQuery(RePoolDetail.class)
                    .eq(RePoolDetail::getPoolId, prizePoolId).count()>0)
                return RewardConstants.REWARD_POOL_PRIZE_ERROR;
            //删除奖品池
            if(!lambdaUpdate().eq(RePool::getId,prizePoolId).remove())
                return RewardConstants.REWARD_POOL_ERROR;
            return RewardConstants.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("删除奖品池失败");
        }
    }


    /**
     * 添加奖品池
     * @param pool 奖品池类，传递用户需要填写的数据
     * @return 添加信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addPrizePool(RePool pool) {

        try {

            //判断用户输入的数据是否为空
            if(pool.getTitle()==null)
                return RewardConstants.REWARD_POOL_TITLE_ERROR;
            if (pool.getRewardCycle()==null)
                return RewardConstants.REWARD_POOL_CYCLE_ERROR;
            if (pool.getRewardConditions()==null)
                return RewardConstants.REWARD_POOL_REWARD_CONDITIONS_ERROR;
            if (pool.getStarTime()==null)
                return RewardConstants.REWARD_POOL_START_TIME_ERROR;
            if (pool.getEndTime()==null)
                return RewardConstants.REWARD_POOL_END_TIME_ERROR;

            //小孩id
            Long userId=1L;
            //家长id
            Long CreatedUserId=1L;

            //新建一个奖品池
            RePool rePool = getRePool(pool, userId, CreatedUserId);

            //将奖品池添加到数据库
            save(rePool);

            return RewardConstants.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("添加奖品池失败");
        }
    }

    private static RePool getRePool(RePool pool, Long userId, Long CreatedUserId) {

        RePool rePool = new RePool();

        //往新建的奖品池类型里面添加用户输入的数据还有两个默认值
        rePool.setTitle(pool.getTitle());
        rePool.setRewardCycle(pool.getRewardCycle());
        rePool.setRewardConditions(pool.getRewardConditions());
        rePool.setState(pool.getState());
        rePool.setStarTime(pool.getStarTime());
        rePool.setEndTime(pool.getEndTime());
        rePool.setUserId(userId);
        rePool.setCreatedUserId(CreatedUserId);
        return rePool;
    }

    /**
     * 修改奖品池
     * @param rePool 奖品池类，传递用户需要修改的数据
     * @return 修改信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer revisionPool(RePool rePool) {
        try {
            if(!lambdaUpdate().eq(RePool::getId, rePool.getId())
                    .set(RePool::getTitle, rePool.getTitle())
                    .set(RePool::getRewardCycle, rePool.getRewardCycle())
                    .set(RePool::getRewardConditions, rePool.getRewardConditions())
                    .set(RePool::getStarTime, rePool.getStarTime())
                    .set(RePool::getEndTime, rePool.getEndTime())
                    .set(RePool::getState, rePool.getState())
                    .update())
                return RewardConstants.REWARD_POOL_ERROR;
            return RewardConstants.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("修改奖品池失败");
        }
    }


    //定时执行的模块(未开启)
    @Override
    public void reJob() throws SchedulerException, TaskException {

        SysJob job =new SysJob();

        job.setJobName("reJob");
        job.setJobGroup("reJob");
        job.setInvokeTarget("com.family.re.service.impl.ReRunJob.runJob()");
        job.setCronExpression("0 0 1 * * ?");
        job.setMisfirePolicy("1");
        job.setConcurrent("1");
        job.setStatus("0");
        sysJobServiceImpl.insertJob(job);
    }

    //定时执行的模块(未开启)
    @Override
    public void addReachPool() {
        LambdaQueryWrapper<RePool> queryWrapper = new LambdaQueryWrapper<>();
        //查询条件：奖品池中，计划的实际完成情况大于等于奖励条件
        queryWrapper.apply("reality_conditions >= reward_conditions");
        List<RePool> rePools = list(queryWrapper);
        rePrizeReachService.addReachPool(rePools);
    }

}

