package com.family.th.service.impl;

import cn.hutool.core.util.StrUtil;
import com.family.th.domain.po.ThTestQuestion;
import com.family.th.mapper.ThTestQuestionMapper;
import com.family.th.service.IThTestQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 思维测试题库表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-01
 */
@Service
public class ThTestQuestionServiceImpl extends ServiceImpl<ThTestQuestionMapper, ThTestQuestion> implements IThTestQuestionService {

    /**
     * 根据测试题ID列表获取测试题信息
     * @param questionIds 测试题ID列表
     * @return
     */
    @Override
    public AjaxResult getThTestQuestion(List<Long> questionIds) {
        String idsStr = StrUtil.join(",", questionIds);

        List<ThTestQuestion> list = lambdaQuery()
                .in(ThTestQuestion::getId, questionIds)
                .last("ORDER BY FIELD(id," + idsStr + ")")
                .list();

        return AjaxResult.success(list);
    }
}
