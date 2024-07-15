package com.family.re.service.impl;

import com.family.re.domain.po.RePoolDetail;
import com.family.re.domain.po.RePrize;
import com.family.re.mapper.RePoolDetailMapper;
import com.family.re.service.IRePoolDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.re.service.IRePrizeService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 奖品池明细表 服务实现类
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-03
 */
@Service
public class RePoolDetailServiceImpl extends ServiceImpl<RePoolDetailMapper, RePoolDetail> implements IRePoolDetailService {


    private final IRePrizeService rePrizeService;

    public RePoolDetailServiceImpl(IRePrizeService rePrizeService) {
        this.rePrizeService = rePrizeService;
    }


    /**
     * 获取奖品池明细列表
     * @param prizePoolId 奖品池id
     * @return 奖品池明细列表
     */
    @Override
    public AjaxResult getPoolDetailList(Long prizePoolId) {
        List<RePoolDetail> list = lambdaQuery()
                .eq(RePoolDetail::getPoolId,prizePoolId).list();
        return AjaxResult.success(list);
    }

    /**
     * 删除奖品池明细
     * @param prizePoolId 奖品池id
     * @param prizeId 奖品id
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deletePoolDetailPrize(Long prizePoolId, Long prizeId) {

        try {
            if (!lambdaUpdate().eq(RePoolDetail::getPoolId,prizePoolId)
                    .eq(RePoolDetail::getId,prizeId).remove())
                return AjaxResult.error("删除奖品池明细失败");
            return AjaxResult.success("删除奖品池明细成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("删除奖品池明细失败");
        }
    }

    /**
     * 添加奖品
     * @param prizePoolId 奖品池id
     * @param prizeId 奖品id
     * @return 添加结果
     */
    @Override
    @Transactional (rollbackFor = Exception.class)
    public AjaxResult addPoolPrize(Long prizePoolId, Long prizeId) {
        try {
            RePrize data = rePrizeService.getById(prizeId);
            RePoolDetail rePoolDetail = new RePoolDetail();
            rePoolDetail.setPoolId(prizePoolId);
            rePoolDetail.setPrizeIco(data.getPrizeIco());
            rePoolDetail.setPrizeName(data.getPrizeName());
            if(!save(rePoolDetail))
                return AjaxResult.error("添加奖品失败");
            return AjaxResult.success("添加奖品成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("添加奖品失败");
        }
    }


}
