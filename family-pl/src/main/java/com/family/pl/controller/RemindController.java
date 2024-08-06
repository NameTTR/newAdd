package com.family.pl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.family.pl.domain.TimeRemindDefault;
import com.family.pl.domain.DTO.request.UpdateRemindDefaultDTO;
import com.family.pl.service.TimeRemindDefaultService;
import com.family.pl.service.impl.TimeRemindDefaultServiceImpl;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 提醒控制器，用于处理计划中的提醒设置相关请求。
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-10
 */
@RestController
@RequestMapping("/family/pl/remind")
public class RemindController extends BaseController {

    @Autowired
    private TimeRemindDefaultService timeRemindDefaultService;
    @Autowired
    private TimeRemindDefaultServiceImpl timeRemindDefaultServiceImpl;

    /**
     * 查询用户的默认提醒设置
     *
     * @return AjaxResult 包含查询结果的响应对象
     */
    @GetMapping()
    public AjaxResult selRemindDef(){
        // 默认使用的用户ID，此处应根据实际需求改为动态获取用户ID
        Long userId = 1L;
        // 查询用户的所有默认提醒设置，按类型升序排列
        List<TimeRemindDefault> timeRemindDefaultList = timeRemindDefaultService.list(new LambdaQueryWrapper<TimeRemindDefault>()
                .eq(TimeRemindDefault::getUserId, userId)
                .orderByAsc(TimeRemindDefault::getType));
        Map<String, List<TimeRemindDefault>> data = new HashMap<>();
        data.put("remindDefaultList", timeRemindDefaultList);
        // 返回查询结果
        return AjaxResult.success(data);
    }


    /**
     * 修改用户的默认提醒设置。
     *
     * @param defaultVO 包含待修改提醒设置信息的UpdateRe mindDefaultDTO对象。
     * @return AjaxResult，表示操作的结果。
     */
    @PutMapping()
    public AjaxResult updRemindDef(@RequestBody UpdateRemindDefaultDTO defaultVO){
        return toAjax(timeRemindDefaultService.updateRemindDefault(defaultVO));
    }

}