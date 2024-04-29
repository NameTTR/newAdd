package com.family.cc.controller;


import com.family.cc.domain.po.CcChapterStudy;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 汉字章节表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
@RestController
@RequestMapping("/family/cc/chapter")
public class CcChapterController extends BaseController {

    @GetMapping("/test")
    public AjaxResult test() {
        return AjaxResult.success();
    }
}
