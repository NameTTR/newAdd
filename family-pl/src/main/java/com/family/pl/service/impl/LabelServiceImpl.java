package com.family.pl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.pl.constant.LabelConstants;
import com.family.pl.constant.TaskConstants;
import com.family.pl.domain.Label;
import com.family.pl.domain.VO.AddLabelVO;
import com.family.pl.service.LabelService;
import com.family.pl.mapper.LabelMapper;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author 名字
* @description 针对表【pl_label(标签表)】的数据库操作Service实现
* @createDate 2024-07-05 17:05:15
*/
@Service
public class LabelServiceImpl extends ServiceImpl<LabelMapper, Label>
    implements LabelService{

    @Autowired
    private LabelService labelService;

    @Transactional
    @Override
    public int addLabel(AddLabelVO addLabelVO) {
        Long userId = 1L;
        List<Label> list = labelService.list(new LambdaQueryWrapper<Label>().eq(Label::getUserId, userId));
        for(Label label : list) {
            if(label.getName().equals(addLabelVO.getLabelName())) {
                return LabelConstants.LABEL_NAME_EXIST;
            }
        }
        Label label = new Label();
        label.setName(addLabelVO.getLabelName());
        label.setUserId(1L);
        return 1;
    }
}




