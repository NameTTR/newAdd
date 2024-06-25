package com.family.en.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.en.domain.po.EnChapter;
import com.family.en.domain.po.EnChapterStudy;
import com.family.en.domain.po.EnStudy;
import com.family.en.domain.po.EnWord;
import com.family.en.enums.EnChapterState;
import com.family.en.enums.EnWordState;
import com.family.en.mapper.EnChapterStudyMapper;
import com.family.en.service.IEnChapterStudyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 单词章节学习记录表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-24
 */
@Service
public class EnChapterStudyServiceImpl extends ServiceImpl<EnChapterStudyMapper, EnChapterStudy> implements IEnChapterStudyService {

    /**
     * 更新章节学习记录
     * @param chapterId     章节ID
     * @param nextChapterId 下一章节ID/下一单元ID
     * @param sign          标记 0：下一章节id，1：下一单元id
     * @return
     */
    @Override
    public AjaxResult updateChapterStudy(Long chapterId, Long nextChapterId, int sign) {
        try {
            //1.获取用户id
//        Long userId = SecurityUtils.getUserId();
            Long userId = 1L;

            //2.根据用户id和章节id查询章节中所有单词的学习记录
            //2.1. 获取当前章节所有单词的id
            List<EnWord> characters = Db.lambdaQuery(EnWord.class)
                    .eq(EnWord::getChapterId, chapterId)
                    .list();
            List<Long> characterIds = new ArrayList<>(characters.size());
            characters.forEach(c -> characterIds.add(c.getId()));

            //2.2. 根据单词id查询学习记录
            List<EnStudy> characterStudies = Db.lambdaQuery(EnStudy.class)
                    .eq(EnStudy::getUserId, userId)
                    .in(EnStudy::getWordId, characterIds)
                    .list();


            //3.遍历章节中单词的学习记录，判断是否更新该章节的学习记录
            for (EnStudy study : characterStudies) {
                if (study.getState() == EnWordState.UNLEARNED) {
                    //3.1 如果存在单词未完成学习，则不更新章节学习记录
                    return AjaxResult.success("章节未学完");
                }
            }
            //4.如果章节中所有单词都已完成学习
            //4.1. 更新章节学习记录状态为已完成
            if (!updateChapterStudySimple(chapterId, userId, EnChapterState.LEARNED)) {
                //如果更新章节学习记录失败，则返回失败信息
                return AjaxResult.error("章节学习记录更新失败");
            }

            //4.2. 获取下一个章节，并更新章节学习记录状态为正在学习
            if (nextChapterId == -1) return AjaxResult.success("章节学习完成");
            if (sign == 1){
                //如果是下一单元，更新nextChapterId为下一单元的id
                EnChapter chapter = Db.lambdaQuery(EnChapter.class).eq(EnChapter::getUnitId, nextChapterId).orderByAsc(EnChapter::getSort).one();
                nextChapterId = chapter.getId();

            }
            if (!updateChapterStudySimple(nextChapterId, userId, EnChapterState.LEARNING)) {
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
    private boolean updateChapterStudySimple(Long chapterId, Long userId, EnChapterState state) {
        EnChapterStudy cur = lambdaQuery()
                .eq(EnChapterStudy::getUserId, userId)
                .eq(EnChapterStudy::getChapterId, chapterId)
                .one();
        cur.setState(state);
        return lambdaUpdate()
                .eq(EnChapterStudy::getUserId, userId)
                .eq(EnChapterStudy::getChapterId, chapterId)
                .update(cur);
    }
}
