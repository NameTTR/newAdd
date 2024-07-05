package com.family.pi.controller;


import com.family.pi.service.IPiPinyinGroupService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 拼音组词表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
@RestController
@RequestMapping("/family/pi/pinyin-group")
public class PiPinyinGroupController {
    @Autowired
    private IPiPinyinGroupService piPinyinGroupService;

    /**
     * 获取单元中拼音的组词信息
     * @param unitId 单元ID
     * @param type   类型
     * @return
     */
    @GetMapping("{unitId}/{type}")
    public AjaxResult getPiPinyinGroup(@PathVariable("unitId") Long unitId, @PathVariable("type") Integer type) {
        return piPinyinGroupService.getPiPinyinGroup(unitId, type);
    }
}
