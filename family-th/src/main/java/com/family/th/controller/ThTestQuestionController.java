package com.family.th.controller;


import com.family.th.service.IThTestQuestionService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 思维测试题库表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-01
 */
@RestController
@RequestMapping("/family/th/test-question")
public class ThTestQuestionController {

    @Autowired
    private IThTestQuestionService thTestQuestionService;

    /**
     * 根据测试题ID列表获取测试题信息
     * @param questionIds 测试题目ID列表
     * @return
     */
    @GetMapping()
    public AjaxResult getThTestQuestion(@RequestBody List<Long> questionIds) {
        return thTestQuestionService.getThTestQuestion(questionIds);
    }
}
