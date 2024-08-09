package com.family.re.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.re.domain.po.RePoolDetail;
import com.family.re.domain.po.RePrizeReachDetail;
import com.family.re.mapper.RePrizeReachDetailMapper;
import com.family.re.service.IRePrizeReachDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.stereotype.Service;

import java.util.*;

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

        try {
            //按照奖品池兑现id查询奖品池兑现明细表
            List<RePrizeReachDetail> dataList = lambdaQuery()
                    .eq(RePrizeReachDetail::getPrizeReachId,prizeReachId).list();
            //将查询到的数据按照顺序放入list中(为了前端展示方便，将第四个位置的数据置为null)
            //一定要放满9个位置，否则前端展示会出现问题
            List<RePrizeReachDetail> list = new ArrayList<>(9);

            if (dataList.size() !=8 ) {
                return AjaxResult.error("奖品数量不足");
            }

            //因为只有9个位置，而且i==4时会放入2次数据，所以只循环8次
            for (int i = 0; i < 8; i++) {
                if (i == 4) {
                    list.add(null);
                }
                list.add(dataList.get(i));
            }
            //返回排好的数据
            return AjaxResult.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("查询奖品池兑现明细表失败");
        }
    }

    /**
     * 更新奖品池兑现明细表
     * @param prizeId 抽中的奖品id
     * @param reachPoolId 兑现池id
     * @return 抽中的奖品数据
     */
    @Override
    public RePrizeReachDetail lotteryUpdate(Long prizeId, Long reachPoolId) {
        RePrizeReachDetail data = null;
        //查询兑现池明细表，将奖品池兑现明细表中的"是否选中"字段更新
        List<RePrizeReachDetail> list = lambdaQuery()
                .eq(RePrizeReachDetail::getPrizeReachId,reachPoolId).list();
        for(RePrizeReachDetail entity : list) {
            //如果是抽中的奖品，将"是否选中"字段更新为1
            if(entity.getId().equals(prizeId)) {
                entity.setIsCheck(1);
                data = entity;
            }
            //如果不是抽中的奖品，将"是否选中"字段更新为0
            else {
                entity.setIsCheck(0);
            }
        }
        //将上述的更新操作批量执行更新
        updateBatchById(list);
        return data;
    }

    @Override
    public void addPrize(Long rePrizeReachId, Long rePoolId) {
        //查询奖品池明细表
        List<RePoolDetail> list = Db.lambdaQuery(RePoolDetail.class)
                .eq(RePoolDetail::getPoolId, rePoolId).list();

        //创建一个奖品池兑现明细表的list
        List<RePrizeReachDetail> rePrizeReachDetails = new ArrayList<>();

        //将查询到的数据添加到奖品池兑现明细表中
        for(RePoolDetail entity : list) {
            RePrizeReachDetail rePrizeReachDetail = new RePrizeReachDetail();
            rePrizeReachDetail.setPrizeReachId(rePrizeReachId);
            rePrizeReachDetail.setPrizeName(entity.getPrizeName());
            rePrizeReachDetail.setPrizeIco(entity.getPrizeIco());
            rePrizeReachDetail.setIsCheck(0);
            rePrizeReachDetails.add(rePrizeReachDetail);
        }

        //如果奖品数量不足8个，将"这里什么都没有"奖品添加到奖品池兑现明细表中
        int count = list.size();
        if(count < 8) {
            for(int i = 0; i < 8 - count; i++) {
                RePrizeReachDetail rePrizeReachDetail = new RePrizeReachDetail();
                rePrizeReachDetail.setPrizeReachId(rePrizeReachId);
                rePrizeReachDetail.setPrizeName("这里什么都没有");
                rePrizeReachDetail.setPrizeIco("/");
                rePrizeReachDetail.setIsCheck(0);
                rePrizeReachDetails.add(rePrizeReachDetail);
            }
        }

        // 打乱列表
        Collections.shuffle(rePrizeReachDetails);

        // 批量插入数据库
        saveBatch(rePrizeReachDetails);
    }
}