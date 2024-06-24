package com.family.en.controller;


import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 单词章节表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-24
 */
@RestController
@RequestMapping("/family/en/chapter")
public class EnChapterController {
    @GetMapping("/test")
    public AjaxResult test() {

        return AjaxResult.success("测试成功");
    }
}
