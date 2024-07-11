package com.family.re.service;

import com.family.re.domain.po.RePoolDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * <p>
 * 奖品池明细表 服务类
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-03
 */
public interface IRePoolDetailService extends IService<RePoolDetail> {

    /**
     * 获取奖品池明细列表
     * @param prizePoolId 奖品池id
     * @return 奖品池明细列表
     */
    AjaxResult getDetailList(Long prizePoolId);


    /**
     * 删除指定奖品池的奖品
     * @param prizePoolId 奖品池id
     * @param prizeId 奖品id
     * @return 删除奖品池奖品结果
     */
    AjaxResult deletePrize(Long prizePoolId, Long prizeId);

    /**
     * 往奖品池添加奖品
     * @param prizePoolId 奖品池id
     * @param prizeId 奖品id
     * @return 往奖品池添加奖品结果
     */
    AjaxResult addPrize(Long prizePoolId, Long prizeId);
    
}
