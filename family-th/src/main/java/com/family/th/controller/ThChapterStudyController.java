package com.family.th.controller;


import com.family.th.service.IThChapterStudyService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 思维章节学习记录表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-01
 */
@RestController
@RequestMapping("/family/th/chapter-study")
public class ThChapterStudyController {
    @Autowired
    private IThChapterStudyService thChapterStudyService;

    /**
     * 更新章节学习记录
     * @param chapterId     章节ID
     * @param nextChapterId 下一章节ID/下一单元ID
     * @param sign          标记 0：下一章节id，1：下一单元id
     * @return
     */
    @PutMapping
    public AjaxResult updateChapterStudy(
            @RequestParam("chapterId") Long chapterId,
            @RequestParam(value = "nextChapterId",defaultValue = "-1") Long nextChapterId,
            @RequestParam(value = "sign",defaultValue = "0") int sign) {
        return thChapterStudyService.updateChapterStudy(chapterId,nextChapterId,sign);
    }
}
