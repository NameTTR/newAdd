package com.family.en.controller;


import com.family.en.service.IEnTestDetailService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 单词测试明细表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-24
 */
@RestController
@RequestMapping("/family/en/test-detail")
public class EnTestDetailController {

    @Autowired
    private IEnTestDetailService enTestDetailService;

    /**
     * 新增单词测试
     * @param chapterId 章节ID
     * @return
     */
    @PostMapping("{chapterId}")
    public AjaxResult addTest(@PathVariable("chapterId") Long chapterId) {
        return enTestDetailService.addTest(chapterId);
    }

    /**
     * 获取完成的测试详情
     * @return
     */
    @GetMapping("finished")
    public AjaxResult getTestFinished() {
        return enTestDetailService.getTestFinished();
    }

    /**
     * 获取 未完成/未测试 的测试详情
     * @return
     */
    @GetMapping("notFinished")
    public AjaxResult getTestNotFinished() {
        return enTestDetailService.getTestNotFinished();
    }

    /**
     * 删除测试记录
     * @param testID
     * @return
     */
    @DeleteMapping("{testID}")
    public AjaxResult deleteTest(@PathVariable("testID") Long testID) {
        return enTestDetailService.deleteTest(testID);
    }
}
