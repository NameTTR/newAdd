package com.family.pl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.family.pl.constant.LabelConstants;
import com.family.pl.constant.TaskConstants;
import com.family.pl.domain.Label;
import com.family.pl.domain.VO.AddLabelVO;
import com.family.pl.service.LabelService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/7/9 11:19
 */
@RestController
@RequestMapping("/family/label")
public class LabelController extends BaseController {

    @Autowired
    private LabelService labelService;

    /**
     * 查询标签库
     * @return
     */
    @GetMapping("selectlabel")
    public AjaxResult selectLabel() {
        Long userId = 1L;
        return AjaxResult.success(labelService.list(new LambdaQueryWrapper<Label>().eq(Label::getUserId, userId)));
    }

    /**
     * 添加标签至标签库
     * @param addLabelVO
     * @return
     */
    @PostMapping("addlabel")
    public AjaxResult addLabel(@RequestBody AddLabelVO addLabelVO) {
        int flag = labelService.addLabel(addLabelVO);
        if(flag == LabelConstants.LABEL_NAME_EXIST) {
            return AjaxResult.error("标签已存在");
        }
        return toAjax(labelService.addLabel(addLabelVO));
    }

    /**
     * 从标签库删除标签
     * @param labelId
     */
    @DeleteMapping("dellabel")
    public AjaxResult delLabel(@RequestParam(value = "labelId") Long labelId) {
        return toAjax(labelService.removeById(labelId));
    }

}