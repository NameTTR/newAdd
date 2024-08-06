package com.family.pl.controller;

import com.family.pl.service.SetupService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/7/11 23:49
 */
@RestController
@RequestMapping("/family/plconfig")
public class ConfigController {

    @Autowired
    private SetupService setupService;

    @GetMapping("selectrepeat")
    public AjaxResult selectRepeat() {
        Long userId = 1L;
        return AjaxResult.success(setupService.selectRepeat(userId));
    }
}