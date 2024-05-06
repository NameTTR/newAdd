package com.family.cc.controller;


import com.family.cc.service.ICcUnitService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 单元表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
@RestController
@RequestMapping("/family/cc/unit")
public class CcUnitController extends BaseController {
    @Autowired
    private ICcUnitService ccUnitService;

    /**
     * 获取单元列表
     * @return
     */
    @GetMapping
    public AjaxResult getUnitList() {
        return ccUnitService.getUnitList();
    }

    /**
     * 获取单元详情
     * @param id 单元ID
     * @return
     */
    @GetMapping("{unitId}")
    public AjaxResult getUnit(@PathVariable("unitId") Long id) {
        return ccUnitService.getUnit(id);
    }
}
