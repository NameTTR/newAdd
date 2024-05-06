package com.family.cc.service;

import com.family.cc.domain.po.CcChapterStudy;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * <p>
 * 汉字章节学习记录表 服务类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
public interface ICcChapterStudyService extends IService<CcChapterStudy> {

    /**
     * 更新章节学习记录
     * @param chapterId     章节ID
     * @param nextChapterId 下一章节ID
     * @return
     */
    AjaxResult updateChapterStudy(Long chapterId,Long nextChapterId);
}
