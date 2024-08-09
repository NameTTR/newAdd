package com.family.th.controller;


import com.family.th.service.IThStudyService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 思维学习记录表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-01
 */
@RestController
@RequestMapping("/family/th/study")
public class ThStudyController {
    @Autowired
    private IThStudyService thStudyService;

    /**
     * 更新汉字学习记录
     * @param thinkingId 汉字id
     * @return
     */
    @PutMapping("{thinkingId}")
    public AjaxResult updateStudyRecord(@PathVariable("thinkingId") Long thinkingId) {
        return thStudyService.updateStudyRecord(thinkingId);
    }
}
