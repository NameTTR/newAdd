package com.family.pi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.pi.domain.dto.PiPinyinDTO;
import com.family.pi.domain.po.PiPinyin;
import com.family.pi.domain.po.PiStudy;
import com.family.pi.mapper.PiStudyMapper;
import com.family.pi.service.IPiStudyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.family.pi.enums.PiPinyinState.LEARNED_FINISH;
import static com.family.pi.enums.PiPinyinState.NOT_MASTERED;

/**
 * <p>
 * 拼音学习记录表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
@Service
public class PiStudyServiceImpl extends ServiceImpl<PiStudyMapper, PiStudy> implements IPiStudyService {

    /**
     * 更新拼音学习记录
     * @param pinyinId 拼音id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateStudyRecord(Long pinyinId) {
        try {
            //1. 获取当前用户id
//        Long userId = SecurityUtils.getUserId();
            Long userId = 1L;

            //2. 获取当前拼音学习记录
            PiStudy study = lambdaQuery()
                    .eq(PiStudy::getUserId, userId)
                    .eq(PiStudy::getPinyinId, pinyinId)
                    .one();
            if (study == null) {
                //2.1. 如果不存在该拼音的id，返回失败信息
                return AjaxResult.error();
            }

            //3. 更新拼音学习记录表
            switch (study.getState()){
                case UNLEARNED:
                case NOT_MASTERED:
                    study.setState(LEARNED_FINISH);
                    break;
                case LEARNED_FINISH:
                    study.setState(NOT_MASTERED);
                    break;
            }
            boolean isSuccess = lambdaUpdate().eq(PiStudy::getUserId, userId)
                    .eq(PiStudy::getPinyinId, pinyinId)
                    .set(PiStudy::getState, study.getState())
                    .update();

            //4. 返回结果
            //4.1. 如果更新失败，返回失败信息
            if (!isSuccess){
                return AjaxResult.error("更新失败");
            }

            //4.2. 如果更新成功，返回更新后的拼音信息
            return AjaxResult.success(study);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
