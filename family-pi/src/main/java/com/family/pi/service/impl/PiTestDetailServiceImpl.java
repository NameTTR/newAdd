package com.family.pi.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.pi.domain.dto.PiTestDetailsDTO;
import com.family.pi.domain.po.PiPinyin;
import com.family.pi.domain.po.PiTest;
import com.family.pi.domain.po.PiTestDetail;
import com.family.pi.domain.po.PiUnit;
import com.family.pi.enums.PiPinyinTestState;
import com.family.pi.enums.PiTestState;
import com.family.pi.mapper.PiTestDetailMapper;
import com.family.pi.service.IPiTestDetailService;
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
 * 拼音测试明细表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-26
 */
@Service
public class PiTestDetailServiceImpl extends ServiceImpl<PiTestDetailMapper, PiTestDetail> implements IPiTestDetailService {

    /**
     * 新增测试
     * @param unitId 单元ID
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AjaxResult addTest(Long unitId) {
        try {
            //1. 获取用户id
//        Long userId = SecurityUtils.getUserId();
            Long userId = 1L;

            //2. 获取章节信息
//            CcChapter chapter = Db.getById(chapterId, CcChapter.class);

            //2. 获取单元信息
            PiUnit unit = Db.getById(unitId, PiUnit.class);

            //3. 新增测试信息
            PiTest test = new PiTest();
            test.setUnitId(unitId);
            test.setUserId(userId);
            test.setState(PiTestState.NOTFINISHED);
            test.setCreatedTime(LocalDateTime.now());
            boolean isSuccess1 = Db.save(test);

            if (!isSuccess1){
                return AjaxResult.error("新增测试记录失败，请重试！");
            }

            //4.1 构建测试明细信息
            List<PiPinyin> pinyins = Db.lambdaQuery(PiPinyin.class).eq(PiPinyin::getUnitId, unitId).list();
            List<PiTestDetail> testDetails = new ArrayList<>(pinyins.size());
            for (PiPinyin pinyin : pinyins) {
                PiTestDetail testDetail = new PiTestDetail();
                testDetail.setUserId(userId);
                testDetail.setTestId(test.getId());
                testDetail.setPinyinId(pinyin.getId());
                testDetail.setPinyin(pinyin.getPinyin());
                testDetail.setResult(PiPinyinTestState.NOTFINISHED);
                testDetail.setCreatedTime(LocalDateTime.now());
                testDetails.add(testDetail);
            }
            //4.2 新增测试明细信息
            boolean isSuccess2 = saveBatch(testDetails);
            if (!isSuccess2){
                throw new RuntimeException("新增测试记录失败，请重试！");
            }

            //5. 构建返回结果
            PiTestDetailsDTO testDetailsDTO = new PiTestDetailsDTO();
            testDetailsDTO.setTestId(test.getId());
            testDetailsDTO.setUnit(unit.getUnit());
            testDetailsDTO.setTotalCount((long) pinyins.size());
            testDetailsDTO.setPassCount(0L);
            testDetailsDTO.setState(PiTestState.NOTFINISHED);
            testDetailsDTO.setPinyinTest(testDetails);
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
            boolean isSuccess = lambdaUpdate().eq(PiTestDetail::getTestId, testID).eq(PiTestDetail::getUserId, userId).remove();
            if (!isSuccess){
                return AjaxResult.error("删除测试记录失败，请重试！");
            }

            //3. 删除测试表
            boolean isSuccess2 = Db.lambdaUpdate(PiTest.class).eq(PiTest::getId, testID).eq(PiTest::getUserId, userId).remove();
            if (!isSuccess2){
                throw new RuntimeException("删除测试记录失败，请重试！");
            }

            return AjaxResult.success("删除测试记录成功！");
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("删除测试记录失败，请重试！");
        }
    }

    /**
     * 获取测试详情列表
     * @param sign 0:已完成的数据，1:未完成/进行中的数据
     * @return
     */
    private List<PiTestDetailsDTO> getTestDetailsDTOList(int sign) {
        //1. 获取用户id
//        Long userId = SecurityUtils.getUserId();
        Long userId = 1L;

        //2. 获取测试表
        List<PiTest> test;
        if (sign == 0) {
            test = Db.lambdaQuery(PiTest.class)
                    .eq(PiTest::getUserId, userId)
                    .eq(PiTest::getState, PiTestState.FINISHED)
                    .orderByDesc(PiTest::getCreatedTime)
                    .list();
        }else {
            test = Db.lambdaQuery(PiTest.class)
                    .eq(PiTest::getUserId, userId)
                    .ne(PiTest::getState, PiTestState.FINISHED)
                    .orderByDesc(PiTest::getCreatedTime)
                    .list();
            test = test.stream().sorted((t1,t2) -> t2.getState().compareTo(t1.getState())).collect(Collectors.toList());
        }
        if (test == null || test.isEmpty()) return Collections.emptyList();
        List<Long> testIds = test.stream().map(PiTest::getId).collect(Collectors.toList());


        //3. 获取单元信息
        List<Long> unitIds = test.stream().map(PiTest::getUnitId).collect(Collectors.toList());
        List<PiUnit> units = Db.lambdaQuery(PiUnit.class).in(PiUnit::getId, unitIds).list();

        Map<Long, PiUnit> unitMap = units.stream().collect(Collectors.toMap(PiUnit::getId, u -> u));

        //4.获取测试表详情
        List<PiTestDetail> testDetails = lambdaQuery().in(PiTestDetail::getTestId, testIds).list();
        Map<Long, List<PiTestDetail>> map = testDetails.stream().collect(Collectors.groupingBy(PiTestDetail::getTestId));

        //5. 构建返回结果
        List<PiTestDetailsDTO> testDTOS = new ArrayList<>(test.size());
        for (PiTest t : test) {
            PiTestDetailsDTO testDTO = new PiTestDetailsDTO();
            List<PiTestDetail> details = map.get(t.getId());
            long totalCount = details.size();
            long passCount = details.stream().filter(c -> c.getResult() == PiPinyinTestState.READY).count();

            testDTO.setTestId(t.getId());
            testDTO.setUnit(unitMap.get(t.getUnitId()).getUnit());
            testDTO.setPinyinTest(details);
            testDTO.setState(t.getState());
            testDTO.setTotalCount(totalCount);
            testDTO.setPassCount(passCount);
            testDTO.setCreatedTime(t.getCreatedTime());
            testDTOS.add(testDTO);
        }

        return testDTOS;
    }
}
