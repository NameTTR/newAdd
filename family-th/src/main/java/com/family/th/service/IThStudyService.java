package com.family.th.service;

import com.family.th.domain.po.ThStudy;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * <p>
 * 思维学习记录表 服务类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-01
 */
public interface IThStudyService extends IService<ThStudy> {

    /**
     * 更新汉字学习记录
     * @param thinkingId 汉字id
     * @return
     */
    AjaxResult updateStudyRecord(Long thinkingId);
}
