package com.family.th.service.impl;

import com.family.th.domain.po.ThStudy;
import com.family.th.mapper.ThStudyMapper;
import com.family.th.service.IThStudyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.family.th.enums.ThThinkingState.LEARNED_FINISH;
import static com.family.th.enums.ThThinkingState.NOT_MASTERED;

/**
 * <p>
 * 思维学习记录表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-01
 */
@Service
public class ThStudyServiceImpl extends ServiceImpl<ThStudyMapper, ThStudy> implements IThStudyService {

    /**
     * 更新个体学习记录
     * @param thinkingId 个体id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateStudyRecord(Long thinkingId) {
        try {
            //1. 获取当前用户id
//        Long userId = SecurityUtils.getUserId();
            Long userId = 1L;

            //2. 获取当前个体学习记录
            ThStudy study = lambdaQuery()
                    .eq(ThStudy::getUserId, userId)
                    .eq(ThStudy::getThinkingId, thinkingId)
                    .one();
            if (study == null) {
                //2.1. 如果不存在该个体的id，返回失败信息
                return AjaxResult.error();
            }

            //3. 更新个体学习记录表
            switch (study.getState()){
                case UNLEARNED:
                case NOT_MASTERED:
                    study.setState(LEARNED_FINISH);
                    break;
                case LEARNED_FINISH:
                    study.setState(NOT_MASTERED);
                    break;
            }
            boolean isSuccess = lambdaUpdate().eq(ThStudy::getUserId, userId)
                    .eq(ThStudy::getThinkingId, thinkingId)
                    .set(ThStudy::getState, study.getState())
                    .update();

            //4. 返回结果
            //4.1. 如果更新失败，返回失败信息
            if (!isSuccess){
                return AjaxResult.error("更新失败");
            }

            //4.2. 如果更新成功，返回更新后的个体信息
            return AjaxResult.success(study);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("更新学习记录失败");

        }
    }
}
