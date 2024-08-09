package com.family.en.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.en.domain.dto.EnTestDetailsDTO;
import com.family.en.domain.po.*;
import com.family.en.enums.EnTestState;
import com.family.en.enums.EnWordTestState;
import com.family.en.mapper.EnTestDetailMapper;
import com.family.en.service.IEnTestDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 单词测试明细表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-24
 */
@Service
public class EnTestDetailServiceImpl extends ServiceImpl<EnTestDetailMapper, EnTestDetail> implements IEnTestDetailService {

    /**
     * 新增单词测试
     * @param chapterId 章节ID
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AjaxResult addTest(Long chapterId) {
        try {
            //1. 获取用户id
//        Long userId = SecurityUtils.getUserId();
            Long userId = 1L;

            //2. 获取章节信息
            EnChapter chapter = Db.getById(chapterId, EnChapter.class);

            //3. 获取单元信息
            EnUnit unit = Db.getById(chapter.getUnitId(), EnUnit.class);

            //4. 新增测试信息
            EnTest test = new EnTest();
            test.setUserId(userId);
            test.setChapterId(chapterId);
            test.setState(EnTestState.NOTFINISHED);
            test.setCreatedTime(LocalDateTime.now());
            boolean isSuccess1 = Db.save(test);

            if (!isSuccess1){
                return AjaxResult.error("新增测试记录失败，请重试！");
            }

            //5.1 构建测试明细信息
            List<EnWord> Words = Db.lambdaQuery(EnWord.class).eq(EnWord::getChapterId, chapterId).list();
            List<EnTestDetail> testDetails = new ArrayList<>(Words.size());
            for (EnWord Word : Words) {
                EnTestDetail testDetail = new EnTestDetail();
                testDetail.setUserId(userId);
                testDetail.setTestId(test.getId());
                testDetail.setWordId(Word.getId());
                testDetail.setWord(Word.getWord());
                testDetail.setResult(EnWordTestState.NOTFINISHED);
                testDetail.setCreatedTime(LocalDateTime.now());
                testDetails.add(testDetail);
            }
            //5.2 新增测试明细信息
            boolean isSuccess2 = saveBatch(testDetails);
            if (!isSuccess2){
                throw new RuntimeException("新增测试记录失败，请重试！");
            }

            //6. 构建返回结果
            EnTestDetailsDTO testDetailsDTO = new EnTestDetailsDTO();
            testDetailsDTO.setTestId(test.getId());
            testDetailsDTO.setUnit(unit.getUnit());
            testDetailsDTO.setChapter(chapter.getChapter());
            testDetailsDTO.setPassCount(0L);
            testDetailsDTO.setState(EnTestState.NOTFINISHED);
            testDetailsDTO.setWordTest(testDetails);
            testDetailsDTO.setCreatedTime(LocalDateTime.now());

            return AjaxResult.success(testDetailsDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("新增测试记录失败，请重试！");

        }
    }

    /**
     * 获取完成的测试详情
     * @return
     */
    @Override
    public AjaxResult getTestFinished() {
        return AjaxResult.success(getTestDetailsDTOList(0));
    }

    /**
     * 获取 未完成/未测试 的测试详情
     * @return
     */
    @Override
    public AjaxResult getTestNotFinished() {
        return AjaxResult.success(getTestDetailsDTOList(1));
    }

    /**
     * 删除测试记录
     * @param testID
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AjaxResult deleteTest(Long testID) {
        try {
            //1. 获取用户id
//        Long userId = SecurityUtils.getUserId();
            Long userId = 1L;

            //2. 删除测试明细表
            boolean isSuccess = lambdaUpdate().eq(EnTestDetail::getTestId, testID).eq(EnTestDetail::getUserId, userId).remove();
            if (!isSuccess){
                return AjaxResult.error("删除测试记录失败，请重试！");
            }

            //3. 删除测试表
            boolean isSuccess2 = Db.lambdaUpdate(EnTest.class).eq(EnTest::getId, testID).eq(EnTest::getUserId, userId).remove();
            if (!isSuccess2){
                throw new RuntimeException("删除测试记录失败，请重试！");
            }

            return AjaxResult.success("删除测试记录成功！");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("删除测试记录失败，请重试！");
        }
    }

    /**
     * 获取测试详情列表
     * @param sign 0:已完成的数据，1:未完成/进行中的数据
     * @return
     */
    private List<EnTestDetailsDTO> getTestDetailsDTOList(int sign) {
        //1. 获取用户id
//        Long userId = SecurityUtils.getUserId();
        Long userId = 1L;

        //2. 获取测试表
        List<EnTest> test;
        if (sign == 0) {
            test = Db.lambdaQuery(EnTest.class)
                    .eq(EnTest::getUserId, userId)
                    .eq(EnTest::getState, EnTestState.FINISHED)
                    .orderByDesc(EnTest::getCreatedTime)
                    .list();
        }else {
            test = Db.lambdaQuery(EnTest.class)
                    .eq(EnTest::getUserId, userId)
                    .ne(EnTest::getState, EnTestState.FINISHED)
                    .orderByDesc(EnTest::getCreatedTime)
                    .list();
            test = test.stream().sorted((t1,t2) -> t2.getState().compareTo(t1.getState())).collect(Collectors.toList());
        }
        if (test == null || test.isEmpty()) return Collections.emptyList();
        List<Long> testIds = test.stream().map(EnTest::getId).collect(Collectors.toList());


        //3. 获取章节信息和单元信息
        List<Long> chapterIds = test.stream().map(EnTest::getChapterId).collect(Collectors.toList());
        List<EnChapter> chapters = Db.lambdaQuery(EnChapter.class).in(EnChapter::getId, chapterIds).list();
        List<Long> unitIds = chapters.stream().map(EnChapter::getUnitId).collect(Collectors.toList());
        List<EnUnit> units = Db.lambdaQuery(EnUnit.class).in(EnUnit::getId, unitIds).list();

        Map<Long, EnChapter> chapterMap = chapters.stream().collect(Collectors.toMap(EnChapter::getId, c -> c));
        Map<Long, EnUnit> unitMap = units.stream().collect(Collectors.toMap(EnUnit::getId, u -> u));

        //4.获取测试表详情
        List<EnTestDetail> testDetails = lambdaQuery().in(EnTestDetail::getTestId, testIds).list();
        Map<Long, List<EnTestDetail>> map = testDetails.stream().collect(Collectors.groupingBy(EnTestDetail::getTestId));


        //5. 构建返回结果
        List<EnTestDetailsDTO> testDTOS = new ArrayList<>(test.size());
        for (EnTest t : test) {
            EnTestDetailsDTO testDTO = new EnTestDetailsDTO();
            EnChapter chapter = chapterMap.get(t.getChapterId());
            List<EnTestDetail> details = map.get(t.getId());
            long count = details.stream().filter(c -> c.getResult() == EnWordTestState.READY).count();

            testDTO.setTestId(t.getId());
            testDTO.setChapter(chapter.getChapter());
            testDTO.setUnit(unitMap.get(chapter.getUnitId()).getUnit());
            testDTO.setWordTest(details);
            testDTO.setState(t.getState());
            testDTO.setPassCount(count);
            testDTO.setCreatedTime(t.getCreatedTime());
            testDTOS.add(testDTO);
        }

        return testDTOS;
    }
}
