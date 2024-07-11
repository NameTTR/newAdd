package com.family.re.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.family.re.domain.po.RePrizeReachDetail;
import com.family.re.mapper.RePrizeReachDetailMapper;
import com.family.re.service.IRePrizeReachDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 奖品池兑现明细表 服务实现类
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-04
 */
@Service
public class RePrizeReachDetailServiceImpl extends ServiceImpl<RePrizeReachDetailMapper, RePrizeReachDetail> implements IRePrizeReachDetailService {

    /**
     * 查询奖品池兑现明细表
     * @param prizeReachId 奖品池兑现id
     * @return 奖品池兑现明细表
     */
    @Override
    public AjaxResult getList(Long prizeReachId) {

        List<RePrizeReachDetail> dataList = lambdaQuery().eq(RePrizeReachDetail::getPrizeReachId,prizeReachId).list();
        List<RePrizeReachDetail> list = new ArrayList<>(9);
        for(int i=0 ; i<9 ; i++) {
            list.add(null);
        }
        int index = 0;
        for (RePrizeReachDetail entity : dataList) {
            if (index == 4) {
                index++; // 跳过第四个位置
            }
            list.set(index, entity);
            index++;
        }
        return AjaxResult.success(list);
    }

    /**
     * 更新奖品池兑现明细表
     * @param prizeId 奖品id
     * @param reachPoolId 兑现池id
     * @return 奖品池兑现明细表
     */
    @Override
    public RePrizeReachDetail lotteryUpdate(Long prizeId, Long reachPoolId) {
        RePrizeReachDetail data = null;
        List<RePrizeReachDetail> list = lambdaQuery()
                .eq(RePrizeReachDetail::getPrizeReachId,reachPoolId).list();
        for(RePrizeReachDetail entity : list) {
            if(entity.getId().equals(prizeId)) {
                entity.setIsCheck(1);
                data = entity;
            }
            else {
                entity.setIsCheck(0);
            }
        }
        updateBatchById(list);
        return data;
    }
}