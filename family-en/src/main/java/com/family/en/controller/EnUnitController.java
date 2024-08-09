package com.family.en.controller;


import com.family.en.service.IEnUnitService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 单词单元表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-24
 */
@RestController
@RequestMapping("/family/en/unit")
public class EnUnitController {

    @Autowired
    private IEnUnitService enUnitService;
    /**
     * 获取单元列表 - 并获得用户正在学习的单元名称及其id - 用于首页显示
     * @return
     */
    @GetMapping
    public AjaxResult getUnitList() {
        return enUnitService.getUnitList();
    }

    /**
     * 获取单元详情
     * @param id 单元ID
     * @return
     */
    @GetMapping("{unitId}")
    public AjaxResult getUnit(@PathVariable("unitId") Long id) {
        return enUnitService.getUnit(id);
    }

    /**
     * 查询单元和对应的章节信息
     * @return
     */
    @GetMapping("/unit-chapter")
    public AjaxResult getUnitChapter() {
        return enUnitService.getUnitChapter();
    }
}
