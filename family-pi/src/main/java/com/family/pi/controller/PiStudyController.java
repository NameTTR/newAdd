package com.family.pi.controller;


import com.family.pi.service.IPiStudyService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 拼音学习记录表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
@RestController
@RequestMapping("/family/pi/study")
public class PiStudyController {
    @Autowired
    private IPiStudyService piStudyService;

    /**
     * 更新拼音学习记录
     * @param pinyinId 汉字id
     * @return
     */
    @PutMapping("{pinyinId}")
    public AjaxResult updateStudyRecord(@PathVariable("pinyinId") Long pinyinId) {
        return piStudyService.updateStudyRecord(pinyinId);
    }
}
