package com.family.pi.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.pi.domain.po.PiPinyin;
import com.family.pi.domain.po.PiStudy;
import com.family.pi.domain.po.PiUnitStudy;
import com.family.pi.enums.PiPinyinState;
import com.family.pi.enums.PiUnitState;
import com.family.pi.mapper.PiUnitStudyMapper;
import com.family.pi.service.IPiUnitStudyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 单元单元学习记录表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
@Service
public class PiUnitStudyServiceImpl extends ServiceImpl<PiUnitStudyMapper, PiUnitStudy> implements IPiUnitStudyService {

    /**
     * 更新单元学习记录
     * @param unitId     单元ID
     * @param nextUnitId 下一单元ID
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateUnitStudy(Long unitId, Long nextUnitId) {
        try {
            //1.获取用户id
//        Long userId = SecurityUtils.getUserId();
            Long userId = 1L;

            //2.根据用户id和单元id查询单元中所有拼音的学习记录
            //2.1. 获取当前单元所有拼音的id
            List<PiPinyin> pinyins = Db.lambdaQuery(PiPinyin.class)
                    .eq(PiPinyin::getUnitId, unitId)
                    .list();
            List<Long> pinyinIds = new ArrayList<>(pinyins.size());
            pinyins.forEach(c -> pinyinIds.add(c.getId()));

            //2.2. 根据拼音id查询学习记录
            List<PiStudy> pinyinStudies = Db.lambdaQuery(PiStudy.class)
                    .eq(PiStudy::getUserId, userId)
                    .in(PiStudy::getPinyinId, pinyinIds)
                    .list();


            //3.遍历单元中拼音的学习记录，判断是否更新该单元的学习记录
            for (PiStudy study : pinyinStudies) {
                if (study.getState() == PiPinyinState.UNLEARNED) {
                    //3.1 如果存在汉字未完成学习，则不更新单元学习记录
                    return AjaxResult.success("单元未学完");
                }
            }
            //4.如果单元中所有拼音都已完成学习
            //4.1. 更新单元学习记录状态为已完成
            if (!updateUnitStudySimple(unitId, userId, PiUnitState.LEARNED)) {
                //如果更新单元学习记录失败，则返回失败信息
                return AjaxResult.error("单元学习记录更新失败");
            }

            //4.2. 获取下一个单元，并更新单元学习记录状态为正在学习
            if (nextUnitId == -1) return AjaxResult.success("单元学习完成");
            if (!updateUnitStudySimple(nextUnitId, userId, PiUnitState.LEARNING)) {
                //如果更新单元学习记录失败，则返回失败信息
                throw new Exception("单元学习记录更新失败");
            }

            //5. 返回成功信息
            return AjaxResult.success("单元学习完成");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("单元学习记录更新失败");
        }
    }

    /**
     * 更新单元学习记录状态
     *
     * @param unitId    单元id
     * @param state     单元学习状态
     * @return
     */
    private boolean updateUnitStudySimple(Long unitId, Long userId, PiUnitState state) {
        PiUnitStudy cur = lambdaQuery()
                .eq(PiUnitStudy::getUserId, userId)
                .eq(PiUnitStudy::getUnitId, unitId)
                .one();
        if (cur.getState() == state) return true;
        return lambdaUpdate()
                .eq(PiUnitStudy::getUserId, userId)
                .eq(PiUnitStudy::getUnitId, unitId)
                .set(PiUnitStudy::getState, state)
                .update(cur);
    }
}
