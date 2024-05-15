package com.family.cc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.cc.domain.dto.*;
import com.family.cc.domain.po.*;
import com.family.cc.enums.CcChapterState;
import com.family.cc.mapper.CcUnitMapper;
import com.family.cc.service.*;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 单元表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
@Service
public class CcUnitServiceImpl extends ServiceImpl<CcUnitMapper, CcUnit> implements ICcUnitService {

    /**
     * 获取单元列表
     * @return
     */
    @Override
    public AjaxResult getUnitList() {
        //获取当前用户的id
//        Long userId = SecurityUtils.getUserId();
        Long userId = 1L;

        //1. 查询所有单元
        List<CcUnit> units = lambdaQuery().orderByAsc(CcUnit::getSort).list();

        //2. 查询用户正在学习的单元 -- 只适用于数据量少的情况
        String unitName = ""; //最后找到的正在学习的单元名称
        for (CcUnit unit : units) {
            //2.1 查询该单元下的所有章节
            List<CcChapter> chapters = Db.lambdaQuery(CcChapter.class)
                    .eq(CcChapter::getUnitId, unit.getId())
                    .list();
            //2.2 查询该用户在该单元下的所有章节学习进度
            int sign = 0;
            for (CcChapter chapter : chapters) {
                List<CcChapterStudy> studies = Db.lambdaQuery(CcChapterStudy.class)
                        .eq(CcChapterStudy::getChapterId, chapter.getId())
                        .eq(CcChapterStudy::getUserId, userId)
                        .eq(CcChapterStudy::getState, CcChapterState.LEARNING)
                        .list();
                //2.3 如果该用户在该章节有学习进度，则找到该单元名称并返回
                if (!studies.isEmpty()){
                    unitName = unit.getUnit();
                    sign = 1;
                    break;
                }
            }
            if (sign == 1) break;
        }

        //3. 返回结果
        if (unitName.isEmpty()) {
            unitName = units.get(0).getUnit();
            return AjaxResult.success(CcUnitListDTO.of(unitName, units));
        }else {
            return AjaxResult.success(CcUnitListDTO.of(unitName, units));
        }
    }

    /**
     * 获取单元信息
     * @param id 获取的单元id
     * @return
     */
    @Override
    public AjaxResult getUnit(Long id) {
        //获取当前用户id
//        Long userId = SecurityUtils.getUserId();
        Long userId = 1L;

        //1. 查询当前单元
        CcUnit unit = getById(id);

        // 用于存放单元信息
        List<CcChapterDTO> unitDate = new ArrayList<>();
        //2. 查询当前单元的所有章节
        List<CcChapter> chapters = Db.lambdaQuery(CcChapter.class)
                .eq(CcChapter::getUnitId, id)
                .orderByAsc(CcChapter::getSort)
                .list();

        //3. 查询所有章节的汉字信息
        for (CcChapter chapter : chapters) {
            //3.1 查询当前章节的汉字信息
            List<CcCharacter> characters = Db.lambdaQuery(CcCharacter.class)
                    .eq(CcCharacter::getChapterId, chapter.getId())
                    .list();
            List<Long> characterIds = new ArrayList<>(characters.size());
            characters.forEach(c -> characterIds.add(c.getId()));

            //3.2 查询汉字的学习情况
            String idsStr = StrUtil.join(",", characterIds);
            List<CcStudy> studies = Db.lambdaQuery(CcStudy.class)
                    .in(CcStudy::getCharacterId, characterIds)
                    .eq(CcStudy::getUserId, userId)
                    .last("ORDER BY FIELD(id," + idsStr + ")")
                    .list();

            //3.3 封装章节信息
            List<CcCharacterDTO> ccCharacterDTOS = BeanUtil.copyToList(characters, CcCharacterDTO.class);
            for (int i = 0; i < ccCharacterDTOS.size(); i++) {
                ccCharacterDTOS.get(i).setState(studies.get(i).getState());
            }

            //3.4 放入单元信息
            unitDate.add(CcChapterDTO.of(chapter, ccCharacterDTOS));
        }

        //3. 返回结果
        return AjaxResult.success(CcUnitDTO.of(unit,unitDate));
    }

    @Override
    public AjaxResult getUnitChapter() {
        //1. 获取单元信息
        List<CcUnit> units = lambdaQuery().orderByAsc(CcUnit::getSort).list();
        List<Long> unitIds = units.stream().map(CcUnit::getId).collect(Collectors.toList());

        //2. 获取章节信息
        List<CcChapter> chapters = Db.lambdaQuery(CcChapter.class).in(CcChapter::getUnitId, unitIds).orderByAsc(CcChapter::getSort).list();
        Map<Long, List<CcChapter>> listMap= chapters.stream().collect(Collectors.groupingBy(CcChapter::getUnitId));

        //3. 返回结果
        List<CcUnitChapterDTO> unitChapterDTOList = new ArrayList<>(units.size());
        for (CcUnit unit : units) {
            CcUnitChapterDTO unitChapterDTO = CcUnitChapterDTO.of(unit, listMap.get(unit.getId()));
            unitChapterDTOList.add(unitChapterDTO);
        }
        return AjaxResult.success(unitChapterDTOList);
    }
}
