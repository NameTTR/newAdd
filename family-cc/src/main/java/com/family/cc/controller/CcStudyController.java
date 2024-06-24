package com.family.cc.controller;


import com.family.cc.service.ICcStudyService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 汉字学习记录表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
@RestController
@RequestMapping("/family/cc/study")
public class CcStudyController extends BaseController {
    @Autowired
    private ICcStudyService ccStudyService;

    /**
     * 更新汉字学习记录
     * @param characterId 汉字id
     * @return
     */
    @PutMapping("{characterId}")
    public AjaxResult updateStudyRecord(@PathVariable("characterId") Long characterId) {
        return ccStudyService.updateStudyRecord(characterId);
    }
}
