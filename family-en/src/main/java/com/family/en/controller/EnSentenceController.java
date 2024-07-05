package com.family.en.controller;


import com.family.en.service.IEnSentenceService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 单词句子表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-24
 */
@RestController
@RequestMapping("/family/en/sentence")
public class EnSentenceController {
    @Autowired
    private IEnSentenceService enSentenceService;

    /**
     * 获取章节中单词的句子信息
     * @param chapterId 章节ID
     * @return
     */
    @GetMapping("{chapterId}")
    public AjaxResult getCcCharacterGroup(@PathVariable("chapterId") Long chapterId) {
        return enSentenceService.getEnSentence(chapterId);
    }
}
