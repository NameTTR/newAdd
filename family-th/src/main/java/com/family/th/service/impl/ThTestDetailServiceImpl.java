package com.family.th.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.th.domain.dto.ThTestDetailsDTO;
import com.family.th.domain.po.*;
import com.family.th.enums.ThTestState;
import com.family.th.enums.ThThinkingTestState;
import com.family.th.mapper.ThTestDetailMapper;
import com.family.th.service.IThTestDetailService;
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
 * 思维测试明细表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-01
 */
@Service
public class ThTestDetailServiceImpl extends ServiceImpl<ThTestDetailMapper, ThTestDetail> implements IThTestDetailService {

    /**
     * 新增测试
     * @param chapterId 章节ID
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult addTest(Long chapterId) {
        try {
            //1. 获取用户id
//        Long userId = SecurityUtils.getUserId();
            Long userId = 1L;

            //2. 获取章节信息
            ThChapter chapter = Db.getById(chapterId, ThChapter.class);

            //3. 获取单元信息
            ThUnit unit = Db.getById(chapter.getUnitId(), ThUnit.class);

            //4. 新增测试信息
            ThTest test = new ThTest();
            test.setUserId(userId);
            test.setChapterId(chapterId);
            test.setState(ThTestState.NOTFINISHED);
            test.setCreatedTime(LocalDateTime.now());
            boolean isSuccess1 = Db.save(test);

            if (!isSuccess1){
                return AjaxResult.error("新增测试记录失败，请重试！");
            }

            //5.1 构建测试明细信息
            List<ThTestQuestion> questions = Db.lambdaQuery(ThTestQuestion.class).eq(ThTestQuestion::getChapterId, chapterId).list();
            List<ThTestDetail> testDetails = new ArrayList<>(questions.size());
            for (ThTestQuestion question : questions) {
                ThTestDetail testDetail = new ThTestDetail();
                testDetail.setUserId(userId);
                testDetail.setTestQuestionId(question.getId());
                testDetail.setCorrectAnswer(question.getAnswer());
                testDetail.setTestId(test.getId());
                testDetail.setResult(ThThinkingTestState.NOTFINISHED);
                testDetail.setCreatedTime(LocalDateTime.now());
                testDetails.add(testDetail);
            }
            //5.2 新增测试明细信息
            boolean isSuccess2 = saveBatch(testDetails);
            if (!isSuccess2){
                throw new RuntimeException("新增测试记录失败，请重试！");
            }

            //6. 构建返回结果
            ThTestDetailsDTO testDetailsDTO = new ThTestDetailsDTO();
            testDetailsDTO.setTestId(test.getId());
            testDetailsDTO.setUnit(unit.getUnit());
            testDetailsDTO.setChapter(chapter.getChapter());
            testDetailsDTO.setPassCount(0L);
            testDetailsDTO.setState(ThTestState.NOTFINISHED);
            testDetailsDTO.setThinkingTest(testDetails);
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
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteTest(Long testID) {
        try {
            //1. 获取用户id
//        Long userId = SecurityUtils.getUserId();
            Long userId = 1L;

            //2. 删除测试明细表
            boolean isSuccess = lambdaUpdate().eq(ThTestDetail::getTestId, testID).eq(ThTestDetail::getUserId, userId).remove();
            if (!isSuccess){
                return AjaxResult.error("删除测试记录失败，请重试！");
            }

            //3. 删除测试表
            boolean isSuccess2 = Db.lambdaUpdate(ThTest.class).eq(ThTest::getId, testID).eq(ThTest::getUserId, userId).remove();
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
    private List<ThTestDetailsDTO> getTestDetailsDTOList(int sign) {
        //1. 获取用户id
//        Long userId = SecurityUtils.getUserId();
        Long userId = 1L;

        //2. 获取测试表
        List<ThTest> test;
        if (sign == 0) {
            test = Db.lambdaQuery(ThTest.class)
                    .eq(ThTest::getUserId, userId)
                    .eq(ThTest::getState, ThTestState.FINISHED)
                    .orderByDesc(ThTest::getCreatedTime)
                    .list();
        }else {
            test = Db.lambdaQuery(ThTest.class)
                    .eq(ThTest::getUserId, userId)
                    .ne(ThTest::getState, ThTestState.FINISHED)
                    .orderByDesc(ThTest::getCreatedTime)
                    .list();
            test = test.stream().sorted((t1,t2) -> t2.getState().compareTo(t1.getState())).collect(Collectors.toList());
        }
        if (test == null || test.isEmpty()) return Collections.emptyList();
        List<Long> testIds = test.stream().map(ThTest::getId).collect(Collectors.toList());


        //3. 获取章节信息和单元信息
        List<Long> chapterIds = test.stream().map(ThTest::getChapterId).collect(Collectors.toList());
        List<ThChapter> chapters = Db.lambdaQuery(ThChapter.class).in(ThChapter::getId, chapterIds).list();
        List<Long> unitIds = chapters.stream().map(ThChapter::getUnitId).collect(Collectors.toList());
        List<ThUnit> units = Db.lambdaQuery(ThUnit.class).in(ThUnit::getId, unitIds).list();

        Map<Long, ThChapter> chapterMap = chapters.stream().collect(Collectors.toMap(ThChapter::getId, c -> c));
        Map<Long, ThUnit> unitMap = units.stream().collect(Collectors.toMap(ThUnit::getId, u -> u));

        //4.获取测试表详情
        List<ThTestDetail> testDetails = lambdaQuery().in(ThTestDetail::getTestId, testIds).list();
        Map<Long, List<ThTestDetail>> map = testDetails.stream().collect(Collectors.groupingBy(ThTestDetail::getTestId));


        //5. 构建返回结果
        List<ThTestDetailsDTO> testDTOS = new ArrayList<>(test.size());
        for (ThTest t : test) {
            ThTestDetailsDTO testDTO = new ThTestDetailsDTO();
            ThChapter chapter = chapterMap.get(t.getChapterId());
            List<ThTestDetail> details = map.get(t.getId());
            long count = details.stream().filter(c -> c.getResult() == ThThinkingTestState.READY).count();
            List<Long> questionIds = details.stream().map(ThTestDetail::getTestQuestionId).collect(Collectors.toList());

            testDTO.setTestId(t.getId());
            testDTO.setChapter(chapter.getChapter());
            testDTO.setUnit(unitMap.get(chapter.getUnitId()).getUnit());
            testDTO.setQuestionIds(questionIds);
            testDTO.setThinkingTest(details);
            testDTO.setState(t.getState());
            testDTO.setPassCount(count);
            testDTO.setCreatedTime(t.getCreatedTime());
            testDTOS.add(testDTO);
        }

        return testDTOS;
    }
}
