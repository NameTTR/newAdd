package com.family.pl.service;

import com.family.pl.domain.Label;
import com.baomidou.mybatisplus.extension.service.IService;
import com.family.pl.domain.DTO.request.AddLabelDTO;

/**
 * <p>
 * 针对表【pl_label(标签表)】的数据库操作Service
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
public interface LabelService extends IService<Label> {

    /**
     * 添加标签信息。
     *
     * @param addLabelDTO 包含新标签名称的数据传输对象。
     * @return 如果标签名称已存在，返回常量LABEL_NAME_EXIST；否则，成功添加标签后返回1。
     */
    int addLabel(AddLabelDTO addLabelDTO);
}
