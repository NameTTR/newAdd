package com.family.re.service;

import com.family.re.domain.po.RePool;
import com.family.re.domain.po.RePrizeReach;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

import java.util.List;

/**
 * <p>
 * 奖品兑现表 服务类
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-03
 */
public interface IRePrizeReachService extends IService<RePrizeReach> {

    /**
     * 获取奖品池兑现池和奖品池列表
     * @return 奖品池兑现池和奖品池列表
     */
    AjaxResult getList();

    /**
     * 抽奖后的数据更新
     * @param prizeId 抽中的奖品id
     * @param reachPoolId 兑现池id
     * @return 更新结果
     */
    AjaxResult lotteryUpdate(Long prizeId, Long reachPoolId);

    /**
     * 从满足条件的奖品池中添加到兑现池
     * @param rePools 满足条件的奖品池
     */
    void addReachPool(List<RePool> rePools);
}
