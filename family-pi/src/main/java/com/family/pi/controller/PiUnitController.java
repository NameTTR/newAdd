package com.family.pi.controller;


import com.family.pi.service.IPiUnitService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 拼音单元表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
@RestController
@RequestMapping("/family/pi/unit")
public class PiUnitController {
    @Autowired
    private IPiUnitService piUnitService;

    /**
     * 获取单元列表 - 并获得用户正在学习的单元名称及其id - 用于首页显示
     * @return
     */
    @GetMapping
    public AjaxResult getUnitList() {
        return piUnitService.getUnitList();
    }

    /**
     * 获取单元详情
     * @param id 单元ID
     * @return
     */
    @GetMapping("{unitId}")
    public AjaxResult getUnit(@PathVariable("unitId") Long id) {
        return piUnitService.getUnit(id);
    }
}
