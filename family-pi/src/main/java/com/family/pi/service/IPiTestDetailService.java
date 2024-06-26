package com.family.pi.service;

import com.family.pi.domain.po.PiTestDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * <p>
 * 拼音测试明细表 服务类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
public interface IPiTestDetailService extends IService<PiTestDetail> {

    /**
     * 新增测试
     * @param unitId 单元ID
     * @return
     */
    AjaxResult addTest(Long unitId);

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
