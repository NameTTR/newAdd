package com.family.th.controller;


import com.family.th.domain.dto.ThTestDetailsDTO;
import com.family.th.domain.po.ThTestDetail;
import com.family.th.service.IThTestDetailService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 思维测试明细表 前端控制器
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-01
 */
@RestController
@RequestMapping("/family/th/test-detail")
public class ThTestDetailController {
    @Autowired
    private IThTestDetailService thTestDetailService;

    /**
     * 新增汉字测试
     * @param chapterId 章节ID
     * @return
     */
    @PostMapping("{chapterId}")
    public AjaxResult addTest(@PathVariable("chapterId") Long chapterId) {
        return thTestDetailService.addTest(chapterId);
    }

    /**
     * 获取完成的测试详情
     * @return
     */
    @GetMapping("finished")
    public AjaxResult getTestFinished() {
        return thTestDetailService.getTestFinished();
    }

    /**
     * 获取 未完成/未测试 的测试详情
     * @return
     */
    @GetMapping("notFinished")
    public AjaxResult getTestNotFinished() {
        return thTestDetailService.getTestNotFinished();
    }

    /**
     * 删除测试记录
     * @param testID
     * @return
     */
    @DeleteMapping("{testID}")
    public AjaxResult deleteTest(@PathVariable("testID") Long testID) {
        return thTestDetailService.deleteTest(testID);
    }


    /**
     * 更新测试记录
     * @param testDetailsDTO 测试记录
     * @return
     */
    @PutMapping()
    public AjaxResult updateTest(@RequestBody ThTestDetailsDTO testDetailsDTO) {
        return thTestDetailService.updateTest(testDetailsDTO);
    }

}
