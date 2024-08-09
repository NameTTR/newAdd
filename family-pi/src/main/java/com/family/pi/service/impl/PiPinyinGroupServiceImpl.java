package com.family.pi.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.pi.domain.po.PiPinyin;
import com.family.pi.domain.po.PiPinyinGroup;
import com.family.pi.mapper.PiPinyinGroupMapper;
import com.family.pi.service.IPiPinyinGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 拼音组词表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
@Service
public class PiPinyinGroupServiceImpl extends ServiceImpl<PiPinyinGroupMapper, PiPinyinGroup> implements IPiPinyinGroupService {

    /**
     * 获取单元中拼音的组词信息
     * @param unitId 单元ID
     * @param type 类型
     * @return
     */
    @Override
    public AjaxResult getPiPinyinGroup(Long unitId, Integer type) {
        //1. 根据章节ID查询拼音列表
        List<PiPinyin> pinyins = Db.lambdaQuery(PiPinyin.class)
                .eq(PiPinyin::getUnitId, unitId)
                .eq(PiPinyin::getType, type)
                .list();
        List<Long> pinyinIds = pinyins.stream().map(PiPinyin::getId).collect(Collectors.toList());

        //2. 获取汉字的词组信息
        List<PiPinyinGroup> pinyinGroups = Db.lambdaQuery(PiPinyinGroup.class)
                .in(PiPinyinGroup::getPinyinId, pinyinIds)
                .list();
        Map<Long, List<PiPinyinGroup>> groupMap = pinyinGroups.stream().collect(Collectors.groupingBy(PiPinyinGroup::getPinyinId));

        //3. 组装返回结果
        List<List<PiPinyinGroup>> groupList = new ArrayList<>();
        pinyins.forEach(pinyin -> groupList.add(groupMap.get(pinyin.getId())));


        return AjaxResult.success(groupList);
    }
}
