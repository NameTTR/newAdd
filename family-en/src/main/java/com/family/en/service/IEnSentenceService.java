package com.family.en.service;

import com.family.en.domain.po.EnSentence;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * <p>
 * 单词句子表 服务类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-24
 */
public interface IEnSentenceService extends IService<EnSentence> {

    /**
     * 获取章节中单词的句子信息
     * @param chapterId 章节ID
     * @return
     */
    AjaxResult getEnSentence(Long chapterId);
}
