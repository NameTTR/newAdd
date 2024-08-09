package com.family.th.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.th.domain.dto.ThTestDetailsDTO;
import com.family.th.domain.po.*;
import com.family.th.enums.ThTestState;
import com.family.th.enums.ThThinkingTestState;
import com.family.th.mapper.ThTestDetailMapper;
import com.family.th.service.IThTestDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.AllArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Statement;
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
@AllArgsConstructor
public class ThTestDetailServiceImpl extends ServiceImpl<ThTestDetailMapper, ThTestDetail> implements IThTestDetailService {

    // 使用 MyBatis 的 SqlSessionTemplate
    private final SqlSessionTemplate sqlSessionTemplate;

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
     * 更新测试记录
     * @param testDetailsDTO 测试记录
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateTest(ThTestDetailsDTO testDetailsDTO) {
        try {
            //1. 获取用户id
//        Long userId = SecurityUtils.getUserId();
            Long userId = 1L;

            //2. 获取用户的测试答案
            List<ThTestDetail> updateTestDetails = testDetailsDTO.getThinkingTest();

            //3.获取当前数据库中的测试记录
            ThTest test = Db.lambdaQuery(ThTest.class).eq(ThTest::getId, testDetailsDTO.getTestId()).eq(ThTest::getUserId, userId).one();

            //3.1 如果当前数据库中不存在测试记录，则返回错误信息
            if (test == null) {
                return AjaxResult.error("测试记录细节不存在，请重试！");
            }

            //3.2 存在，则进行遍历测试记录，判断是否有更新
            ThTestState state = ThTestState.NOTFINISHED;
            List<ThTestDetail> needUpdateDetails = new ArrayList<>();
            for (ThTestDetail updateTestDetail : updateTestDetails) {
                if (updateTestDetail.getResult() == ThThinkingTestState.FINISHING) {
                    needUpdateDetails.add(updateTestDetail);
                }
            }
            if (updateTestDetails.get(updateTestDetails.size() - 1).getResult() != ThThinkingTestState.NOTFINISHED)
                state = ThTestState.FINISHED;

            //3.3 如果没有更新，则直接返回成功信息
            if (needUpdateDetails.isEmpty()){
                return AjaxResult.success("测试记录没有更新！");
            }

            //4. 有更新，则进行更新
            //4.1 遍历创建更新的sql语句
            StringBuilder sqlBuilder = new StringBuilder();
            needUpdateDetails.forEach(testDetail -> {
                sqlBuilder.append("UPDATE th_test_detail SET result = ").append(testDetail.getResult()).append(" WHERE id = ").append(testDetail.getId()).append(";");
            });
            if(state == ThTestState.FINISHED) sqlBuilder.append("UPDATE th_test SET state = ").append(state).append(" WHERE id = ").append(testDetailsDTO.getTestId()).append(";");

            //4.2 执行更新语句
            int update = sqlSessionTemplate.update(sqlBuilder.toString());
            if (update > 1){
                //说明有更新失败，抛出异常
                return AjaxResult.error("更新测试记录失败，请重试！");
            }

            //4.3 返回成功信息
            return AjaxResult.success("更新测试记录成功！");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("更新测试记录失败，请重试！");
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
