package com.family.th.service;

import com.family.th.domain.po.ThTestDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * <p>
 * 思维测试明细表 服务类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-01
 */
public interface IThTestDetailService extends IService<ThTestDetail> {

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
