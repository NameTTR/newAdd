package com.family.re.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
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
//        LocalDate date = LocalDate.now();
//        List<RePool> rePools = lambdaQuery().eq(RePool::getUserId, 1).ge(RePool::getStarTime,date).le(RePool::getEndTime,date).list();
//        for (RePool rePool : rePools) {
//            int cycle = rePool.getRewardCycle();
//            LocalDate start = null;
//            LocalDate end = null;
//            switch (cycle) {
//                case 1: {
//                    start = end = LocalDate.now();
//                }
//                case 2:{
//                    LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
//                    LocalDate sunday = monday.plusWeeks(1).minusDays(1);
//                    start = monday.isBefore(rePool.getStarTime())?rePool.getStarTime():monday;
//                    end = sunday.isAfter(rePool.getEndTime())?rePool.getEndTime():sunday;
//                    break;
//                }
//                case 3:{
//                    LocalDate firstDay = date.with(TemporalAdjusters.firstDayOfMonth());
//                    LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfMonth());
//                    start = firstDay.isBefore(rePool.getStarTime())?rePool.getStarTime():firstDay;
//                    end = lastDay.isAfter(rePool.getEndTime())?rePool.getEndTime():lastDay;
//                    break;
//                }
//                case 4:{
//                    LocalDate firstDay = date.with(TemporalAdjusters.firstDayOfYear());
//                    LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfYear());
//                    start = firstDay.isBefore(rePool.getStarTime())?rePool.getStarTime():firstDay;
//                    end = lastDay.isAfter(rePool.getEndTime())?rePool.getEndTime():lastDay;
//                    break;
//                }
//            }
//            rePool.setRealityConditions(task.getRealityConditions(start,end,rePool.getCreatedUserId()));
//            updateById(rePool);
//        }
//    }


    /**
     * 删除奖品池列表
     * @param prizePoolId 奖品池id
     * @return 删除信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deletePrize(Long prizePoolId) {

        try {
            //删除奖品池里面的奖品
            if(!Db.lambdaUpdate(RePoolDetail.class).eq(RePoolDetail::getPoolId, prizePoolId).remove())
                return AjaxResult.error("删除奖品池的奖品失败");

            //删除奖品池
            if(!lambdaUpdate().eq(RePool::getId,prizePoolId).remove())
                return AjaxResult.error("删除奖品池失败");

            return AjaxResult.success("删除奖品池成功");
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
    public AjaxResult add(RePool pool) {

        try {
            //小孩id
            Long userId=1L;
            //家长id
            Long CreatedUserId=1L;

            //新建一个奖品池
            RePool rePool = getRePool(pool, userId, CreatedUserId);

            //将奖品池添加到数据库
            save(rePool);

            return AjaxResult.success("添加奖品池成功");
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
    public AjaxResult upDate(RePool rePool) {
        try {
            if(!lambdaUpdate().eq(RePool::getId,rePool.getId())
                    .set(RePool::getTitle,rePool.getTitle())
                    .set(RePool::getRewardCycle,rePool.getRewardCycle())
                    .set(RePool::getRewardConditions,rePool.getRewardConditions())
                    .set(RePool::getStarTime,rePool.getStarTime())
                    .set(RePool::getEndTime,rePool.getEndTime())
                    .set(RePool::getState,rePool.getState())
                    .update())
                return AjaxResult.error("修改奖品池失败");
            return AjaxResult.success("修改奖品池成功");
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
    

    @Override
    public void addReachPool() {
        LambdaQueryWrapper<RePool> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.apply("reality_conditions >= reward_conditions");
        List<RePool> rePools = list(queryWrapper);
        rePrizeReachService.addReachPool(rePools);
    }

}

