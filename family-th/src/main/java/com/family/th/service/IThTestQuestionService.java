package com.family.th.service;

import com.family.th.domain.po.ThTestQuestion;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;

import java.util.List;

/**
 * <p>
 * 思维测试题库表 服务类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-01
 */
public interface IThTestQuestionService extends IService<ThTestQuestion> {

    /**
     * 根据测试题ID列表获取测试题信息
     * @param questionIds 测试题ID列表
     * @return
     */
    AjaxResult getThTestQuestion(List<Long> questionIds);
}
