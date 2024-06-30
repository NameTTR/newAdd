package com.family.pi.service;

import com.family.pi.domain.po.PiUnitStudy;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * <p>
 * 单元章节学习记录表 服务类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
public interface IPiUnitStudyService extends IService<PiUnitStudy> {

    /**
     * 更新章节学习记录
     * @param unitId     章节ID
     * @param nextUnitId 下一单元ID
     * @return
     */
    AjaxResult updateUnitStudy(Long unitId, Long nextUnitId);
}
