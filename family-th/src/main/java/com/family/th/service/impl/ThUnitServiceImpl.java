package com.family.th.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.th.domain.dto.*;
import com.family.th.domain.po.*;
import com.family.th.enums.ThChapterState;
import com.family.th.mapper.ThUnitMapper;
import com.family.th.service.IThUnitService;
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
 * 单元表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-01
 */
@Service
public class ThUnitServiceImpl extends ServiceImpl<ThUnitMapper, ThUnit> implements IThUnitService {

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
        List<ThUnit> units = lambdaQuery().orderByAsc(ThUnit::getSort).list();
        List<Long> unitIds = units.stream().map(ThUnit::getId).collect(Collectors.toList());

        //2. 查询用户正在学习的单元
        ThUnit unit = null; //最后找到的正在学习的单元

        //2.1 查询所有单元下的所有章节
        List<ThChapter> chapters = Db.lambdaQuery(ThChapter.class).in(ThChapter::getUnitId, unitIds).list();
        List<Long> ids = chapters.stream().map(ThChapter::getId).collect(Collectors.toList());

        //2.2 查询该用户在该单元下的所有章节学习进度
        List<ThChapterStudy> list = Db.lambdaQuery(ThChapterStudy.class)
                .eq(ThChapterStudy::getUserId, userId)
                .in(ThChapterStudy::getChapterId, ids)
                .eq(ThChapterStudy::getState, ThChapterState.LEARNING)
                .list();
        if (list != null && !list.isEmpty()) {
            //2.3 如果该用户在该章节有学习进度，则找到该单元名称并返回
            ThChapterStudy chapterStudy = list.get(0);
            ThChapter chapter = chapters.stream().filter(c -> c.getId().equals(chapterStudy.getChapterId())).findFirst().get();
            unit = units.stream().filter(u -> u.getId().equals(chapter.getUnitId())).findFirst().get();
        }

        //3. 返回结果
        if (unit == null) {
            unit = units.get(0);
        }
        return AjaxResult.success(ThUnitListDTO.of(unit.getUnit(),unit.getId(),units));
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
        ThUnit unit = getById(id);

        // 用于存放单元信息
        List<ThChapterDTO> unitDate = new ArrayList<>();
        //2. 查询当前单元的所有章节
        List<ThChapter> chapters = Db.lambdaQuery(ThChapter.class)
                .eq(ThChapter::getUnitId, id)
                .orderByAsc(ThChapter::getSort)
                .list();
        //2.1 查询当前单元下的所有章节的学习进度
        List<Long> ids = chapters.stream().map(ThChapter::getId).collect(Collectors.toList());
        List<ThChapterStudy> chapterStudies = Db.lambdaQuery(ThChapterStudy.class)
                .eq(ThChapterStudy::getUserId, userId)
                .in(ThChapterStudy::getChapterId, ids)
                .list();
        Map<Long, ThChapterStudy> chapterStudyMap = chapterStudies.stream().collect(Collectors.toMap(ThChapterStudy::getChapterId, c -> c));

/*
        //3. 查询所有章节的个体信息
        for (ThChapter chapter : chapters) {
            //3.1 查询当前章节的个体信息
            List<ThThinking> thinkings = Db.lambdaQuery(ThThinking.class)
                    .eq(ThThinking::getChapterId, chapter.getId())
                    .list();
            List<Long> thinkingIds = new ArrayList<>(thinkings.size());
            thinkings.forEach(c -> thinkingIds.add(c.getId()));

            //3.2 查询个体的学习情况
            String idsStr = StrUtil.join(",", thinkingIds);
            List<ThStudy> studies = Db.lambdaQuery(ThStudy.class)
                    .in(ThStudy::getThinkingId, thinkingIds)
                    .eq(ThStudy::getUserId, userId)
                    .last("ORDER BY FIELD(id," + idsStr + ")")
                    .list();

            //3.3 封装章节信息
            //3.3.1 个体学习学习情况
            List<ThThinkingDTO> ThThinkingDTOS = BeanUtil.copyToList(thinkings, ThThinkingDTO.class);

            for (int i = 0; i < ThThinkingDTOS.size(); i++) {
                ThThinkingDTOS.get(i).setState(studies.get(i).getState());
            }

            //3.4 放入单元信息
            unitDate.add(ThChapterDTO.of(chapter,chapterStudyMap.get(chapter.getId()).getState(),ThThinkingDTOS));
        }
*/
        //3. 查询所有章节的汉字信息
        //3.1 查询当前单元下的所有章节的汉字信息
        List<Long> chapterIds = chapters.stream().map(ThChapter::getId).collect(Collectors.toList());

        // 需要的汉字信息
        List<ThThinking> characters = Db.lambdaQuery(ThThinking.class)
                .in(ThThinking::getChapterId, chapterIds)
                .list();
        List<Long> characterIds = characters.stream().map(ThThinking::getId).collect(Collectors.toList());

        // 需要的汉字学习记录
        List<ThStudy> studies = Db.lambdaQuery(ThStudy.class)
                .in(ThStudy::getThinkingId, characterIds)
                .eq(ThStudy::getUserId, userId)
                .list();
        Map<Long, ThStudy> studyMap = studies.stream().collect(Collectors.toMap(ThStudy::getThinkingId, c -> c));

        //3.2 封装单元信息
        //3.2.1 章节信息
        List<ThThinkingDTO> ccCharacterDTOS = BeanUtil.copyToList(characters, ThThinkingDTO.class);
        ccCharacterDTOS.forEach(c -> c.setState(studyMap.get(c.getId()).getState()));
        //3.3 按章节将汉字分组
        Map<Long, List<ThThinkingDTO>> chapterMapOfCharacterDTOS = ccCharacterDTOS.stream().collect(Collectors.groupingBy(ThThinkingDTO::getChapterId));
        for (ThChapter chapter : chapters){
            List<ThThinkingDTO> characterDTOS = chapterMapOfCharacterDTOS.get(chapter.getId());
            unitDate.add(ThChapterDTO.of(chapter,chapterStudyMap.get(chapter.getId()).getState(), characterDTOS));
        }

        //4. 返回结果
        return AjaxResult.success(ThUnitDTO.of(unit, unitDate));
    }

    /**
     * 查询单元和对应的章节信息
     * @return
     */
    @Override
    public AjaxResult getUnitChapter() {
        //1. 获取单元信息
        List<ThUnit> units = lambdaQuery().orderByAsc(ThUnit::getSort).list();
        List<Long> unitIds = units.stream().map(ThUnit::getId).collect(Collectors.toList());

        //2. 获取章节信息
        List<ThChapter> chapters = Db.lambdaQuery(ThChapter.class).in(ThChapter::getUnitId, unitIds).orderByAsc(ThChapter::getSort).list();
        Map<Long, List<ThChapter>> listMap = chapters.stream().collect(Collectors.groupingBy(ThChapter::getUnitId));

        //3. 返回结果
        List<ThUnitChapterDTO> unitChapterDTOList = new ArrayList<>(units.size());
        for (ThUnit unit : units) {
            ThUnitChapterDTO unitChapterDTO = ThUnitChapterDTO.of(unit, listMap.get(unit.getId()));
            unitChapterDTOList.add(unitChapterDTO);
        }
        return AjaxResult.success(unitChapterDTOList);
    }
}
