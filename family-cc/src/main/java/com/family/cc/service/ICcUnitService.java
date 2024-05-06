package com.family.cc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.family.cc.domain.po.CcChapter;
import com.family.cc.domain.po.CcUnit;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * <p>
 * 单元表 服务类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
public interface ICcUnitService extends IService<CcUnit> {

    /**
     * 获取单元列表
     * @return
     */
    AjaxResult getUnitList();

    /**
     * 获取单元详情
     * @param id 获取的单元id
     * @return
     */
    AjaxResult getUnit(Long id);
}
