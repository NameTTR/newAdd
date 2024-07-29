package com.family.cc.controller;


import com.family.cc.service.ICcCharacterGroupService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 汉字组词表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
@RestController
@RequestMapping("/family/cc/character-group")
public class CcCharacterGroupController extends BaseController {
    @Autowired
    private ICcCharacterGroupService ccCharacterGroupService;

    /**
     * 获取章节中汉字的组词信息
     * @param chapterId 章节ID
     * @return
     */
    @GetMapping("{chapterId}")
    public AjaxResult getCharacterGroup(@PathVariable("chapterId") Long chapterId) {
        return ccCharacterGroupService.getCcCharacterGroup(chapterId);
    }
}
