package com.family.pi.service;

import com.family.pi.domain.po.PiPinyinGroup;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * <p>
 * 拼音组词表 服务类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
public interface IPiPinyinGroupService extends IService<PiPinyinGroup> {

    /**
     * 获取单元中拼音的组词信息
     * @param unitId 单元ID
     * @param type 类型
     * @return
     */
    AjaxResult getPiPinyinGroup(Long unitId, Integer type);
}
