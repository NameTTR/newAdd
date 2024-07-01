package com.family.th.service;

import com.family.th.domain.po.ThUnit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * <p>
 * 单元表 服务类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-01
 */
public interface IThUnitService extends IService<ThUnit> {

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

    /**
     * 查询单元和对应的章节信息
     * @return
     */
    AjaxResult getUnitChapter();
}
