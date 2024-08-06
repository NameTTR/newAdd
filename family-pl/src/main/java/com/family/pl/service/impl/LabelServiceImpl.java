package com.family.pl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.pl.constant.LabelConstants;
import com.family.pl.domain.Label;
import com.family.pl.domain.DTO.request.AddLabelDTO;
import com.family.pl.service.LabelService;
import com.family.pl.mapper.LabelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 针对表【pl_label(标签表)】的数据库操作Service实现
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
@Service
public class LabelServiceImpl extends ServiceImpl<LabelMapper, Label>
        implements LabelService {

    @Autowired
    private LabelService labelService;

    /**
     * 添加标签信息。
     *
     * @param addLabelDTO 包含新标签名称的数据传输对象。
     * @return 如果标签名称已存在，返回常量LABEL_NAME_EXIST；否则，成功添加标签后返回1。
     */
    @Transactional
    @Override
    public int addLabel(AddLabelDTO addLabelDTO) {
        // 默认用户ID，用于测试或示例。实际应用中应从当前登录用户获取。
        Long userId = 1L;

        // 查询已存在的标签列表，判断是否重名。
        List<Label> list = labelService.list(new LambdaQueryWrapper<Label>().eq(Label::getUserId, userId));
        for (Label label : list) {
            // 如果存在重名标签，则返回标签名称已存在的常量值。
            if (label.getName().equals(addLabelDTO.getLabelName())) {
                return LabelConstants.LABEL_NAME_EXIST;
            }
        }

        // 创建新标签对象，并设置名称和用户ID。
        Label label = new Label();
        label.setName(addLabelDTO.getLabelName());
        label.setUserId(userId);

        // 保存新标签。
        labelService.save(label);

        // 标签添加成功，返回1。
        return 1;
    }

}




