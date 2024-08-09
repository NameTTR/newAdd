package com.family.cc.service;

import com.family.cc.domain.po.CcStudy;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * <p>
 * 汉字学习记录表 服务类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
public interface ICcStudyService extends IService<CcStudy> {

    /**
     * 更新汉字学习记录
     * @param characterId 汉字id
     * @return
     */
    AjaxResult updateStudyRecord(Long characterId);

    /**
     * 新增用户汉字学习记录
     * @return
     */
    AjaxResult addStudyRecord();
}
