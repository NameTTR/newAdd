package com.family.cc.service.impl;

import cn.hutool.core.bean.BeanUtil;
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
import java.util.Collections;
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
     * 获取单元列表 - 并获得用户正在学习的单元名称及其id - 用于首页显示
     * @return
     */
    @Override
    public AjaxResult getUnitList() {
        //获取当前用户的id
//        Long userId = SecurityUtils.getUserId();
        Long userId = 1L;

        //1. 查询所有单元
        List<CcUnit> units = lambdaQuery().orderByAsc(CcUnit::getSort).list();
        List<Long> unitIds = units.stream().map(CcUnit::getId).collect(Collectors.toList());

        //2. 查询用户正在学习的单元
        CcUnit unit = null; //最后找到的正在学习的单元

        //2.1 查询所有单元下的所有章节
        List<CcChapter> chapters = Db.lambdaQuery(CcChapter.class).in(CcChapter::getUnitId, unitIds).list();
        List<Long> ids = chapters.stream().map(CcChapter::getId).collect(Collectors.toList());

        //2.2 查询该用户在该单元下的所有章节学习进度
        List<CcChapterStudy> list = Db.lambdaQuery(CcChapterStudy.class)
                .eq(CcChapterStudy::getUserId, userId)
                .in(CcChapterStudy::getChapterId, ids)
                .eq(CcChapterStudy::getState, CcChapterState.LEARNING)
                .list();
        if (list != null && !list.isEmpty()) {
            //2.3 如果该用户在该章节有学习进度，则找到该单元名称并返回
            CcChapterStudy chapterStudy = list.get(0);
            CcChapter chapter = chapters.stream().filter(c -> c.getId().equals(chapterStudy.getChapterId())).findFirst().get();
            unit = units.stream().filter(u -> u.getId().equals(chapter.getUnitId())).findFirst().get();
        }

        //3. 返回结果
        if (unit == null) {
            unit = units.get(0);
        }
        return AjaxResult.success(CcUnitListDTO.of(unit.getUnit(),unit.getId(),units));
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
        CcUnit unit = getById(id);

        // 用于存放单元信息
        List<CcChapterDTO> unitDate = new ArrayList<>();
        //2. 查询当前单元的所有章节
        List<CcChapter> chapters = Db.lambdaQuery(CcChapter.class)
                .eq(CcChapter::getUnitId, id)
                .orderByAsc(CcChapter::getSort)
                .list();
        //2.1 查询当前单元下的所有章节的学习进度
        List<Long> ids = chapters.stream().map(CcChapter::getId).collect(Collectors.toList());
        List<CcChapterStudy> chapterStudies = Db.lambdaQuery(CcChapterStudy.class)
                .eq(CcChapterStudy::getUserId, userId)
                .in(CcChapterStudy::getChapterId, ids)
                .list();
        Map<Long, CcChapterStudy> chapterStudyMap = chapterStudies.stream().collect(Collectors.toMap(CcChapterStudy::getChapterId, c -> c));

        //3. 查询所有章节的汉字信息
        //3.1 查询当前单元下的所有章节的汉字信息
        List<Long> chapterIds = chapters.stream().map(CcChapter::getId).collect(Collectors.toList());

        // 需要的汉字信息
        List<CcCharacter> characters = Db.lambdaQuery(CcCharacter.class)
                .in(CcCharacter::getChapterId, chapterIds)
                .list();
        List<Long> characterIds = characters.stream().map(CcCharacter::getId).collect(Collectors.toList());

        // 需要的汉字学习记录
        List<CcStudy> studies = Db.lambdaQuery(CcStudy.class)
                .in(CcStudy::getCharacterId, characterIds)
                .eq(CcStudy::getUserId, userId)
                .list();
        Map<Long, CcStudy> studyMap = studies.stream().collect(Collectors.toMap(CcStudy::getCharacterId, c -> c));

        //3.2 封装单元信息
        //3.2.1 章节信息
        List<CcCharacterDTO> ccCharacterDTOS = BeanUtil.copyToList(characters, CcCharacterDTO.class);
        ccCharacterDTOS.forEach(c -> c.setState(studyMap.get(c.getId()).getState()));

        //3.3 按章节将汉字分组
        Map<Long, List<CcCharacterDTO>> chapterMapOfCharacterDTOS = ccCharacterDTOS.stream().collect(Collectors.groupingBy(CcCharacterDTO::getChapterId));
        for (CcChapter chapter : chapters){
            List<CcCharacterDTO> characterDTOS = chapterMapOfCharacterDTOS.get(chapter.getId());
            unitDate.add(CcChapterDTO.of(chapter,chapterStudyMap.get(chapter.getId()).getState(), characterDTOS));
        }

        //4. 返回结果
        return AjaxResult.success(CcUnitDTO.of(unit, unitDate));
    }

    /**
     * 查询单元和对应的章节信息
     * @return
     */
    @Override
    public AjaxResult getUnitChapter() {
        //1. 获取单元信息
        List<CcUnit> units = lambdaQuery().orderByAsc(CcUnit::getSort).list();
        List<Long> unitIds = units.stream().map(CcUnit::getId).collect(Collectors.toList());

        //2. 获取章节信息
        List<CcChapter> chapters = Db.lambdaQuery(CcChapter.class).in(CcChapter::getUnitId, unitIds).orderByAsc(CcChapter::getSort).list();
        Map<Long, List<CcChapter>> listMap = chapters.stream().collect(Collectors.groupingBy(CcChapter::getUnitId));

        //3. 返回结果
        List<CcUnitChapterDTO> unitChapterDTOList = new ArrayList<>(units.size());
        for (CcUnit unit : units) {
            CcUnitChapterDTO unitChapterDTO = CcUnitChapterDTO.of(unit, listMap.get(unit.getId()));
            unitChapterDTOList.add(unitChapterDTO);
        }
        return AjaxResult.success(unitChapterDTOList);
    }
}
