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
import org.springframework.transaction.annotation.Transactional;

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
     * 获取奖品池兑现池和奖品池列表
     * @return 奖品池兑现池和奖品池列表
     */
    @Override
    public AjaxResult getPrizeReach() {

        RePrizeReachDTO list = new RePrizeReachDTO();

        //奖品池表
        list.setPlanList(Db.lambdaQuery(RePool.class)
                .eq(RePool::getState,1)
                .eq(RePool::getUserId, 1)
                .list());

        //未兑现的奖品兑现表
        list.setUnPrizeList(lambdaQuery()
                .eq(RePrizeReach::getUserId, 1)
                .eq(RePrizeReach::getIsLottery, 0)
                .list());

        //已兑现的奖品兑现表
        list.setPrizeList(lambdaQuery()
                .eq(RePrizeReach::getUserId, 1)
                .eq(RePrizeReach::getIsLottery, 1)
                //.first("LIMIT 3")
                .list());
        return AjaxResult.success(list);
    }

    /**
     * 抽奖后的数据更新
     * @param prizeId 抽中的奖品id
     * @param reachPoolId 兑现池id
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult lotteryUpdate(Long prizeId, Long reachPoolId) {

        try {
            RePrizeReach one =lambdaQuery().eq(RePrizeReach::getId,reachPoolId).one();
            if(one.getIsLottery()==1) {
                return AjaxResult.error("该奖品已经被抽过了");
            }
            //通过抽中的奖品id，获取奖品池兑现明细表中，抽中的奖品的数据，并且对抽中和未抽中的奖品进行标记
            RePrizeReachDetail data = rePrizeReachDetailService.lotteryUpdate(prizeId, reachPoolId);

            if(data==null){
                return AjaxResult.error("奖品池兑现明细表中没有奖品池id为"+reachPoolId+"的数据");
            }
            //更新奖品兑现表，将已经抽中的奖品的数据更新到奖品兑现表中
            one.setIsLottery(1)
            .setPoolDetailId(prizeId)
            .setPrizeIco(data.getPrizeIco())
            .setPrizeName(data.getPrizeName())
            .setTimeLottery(LocalDateTime.now());
            if(!updateById(one)) {
                return AjaxResult.error("奖品更新失败");
            }
            return AjaxResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("数据更新失败");
        }
    }

    /**
     * 从满足条件的奖品池中添加到兑现池
     * @param rePools 满足条件的奖品池
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

    /**
     * 获取所有的奖品池兑现明细表
     * @return 所有的奖品池兑现明细表
     */
    @Override
    public AjaxResult getAllPrizeReachList() {
        Long userId = 1L;
        return AjaxResult.success(lambdaQuery()
                .eq(RePrizeReach::getUserId, userId)
                .eq(RePrizeReach::getIsLottery, 1)
                .list());
    }
}
