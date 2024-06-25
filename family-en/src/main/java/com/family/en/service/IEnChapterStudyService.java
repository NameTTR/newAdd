package com.family.en.service;

import com.family.en.domain.po.EnChapterStudy;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * <p>
 * 单词章节学习记录表 服务类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-24
 */
public interface IEnChapterStudyService extends IService<EnChapterStudy> {

    /**
     * 更新章节学习记录
     * @param chapterId     章节ID
     * @param nextChapterId 下一章节ID/下一单元ID
     * @param sign          标记 0：下一章节id，1：下一单元id
     * @return
     */
    AjaxResult updateChapterStudy(Long chapterId, Long nextChapterId, int sign);
}
