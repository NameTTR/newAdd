package com.family.re.service;

import com.family.re.domain.po.RePrize;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

import java.util.List;

/**
 * <p>
 * 奖品表 服务类
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-03
 */
public interface IRePrizeService extends IService<RePrize> {

    /**
     * 获取奖品列表
     * @return 奖品列表
     */
    AjaxResult getPrizeList(List<String> name);

    /**
     * 删除奖品
     * @param prizeId 奖品id
     * @return 删除结果
     */
    AjaxResult deletePrize(Long prizeId);

    /**
     * 添加奖品
     * @param prizeIco 奖品图标
     * @param prizeName 奖品名称
     * @return 添加结果
     */
    AjaxResult addPrize(String prizeIco, String prizeName);

    /**
     * 修改奖品
     * @param rePrize 奖品
     * @return 修改结果
     */
    AjaxResult changePrize(RePrize rePrize);

    /**
     * 抽奖
     * @param count 奖品数量
     * @return 随机数结果
     */
    AjaxResult lottery( int count);
}
