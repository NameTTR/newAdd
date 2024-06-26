package com.family.pi.service;

import com.family.pi.domain.po.PiUnit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * <p>
 * 拼音单元表 服务类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
public interface IPiUnitService extends IService<PiUnit> {

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
