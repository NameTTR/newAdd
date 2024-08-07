package com.family.pi.service;

import com.family.pi.domain.po.PiStudy;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * <p>
 * 拼音学习记录表 服务类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
public interface IPiStudyService extends IService<PiStudy> {

    /**
     * 更新拼音学习记录
     * @param pinyinId 拼音id
     * @return
     */
    AjaxResult updateStudyRecord(Long pinyinId);
}
