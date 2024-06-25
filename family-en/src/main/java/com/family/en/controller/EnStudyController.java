package com.family.en.controller;


import com.family.en.service.IEnStudyService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 单词学习记录表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-24
 */
@RestController
@RequestMapping("/family/en/study")
public class EnStudyController {

    @Autowired
    private IEnStudyService enStudyService;

    /**
     * 更新单词学习记录
     * @param characterId 汉字id
     * @return
     */
    @PutMapping("{characterId}")
    public AjaxResult updateStudyRecord(@PathVariable("characterId") Long characterId) {
        return enStudyService.updateStudyRecord(characterId);
    }
}
