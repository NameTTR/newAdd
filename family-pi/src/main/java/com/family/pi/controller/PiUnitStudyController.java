package com.family.pi.controller;


import com.family.pi.service.IPiUnitStudyService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 单元章节学习记录表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
@RestController
@RequestMapping("/family/pi/unit-study")
public class PiUnitStudyController {
    @Autowired
    private IPiUnitStudyService piUnitStudyService;

    /**
     * 更新章节学习记录
     * @param unitId     章节ID
     * @param nextUnitId 下一单元ID
     * @return
     */
    @PutMapping
    public AjaxResult updateUnitStudy(
            @RequestParam("unitId") Long unitId,
            @RequestParam(value = "nextUnitId",defaultValue = "-1") Long nextUnitId) {
        return piUnitStudyService.updateUnitStudy(unitId,nextUnitId);
    }
}
