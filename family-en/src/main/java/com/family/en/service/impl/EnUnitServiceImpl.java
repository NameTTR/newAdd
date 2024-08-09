package com.family.en.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.en.domain.dto.*;
import com.family.en.domain.po.*;
import com.family.en.enums.EnChapterState;
import com.family.en.mapper.EnUnitMapper;
import com.family.en.service.IEnUnitService;
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
 * 单词单元表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-24
 */
@Service
public class EnUnitServiceImpl extends ServiceImpl<EnUnitMapper, EnUnit> implements IEnUnitService {

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
        List<EnUnit> units = lambdaQuery().orderByAsc(EnUnit::getSort).list();
        List<Long> unitIds = units.stream().map(EnUnit::getId).collect(Collectors.toList());

        //2. 查询用户正在学习的单元
        EnUnit unit = null; //最后找到的正在学习的单元

        //2.1 查询所有单元下的所有章节
        List<EnChapter> chapters = Db.lambdaQuery(EnChapter.class).in(EnChapter::getUnitId, unitIds).list();
        List<Long> ids = chapters.stream().map(EnChapter::getId).collect(Collectors.toList());

        //2.2 查询该用户在该单元下的所有章节学习进度
        List<EnChapterStudy> list = Db.lambdaQuery(EnChapterStudy.class)
                .eq(EnChapterStudy::getUserId, userId)
                .in(EnChapterStudy::getChapterId, ids)
                .eq(EnChapterStudy::getState, EnChapterState.LEARNING)
                .list();
        if (list != null && !list.isEmpty()) {
            //2.3 如果该用户在该章节有学习进度，则找到该单元名称并返回
            EnChapterStudy chapterStudy = list.get(0);
            EnChapter chapter = chapters.stream().filter(c -> c.getId().equals(chapterStudy.getChapterId())).findFirst().get();
            unit = units.stream().filter(u -> u.getId().equals(chapter.getUnitId())).findFirst().get();
        }

        //3. 返回结果
        if (unit == null) {
            unit = units.get(0);
        }
        return AjaxResult.success(EnUnitListDTO.of(unit.getUnit(),unit.getId(),units));

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
        EnUnit unit = getById(id);

        // 用于存放单元信息
        List<EnChapterDTO> unitDate = new ArrayList<>();
        //2. 查询当前单元的所有章节
        List<EnChapter> chapters = Db.lambdaQuery(EnChapter.class)
                .eq(EnChapter::getUnitId, id)
                .orderByAsc(EnChapter::getSort)
                .list();
        //2.1 查询当前单元下的所有章节的学习进度
        List<Long> ids = chapters.stream().map(EnChapter::getId).collect(Collectors.toList());
        List<EnChapterStudy> chapterStudies = Db.lambdaQuery(EnChapterStudy.class)
                .eq(EnChapterStudy::getUserId, userId)
                .in(EnChapterStudy::getChapterId, ids)
                .list();
        Map<Long, EnChapterStudy> chapterStudyMap = chapterStudies.stream().collect(Collectors.toMap(EnChapterStudy::getChapterId, c -> c));

        //3. 查询所有章节的单词信息
        //3.1 查询当前单元下的所有章节的单词信息
        List<Long> chapterIds = chapters.stream().map(EnChapter::getId).collect(Collectors.toList());

        // 需要的单词信息
        List<EnWord> words = Db.lambdaQuery(EnWord.class)
                .in(EnWord::getChapterId, chapterIds)
                .list();
        List<Long> wordIds = words.stream().map(EnWord::getId).collect(Collectors.toList());

        // 需要的单词学习记录
        List<EnStudy> studies = Db.lambdaQuery(EnStudy.class)
                .in(EnStudy::getWordId, wordIds)
                .eq(EnStudy::getUserId, userId)
                .list();
        Map<Long, EnStudy> studyMap = studies.stream().collect(Collectors.toMap(EnStudy::getWordId, c -> c));

        //3.2 封装单元信息
        //3.2.1 章节信息
        List<EnWordDTO> EnWordDTOS = BeanUtil.copyToList(words, EnWordDTO.class);
        EnWordDTOS.forEach(c -> c.setState(studyMap.get(c.getId()).getState()));
        //3.3 按章节将单词分组
        Map<Long, List<EnWordDTO>> chapterMapOfwordDTOS = EnWordDTOS.stream().collect(Collectors.groupingBy(EnWordDTO::getChapterId));
        for (EnChapter chapter : chapters){
            List<EnWordDTO> wordDTOS = chapterMapOfwordDTOS.get(chapter.getId());
            unitDate.add(EnChapterDTO.of(chapter,chapterStudyMap.get(chapter.getId()).getState(), wordDTOS));
        }

        //4. 返回结果
        return AjaxResult.success(EnUnitDTO.of(unit, unitDate));
    }

    /**
     * 查询单元和对应的章节信息
     * @return
     */
    @Override
    public AjaxResult getUnitChapter() {
        //1. 获取单元信息
        List<EnUnit> units = lambdaQuery().orderByAsc(EnUnit::getSort).list();
        List<Long> unitIds = units.stream().map(EnUnit::getId).collect(Collectors.toList());

        //2. 获取章节信息
        List<EnChapter> chapters = Db.lambdaQuery(EnChapter.class).in(EnChapter::getUnitId, unitIds).orderByAsc(EnChapter::getSort).list();
        Map<Long, List<EnChapter>> listMap = chapters.stream().collect(Collectors.groupingBy(EnChapter::getUnitId));

        //3. 返回结果
        List<EnUnitChapterDTO> unitChapterDTOList = new ArrayList<>(units.size());
        for (EnUnit unit : units) {
            EnUnitChapterDTO unitChapterDTO = EnUnitChapterDTO.of(unit, listMap.get(unit.getId()));
            unitChapterDTOList.add(unitChapterDTO);
        }
        return AjaxResult.success(unitChapterDTOList);
    }
}
