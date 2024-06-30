package com.family.pi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.pi.domain.dto.PiPinyinDTO;
import com.family.pi.domain.dto.PiUnitDTO;
import com.family.pi.domain.dto.PiUnitListDTO;
import com.family.pi.domain.po.*;
import com.family.pi.enums.PiUnitState;
import com.family.pi.mapper.PiUnitMapper;
import com.family.pi.service.IPiUnitService;
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
 * 拼音单元表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
@Service
public class PiUnitServiceImpl extends ServiceImpl<PiUnitMapper, PiUnit> implements IPiUnitService {

    /**
     * 获取单元列表 - 并获得用户正在学习的单元名称及其id - 用于首页显示
     * @return
     */
    @Override
    public AjaxResult getUnitList() {
        //获取当前用户的id
//        Long userId = SecurityUtils.getUserId();
        Long userId = 1L;

        //1. 查询所有单元
        List<PiUnit> units = lambdaQuery().orderByAsc(PiUnit::getSort).list();
        List<Long> unitIds = units.stream().map(PiUnit::getId).collect(Collectors.toList());

        //2. 查询用户正在学习的单元
        PiUnit unit = null; //最后找到的正在学习的单元

        //2.1 查询该用户在所有单元学习进度
        List<PiUnitStudy> list = Db.lambdaQuery(PiUnitStudy.class)
                .eq(PiUnitStudy::getUserId, userId)
                .in(PiUnitStudy::getUnitId, unitIds)
                .eq(PiUnitStudy::getState, PiUnitState.LEARNING)
                .list();
        if (list != null && !list.isEmpty()) {
            //2.3 如果该用户在该单元有学习进度，则找到该单元名称并返回
            PiUnitStudy piUnitStudy = list.get(0);
            unit = units.stream().filter(u -> u.getId().equals(piUnitStudy.getUnitId())).findFirst().orElse(null);
        }

        //3. 返回结果
        if (unit == null) {
            unit = units.get(0);
        }
        return AjaxResult.success(PiUnitListDTO.of(unit.getUnit(),unit.getId(),units));
    }

    /**
     * 获取单元信息
     *
     * @param id 获取的单元id
     * @return
     */
    @Override
    public AjaxResult getUnit(Long id) {
        //获取当前用户id
//        Long userId = SecurityUtils.getUserId();
        Long userId = 1L;

        //1. 查询当前单元
        PiUnit unit = getById(id);

        //2 查询当前单元的学习进度
        PiUnitStudy unitStudy = Db.lambdaQuery(PiUnitStudy.class)
                .eq(PiUnitStudy::getUserId, userId)
                .eq(PiUnitStudy::getUnitId, id)
                .one();

        //3.1 查询单元的拼音信息
        List<PiPinyin> pinyins = Db.lambdaQuery(PiPinyin.class)
                .eq(PiPinyin::getUnitId, unit.getId())
                .list();
        List<Long> pinyinIds = new ArrayList<>(pinyins.size());
        pinyins.forEach(c -> pinyinIds.add(c.getId()));

        //3.2 查询拼音的学习情况
        String idsStr = StrUtil.join(",", pinyinIds);
        List<PiStudy> studies = Db.lambdaQuery(PiStudy.class)
                .in(PiStudy::getPinyinId, pinyinIds)
                .eq(PiStudy::getUserId, userId)
                .last("ORDER BY FIELD(id," + idsStr + ")")
                .list();

        //3.3 封装单元信息
        //3.3.1 拼音学习学习情况
        List<PiPinyinDTO> piPinyinDTOS = BeanUtil.copyToList(pinyins, PiPinyinDTO.class);
        //3.3.2 拼音的词组信息
        List<PiPinyinGroup> pinyinGroups = Db.lambdaQuery(PiPinyinGroup.class)
                .in(PiPinyinGroup::getPinyinId, pinyinIds)
                .list();
        Map<Long, List<PiPinyinGroup>> groupMap = pinyinGroups.stream().collect(Collectors.groupingBy(PiPinyinGroup::getPinyinId));

        for (int i = 0; i < piPinyinDTOS.size(); i++) {
            PiPinyinDTO piPinyinDTO = piPinyinDTOS.get(i);
            piPinyinDTO.setState(studies.get(i).getState());

            if (groupMap.get(piPinyinDTO.getId()) == null){
                piPinyinDTO.setCompounds(Collections.emptyList());
                continue;
            }
            List<PiPinyinGroup> compounds = groupMap.get(piPinyinDTO.getId());


            piPinyinDTO.setCompounds(compounds);

        }

        //4. 返回结果
        List<PiPinyinDTO> initials = piPinyinDTOS.stream().filter(p -> p.getType() == 1).collect(Collectors.toList());
        List<PiPinyinDTO> finals = piPinyinDTOS.stream().filter(p -> p.getType() == 2).collect(Collectors.toList());
        List<PiPinyinDTO> combinations = piPinyinDTOS.stream().filter(p -> p.getType() != 1 && p.getType() != 2).collect(Collectors.toList());
        return AjaxResult.success(PiUnitDTO.of(unit, unitStudy.getState(),initials,finals,combinations));
    }
}
