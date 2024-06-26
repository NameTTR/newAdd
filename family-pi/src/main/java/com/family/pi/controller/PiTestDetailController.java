package com.family.pi.controller;


import com.family.pi.service.IPiTestDetailService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 拼音测试明细表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
@RestController
@RequestMapping("/family/pi/test-detail")
public class PiTestDetailController {
    @Autowired
    private IPiTestDetailService piTestDetailService;

    /**
     * 新增汉字测试
     * @param unitId 单元ID
     * @return
     */
    @PostMapping("{unitId}")
    public AjaxResult addTest(@PathVariable("unitId") Long unitId) {
        return piTestDetailService.addTest(unitId);
    }

    /**
     * 获取完成的测试详情
     * @return
     */
    @GetMapping("finished")
    public AjaxResult getTestFinished() {
        return piTestDetailService.getTestFinished();
    }

    /**
     * 获取 未完成/未测试 的测试详情
     * @return
     */
    @GetMapping("notFinished")
    public AjaxResult getTestNotFinished() {
        return piTestDetailService.getTestNotFinished();
    }

    /**
     * 删除测试记录
     * @param testID
     * @return
     */
    @DeleteMapping("{testID}")
    public AjaxResult deleteTest(@PathVariable("testID") Long testID) {
        return piTestDetailService.deleteTest(testID);
    }
}
