package com.family.th.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.th.domain.po.ThChapter;
import com.family.th.domain.po.ThChapterStudy;
import com.family.th.domain.po.ThStudy;
import com.family.th.domain.po.ThThinking;
import com.family.th.enums.ThChapterState;
import com.family.th.enums.ThThinkingState;
import com.family.th.mapper.ThChapterStudyMapper;
import com.family.th.service.IThChapterStudyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 思维章节学习记录表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-01
 */
@Service
public class ThChapterStudyServiceImpl extends ServiceImpl<ThChapterStudyMapper, ThChapterStudy> implements IThChapterStudyService {
    
    /**
     * 更新章节学习记录
     * @param chapterId     章节ID
     * @param nextChapterId 下一章节ID/下一单元ID
     * @param sign          标记 0：下一章节id，1：下一单元id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateChapterStudy(Long chapterId, Long nextChapterId, int sign) {
        try {
            //1.获取用户id
//        Long userId = SecurityUtils.getUserId();
            Long userId = 1L;

            //2.根据用户id和章节id查询章节中所有个体的学习记录
            //2.1. 获取当前章节所有个体的id
            List<ThThinking> characters = Db.lambdaQuery(ThThinking.class)
                    .eq(ThThinking::getChapterId, chapterId)
                    .list();
            List<Long> characterIds = new ArrayList<>(characters.size());
            characters.forEach(c -> characterIds.add(c.getId()));

            //2.2. 根据个体id查询学习记录
            List<ThStudy> characterStudies = Db.lambdaQuery(ThStudy.class)
                    .eq(ThStudy::getUserId, userId)
                    .in(ThStudy::getThinkingId, characterIds)
                    .list();


            //3.遍历章节中个体的学习记录，判断是否更新该章节的学习记录
            for (ThStudy study : characterStudies) {
                if (study.getState() == ThThinkingState.UNLEARNED) {
                    //3.1 如果存在个体未完成学习，则不更新章节学习记录
                    return AjaxResult.success("章节未学完");
                }
            }
            //4.如果章节中所有个体都已完成学习
            //4.1. 更新章节学习记录状态为已完成
            if (!updateChapterStudySimple(chapterId, userId, ThChapterState.LEARNED)) {
                //如果更新章节学习记录失败，则返回失败信息
                return AjaxResult.error("章节学习记录更新失败");
            }

            //4.2. 获取下一个章节，并更新章节学习记录状态为正在学习
            if (nextChapterId == -1) return AjaxResult.success("章节学习完成");
            if (sign == 1){
                //如果是下一单元，更新nextChapterId为下一单元的id
                List<ThChapter> chapter = Db.lambdaQuery(ThChapter.class).eq(ThChapter::getUnitId, nextChapterId).orderByAsc(ThChapter::getSort).list();
                nextChapterId = chapter.get(0).getId();

            }
            if (!updateChapterStudySimple(nextChapterId, userId, ThChapterState.LEARNING)) {
                //如果更新章节学习记录失败，则返回失败信息
                throw new Exception("章节学习记录更新失败");
            }

            //5. 返回成功信息
            return AjaxResult.success("章节学习完成");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("章节学习记录更新失败");
        }
    }

    /**
     * 更新章节学习记录状态
     *
     * @param chapterId 章节id
     * @param state     章节学习状态
     * @return
     */
    private boolean updateChapterStudySimple(Long chapterId, Long userId, ThChapterState state) {
        ThChapterStudy cur = lambdaQuery()
                .eq(ThChapterStudy::getUserId, userId)
                .eq(ThChapterStudy::getChapterId, chapterId)
                .one();
        if(cur.getState() == state) return true;
        return lambdaUpdate()
                .eq(ThChapterStudy::getUserId, userId)
                .eq(ThChapterStudy::getChapterId, chapterId)
                .set(ThChapterStudy::getState, state)
                .update(cur);
    }
}
