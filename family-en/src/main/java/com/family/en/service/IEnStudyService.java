package com.family.en.service;

import com.family.en.domain.po.EnStudy;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * <p>
 * 单词学习记录表 服务类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-24
 */
public interface IEnStudyService extends IService<EnStudy> {

    /**
     * 更新单词学习记录
     * @param wordId 汉字id
     * @return
     */
    AjaxResult updateStudyRecord(Long wordId);
}
