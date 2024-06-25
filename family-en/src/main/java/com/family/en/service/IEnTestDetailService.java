package com.family.en.service;

import com.family.en.domain.po.EnTestDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * <p>
 * 单词测试明细表 服务类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-24
 */
public interface IEnTestDetailService extends IService<EnTestDetail> {

    /**
     * 新增测试
     * @param chapterId 章节ID
     * @return
     */
    AjaxResult addTest(Long chapterId);

    /**
     * 获取完成的测试详情
     * @return
     */
    AjaxResult getTestFinished();

    /**
     * 获取 未完成/未测试 的测试详情
     * @return
     */
    AjaxResult getTestNotFinished();

    /**
     * 删除测试记录
     * @param testID
     * @return
     */
    AjaxResult deleteTest(Long testID);
}
