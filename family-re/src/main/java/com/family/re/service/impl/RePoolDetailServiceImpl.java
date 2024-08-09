package com.family.re.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.re.domain.po.RePool;
import com.family.re.domain.po.RePoolDetail;
import com.family.re.domain.po.RePrize;
import com.family.re.mapper.RePoolDetailMapper;
import com.family.re.service.IRePoolDetailService;
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
     * @param prizeName 奖品名称
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deletePoolDetailPrize(Long prizePoolId, String prizeName) {

        try {
            if (!lambdaUpdate().eq(RePoolDetail::getPoolId,prizePoolId)
                    .eq(RePoolDetail::getPrizeName,prizeName).remove())
                return AjaxResult.error("删除奖品池明细失败");
            // 减少 getCountPrize 的值
            RePool rePool = Db.lambdaQuery(RePool.class)
                    .eq(RePool::getId, prizePoolId)
                    .one();
            if (rePool == null) {
                return AjaxResult.error("奖品池不存在");
            }
            if (rePool.getCountPrize() == 0) {
                return AjaxResult.error("奖品池已空");
            }
            int newCount = rePool.getCountPrize() - 1;
            // 更新 getCountPrize 字段
            Db.lambdaUpdate(RePool.class)
                    .eq(RePool::getId, prizePoolId)
                    .set(RePool::getCountPrize, newCount)
                    .update();
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
            //从总奖池中获取奖品
            RePrize data = Db.lambdaQuery(RePrize.class)
                    .eq(RePrize::getId, prizeId)
                    .one();
            //判断奖品是否存在奖品的总池中
            if(data==null) {
                return AjaxResult.error("奖品不存在");
            }
            //获取奖品池
            RePool rePool = Db.lambdaQuery(RePool.class)
                    .eq(RePool::getId, prizePoolId)
                    .one();
            if (rePool == null) {
                return AjaxResult.error("奖品池不存在");
            }
            if(rePool.getCountPrize()==8) {
                return AjaxResult.error("奖品池已满");
            }
            //判断奖品是否已存在于奖品池
            if(lambdaQuery()
                    .eq(RePoolDetail::getPoolId,prizePoolId)
                    .eq(RePoolDetail::getPrizeName,data.getPrizeName())
                    .count()>0) {
                return AjaxResult.error("奖品已存在");
            }
            //添加奖品，将总奖池的数据添加到奖品池明细中
            RePoolDetail rePoolDetail = new RePoolDetail();
            rePoolDetail.setPoolId(prizePoolId);
            rePoolDetail.setPrizeIco(data.getPrizeIco());
            rePoolDetail.setPrizeName(data.getPrizeName());
            //保存奖品
            if(!save(rePoolDetail)) {
                return AjaxResult.error("添加奖品失败");
            }

            // 增加 getCountPrize 的值
            int newCount = rePool.getCountPrize() + 1;
            // 更新 getCountPrize 字段
            Db.lambdaUpdate(RePool.class)
                    .eq(RePool::getId, prizePoolId)
                    .set(RePool::getCountPrize, newCount)
                    .update();

            return AjaxResult.success("添加奖品成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("添加奖品失败");
        }
    }


}
