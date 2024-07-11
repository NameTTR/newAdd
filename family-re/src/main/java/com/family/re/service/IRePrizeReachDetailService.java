package com.family.re.service;

import com.family.re.domain.po.RePrizeReachDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * <p>
 * 奖品池兑现明细表 服务类
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-04
 */
public interface IRePrizeReachDetailService extends IService<RePrizeReachDetail> {

    /**
     * 查询奖品池兑现明细表
     * @param prizeReachId 奖品池兑现id
     * @return 奖品池兑现明细表
     */
    AjaxResult getList(Long prizeReachId);

    /**
     * 更新奖品池兑现明细表
     * @param prizeId 奖品id
     * @param reachPoolId 兑现池id
     * @return 奖品池兑现明细表
     */
    RePrizeReachDetail lotteryUpdate(Long prizeId, Long reachPoolId);
}
