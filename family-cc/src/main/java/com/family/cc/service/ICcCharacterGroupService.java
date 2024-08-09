package com.family.cc.service;

import com.family.cc.domain.po.CcCharacterGroup;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * <p>
 * 汉字组词表 服务类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
public interface ICcCharacterGroupService extends IService<CcCharacterGroup> {

    /**
     * 获取章节中汉字的组词信息
     * @param chapterId 章节ID
     * @return
     */
    AjaxResult getCcCharacterGroup(Long chapterId);
}
