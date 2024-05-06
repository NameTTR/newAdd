package com.family.cc.controller;


import com.family.cc.service.ICcChapterStudyService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 汉字章节学习记录表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
@RestController
@RequestMapping("/family/cc/chapter-study")
public class CcChapterStudyController extends BaseController {

    @Autowired
    private ICcChapterStudyService ccChapterStudyService;

    /**
     * 更新章节学习记录
     * @param chapterId     章节ID
     * @param nextChapterId 下一章节ID
     * @return
     */
    @PutMapping
    public AjaxResult updateChapterStudy(@RequestParam("chapterId") Long chapterId, @RequestParam(value = "nextChapterId",defaultValue = "-1") Long nextChapterId) {
        return ccChapterStudyService.updateChapterStudy(chapterId,nextChapterId);
    }
}
