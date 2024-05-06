package com.family.cc.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.cc.domain.po.CcChapterStudy;
import com.family.cc.domain.po.CcCharacter;
import com.family.cc.domain.po.CcStudy;
import com.family.cc.enums.CcChapterStatus;
import com.family.cc.enums.CcCharacterStatus;
import com.family.cc.mapper.CcChapterStudyMapper;
import com.family.cc.service.ICcChapterStudyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 汉字章节学习记录表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
@Service
public class CcChapterStudyServiceImpl extends ServiceImpl<CcChapterStudyMapper, CcChapterStudy> implements ICcChapterStudyService {

    /**
     * 更新章节学习记录
     *
     * @param chapterId     章节ID
     * @param nextChapterId 下一章节ID
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateChapterStudy(Long chapterId, Long nextChapterId) {
        //1.获取用户id
//        Long userId = SecurityUtils.getUserId();
        Long userId = 1L;

        //2.根据用户id和章节id查询章节中所有汉字的学习记录
        //2.1. 获取当前章节所有汉字的id
        List<CcCharacter> characters = Db.lambdaQuery(CcCharacter.class)
                .eq(CcCharacter::getChapterId, chapterId)
                .list();
        List<Long> characterIds = new ArrayList<>(characters.size());
        characters.forEach(c -> characterIds.add(c.getId()));

        //2.2. 根据汉字id查询学习记录
        List<CcStudy> characterStudies = Db.lambdaQuery(CcStudy.class)
                .eq(CcStudy::getUserId, userId)
                .in(CcStudy::getCharacterId, characterIds)
                .list();


        //3.遍历章节中汉字的学习记录，判断是否更新该章节的学习记录
        for (CcStudy study : characterStudies) {
            if (study.getState() != CcCharacterStatus.LEARNED_FINISH) {
                //3.1 如果存在汉字未完成学习，则不更新章节学习记录
                return AjaxResult.success("章节未学完");
            }
        }
        //4.如果章节中所有汉字都已完成学习
        //4.1. 更新章节学习记录状态为已完成
        if (!updateChapterStudySimple(chapterId, userId, CcChapterStatus.LEARNED)) {
            //如果更新章节学习记录失败，则返回失败信息
            return AjaxResult.error("章节学习记录更新失败");
        }

        //4.2. 获取下一个章节，并更新章节学习记录状态为正在学习
        if (nextChapterId == -1) return AjaxResult.success("章节学习完成");
        if (!updateChapterStudySimple(nextChapterId, userId, CcChapterStatus.LEARNING)) {
            //如果更新章节学习记录失败，则返回失败信息
            return AjaxResult.error("章节学习记录更新失败");
        }

        //5. 返回成功信息
        return AjaxResult.success("章节学习完成");
    }

    /**
     * 更新章节学习记录状态
     *
     * @param chapterId 章节id
     * @param state     章节学习状态
     * @return
     */
    private boolean updateChapterStudySimple(Long chapterId, Long userId, CcChapterStatus state) {
        CcChapterStudy cur = lambdaQuery()
                .eq(CcChapterStudy::getUserId, userId)
                .eq(CcChapterStudy::getChapterId, chapterId)
                .one();
        cur.setState(state);
        return lambdaUpdate()
                .eq(CcChapterStudy::getUserId, userId)
                .eq(CcChapterStudy::getChapterId, chapterId)
                .update(cur);
    }
}
