package com.family.pl.service;

import com.family.pl.domain.Label;
import com.baomidou.mybatisplus.extension.service.IService;
import com.family.pl.domain.VO.AddLabelVO;

/**
* @author 名字
* @description 针对表【pl_label(标签表)】的数据库操作Service
* @createDate 2024-07-05 17:05:15
*/
public interface LabelService extends IService<Label> {

    int addLabel(AddLabelVO addLabelVO);
}
