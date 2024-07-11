package com.family.re.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.re.domain.dto.RePrizeReachDTO;
import com.family.re.domain.po.RePool;
import com.family.re.domain.po.RePrizeReach;
import com.family.re.domain.po.RePrizeReachDetail;
import com.family.re.mapper.RePrizeReachMapper;
import com.family.re.service.IRePrizeReachDetailService;
import com.family.re.service.IRePrizeReachService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 * 奖品兑现表 服务实现类
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-03
 */
@Service
public class RePrizeReachServiceImpl extends ServiceImpl<RePrizeReachMapper, RePrizeReach> implements IRePrizeReachService {

    private final IRePrizeReachDetailService rePrizeReachDetailService;

    public RePrizeReachServiceImpl(IRePrizeReachDetailService rePrizeReachDetailService) {
        this.rePrizeReachDetailService = rePrizeReachDetailService;
    }

    /**
     * 获取奖品池兑现池和奖品池
     * @return 奖品池兑现池和奖品池
     */
    @Override
    public AjaxResult getList() {

        RePrizeReachDTO list = new RePrizeReachDTO();

        //奖品池表
        list.setPlanList(Db.lambdaQuery(RePool.class)
                .eq(RePool::getState,1)
                .eq(RePool::getUserId, 1)
                .list());

        if(list.getPrizeList()==null)
            return AjaxResult.error("奖品池表中没有数据");

        //未兑现的奖品兑现表
        list.setUnPrizeList(lambdaQuery()
                .eq(RePrizeReach::getUserId, 1)
                .eq(RePrizeReach::getIsLottery, 0)
                .list());

        //已兑现的奖品兑现表
        list.setPrizeList(lambdaQuery()
                .eq(RePrizeReach::getUserId, 1)
                .eq(RePrizeReach::getIsLottery, 1)
                .first("LIMIT 3")
                .list());
        return AjaxResult.success(list);
    }

    /**
     * 抽奖后的数据更新
     * @param prizeId 奖品id
     * @param reachPoolId 兑现池id
     * @return 更新结果
     */
    @Override
    public AjaxResult lotteryUpdate(Long prizeId, Long reachPoolId) {

        RePrizeReachDetail data = rePrizeReachDetailService.lotteryUpdate(prizeId, reachPoolId);

        if(data==null)
            return AjaxResult.error("奖品池兑现明细表中没有奖品池id为"+reachPoolId+"的数据");
        lambdaUpdate().eq(RePrizeReach::getId,reachPoolId)
                .set(RePrizeReach::getIsLottery,1)
                .set(RePrizeReach::getPoolDetailId,prizeId)
                .set(RePrizeReach::getPrizeIco,data.getPrizeIco())
                .set(RePrizeReach::getPrizeName,data.getPrizeName())
                .set(RePrizeReach::getTimeLottery, LocalDateTime.now())
                .update();
        return AjaxResult.success();
    }

    /**
     * 添加兑现池
     * @param rePools 兑现池
     */
    @Override
    public void addReachPool(List<RePool> rePools) {
        for(RePool rePool:rePools){
            RePrizeReach rePrizeReach = new RePrizeReach();
            rePrizeReach.setUserId(1L);
            rePrizeReach.setPoolId(rePool.getId());
            rePrizeReach.setIsLottery(0);
            rePrizeReach.setFlagDelete(0);
            save(rePrizeReach);
        }
    }
}
