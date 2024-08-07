package com.family.cc.controller;


import com.family.cc.service.ICcTestDetailService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 汉字测试明细表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
@RestController
@RequestMapping("/family/cc/test-detail")
public class CcTestDetailController extends BaseController {

    @Autowired
    private ICcTestDetailService ccTestDetailService;

    /**
     * 新增汉字测试
     * @param chapterId 章节ID
     * @return
     */
    @PostMapping("{chapterId}")
    public AjaxResult addTest(@PathVariable("chapterId") Long chapterId) {
        return ccTestDetailService.addTest(chapterId);
    }

    /**
     * 获取完成的测试详情
     * @return
     */
    @GetMapping("finished")
    public AjaxResult getTestFinished() {
        return ccTestDetailService.getTestFinished();
    }

    /**
     * 获取 未完成/未测试 的测试详情
     * @return
     */
    @GetMapping("notFinished")
    public AjaxResult getTestNotFinished() {
        return ccTestDetailService.getTestNotFinished();
    }

    /**
     * 删除测试记录
     * @param testID
     * @return
     */
    @DeleteMapping("{testID}")
    public AjaxResult deleteTest(@PathVariable("testID") Long testID) {
        return ccTestDetailService.deleteTest(testID);
    }
}
