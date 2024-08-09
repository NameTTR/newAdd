package com.family.cc.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.cc.domain.dto.CcTestDetailsDTO;
import com.family.cc.domain.po.*;
import com.family.cc.enums.CcCharacterState;
import com.family.cc.enums.CcCharacterTestState;
import com.family.cc.enums.CcTestState;
import com.family.cc.mapper.CcTestDetailMapper;
import com.family.cc.service.ICcTestDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.common.domain.result.WhisperResult;
import com.family.common.service.OkHttpService;
import com.github.houbb.pinyin.util.PinyinHelper;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 汉字测试明细表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
@Service
@AllArgsConstructor
public class CcTestDetailServiceImpl extends ServiceImpl<CcTestDetailMapper, CcTestDetail> implements ICcTestDetailService {

    //okHttp服务类
    private final OkHttpService okHttpService;

    /**
     * 新增汉字测试
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
            CcChapter chapter = Db.getById(chapterId, CcChapter.class);

            //3. 获取单元信息
            CcUnit unit = Db.getById(chapter.getUnitId(), CcUnit.class);

            //4. 新增测试信息
            CcTest test = new CcTest();
            test.setUserId(userId);
            test.setChapterId(chapterId);
            test.setState(CcTestState.NOTFINISHED);
            test.setCreatedTime(LocalDateTime.now());
            boolean isSuccess1 = Db.save(test);

            if (!isSuccess1){
                return AjaxResult.error("新增测试记录失败，请重试！");
            }

            //5.1 构建测试明细信息
            List<CcCharacter> characters = Db.lambdaQuery(CcCharacter.class).eq(CcCharacter::getChapterId, chapterId).list();
            List<CcTestDetail> testDetails = new ArrayList<>(characters.size());
            for (CcCharacter character : characters) {
                CcTestDetail testDetail = new CcTestDetail();
                testDetail.setUserId(userId);
                testDetail.setTestId(test.getId());
                testDetail.setCharacterId(character.getId());
                testDetail.setCharacter(character.getCharacter());
                testDetail.setResult(CcCharacterTestState.NOTFINISHED);
                testDetail.setCreatedTime(LocalDateTime.now());
                testDetails.add(testDetail);
            }
            //5.2 新增测试明细信息
            boolean isSuccess2 = saveBatch(testDetails);
            if (!isSuccess2){
                throw new RuntimeException("新增测试记录失败，请重试！");
            }

            //6. 构建返回结果
            CcTestDetailsDTO testDetailsDTO = new CcTestDetailsDTO();
            testDetailsDTO.setTestId(test.getId());
            testDetailsDTO.setUnit(unit.getUnit());
            testDetailsDTO.setChapter(chapter.getChapter());
            testDetailsDTO.setPassCount(0L);
            testDetailsDTO.setState(CcTestState.NOTFINISHED);
            testDetailsDTO.setCharacterTest(testDetails);
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
     * @param testID 测试记录ID
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
            boolean isSuccess = lambdaUpdate().eq(CcTestDetail::getTestId, testID).eq(CcTestDetail::getUserId, userId).remove();
            if (!isSuccess){
                return AjaxResult.error("删除测试记录失败，请重试！");
            }

            //3. 删除测试表
            boolean isSuccess2 = Db.lambdaUpdate(CcTest.class).eq(CcTest::getId, testID).eq(CcTest::getUserId, userId).remove();
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
     * 批改测试结果
     *
     * @param testID        测试记录ID
     * @param files         音频文件
     * @param testDetailIds 测试详情ID
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult checkTest(Long testID, MultipartFile[] files, List<Long> testDetailIds) {
        if(files == null || testDetailIds == null || testDetailIds.size() != files.length){
            return AjaxResult.error("参数错误，请重试！");
        }

        try {
            //1.获取用户id
//        Long userId = SecurityUtils.getUserId();
            Long userId = 1L;

            //2.获取音频转文本信息
            WhisperResult whisperResult = okHttpService.getTextOfWhisper(files);
            if (whisperResult.getCode() == 0){
    //            throw new RuntimeException(whisperResult.getMsg());
                return AjaxResult.error("音频转文本失败，请重试！");
            }

            //3.获取测试详情
            String idStr = StrUtil.join(",", testDetailIds);
            List<CcTestDetail> testDetails = lambdaQuery()
                    .eq(CcTestDetail::getTestId, testID)
                    .eq(CcTestDetail::getUserId, userId)
                    .in(CcTestDetail::getId, testDetailIds)
                    .last("ORDER BY FIELD(id," + idStr + ")")
                    .list();

            //4.判断题目的对错
            List<String> resultText = whisperResult.getText();
            for (int i = 0; i < testDetailIds.size(); i++) {
                String result = resultText.get(i);
                CcTestDetail testDetail = testDetails.get(i);

                if (result == null || result.isEmpty())
                    testDetail.setResult(CcCharacterTestState.ERROR);


                //4.1.将音频转文本的结果与汉字对比
                boolean isCorrect = false;
                char[] charArray = result.toCharArray();
                for (char c : charArray) {
                    if(c == ' ') continue;
                    if(PinyinHelper.hasSamePinyin(c, testDetail.getCharacter().charAt(0))){
                        isCorrect = true;
                        break;
                    }
                }

                //4.2.根据结果设置状态码
                if (isCorrect)
                    testDetail.setResult(CcCharacterTestState.READY);
                else
                    testDetail.setResult(CcCharacterTestState.ERROR);
            }

            //5.更新测试详情中汉字结果判断
            boolean isSuccess = updateBatchById(testDetails);
            if (!isSuccess) return AjaxResult.error("更新测试详情失败，请重试！");

            //6.更新测试状态
            List<CcTestDetail> nowTest = lambdaQuery()
                    .eq(CcTestDetail::getTestId, testID)
                    .eq(CcTestDetail::getUserId, userId)
                    .list();

            //6.1.判断测试是否完成
            long count = nowTest.stream().filter(t -> t.getResult() == CcCharacterTestState.NOTFINISHED).count();
            if (count == 0){
                boolean isUpdate = Db.lambdaUpdate(CcTest.class)
                        .eq(CcTest::getId, testID)
                        .eq(CcTest::getUserId, userId)
                        .set(CcTest::getState, CcTestState.FINISHED)
                        .update();
                if (!isUpdate) return AjaxResult.error("更新测试状态失败，请重试！");
            }

            return AjaxResult.success("批改测试结果成功！");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取测试详情列表
     * @param sign 0:已完成的数据，1:未完成/进行中的数据
     * @return
     */
    private List<CcTestDetailsDTO> getTestDetailsDTOList(int sign) {
        //1. 获取用户id
//        Long userId = SecurityUtils.getUserId();
        Long userId = 1L;

        //2. 获取测试表
        List<CcTest> test;
        if (sign == 0) {
             test = Db.lambdaQuery(CcTest.class)
                    .eq(CcTest::getUserId, userId)
                    .eq(CcTest::getState, CcTestState.FINISHED)
                    .orderByDesc(CcTest::getCreatedTime)
                    .list();
        }else {
            test = Db.lambdaQuery(CcTest.class)
                    .eq(CcTest::getUserId, userId)
                    .ne(CcTest::getState, CcTestState.FINISHED)
                    .orderByDesc(CcTest::getCreatedTime)
                    .list();
            test = test.stream().sorted((t1,t2) -> t2.getState().compareTo(t1.getState())).collect(Collectors.toList());
        }
        if (test == null || test.isEmpty()) return Collections.emptyList();
        List<Long> testIds = test.stream().map(CcTest::getId).collect(Collectors.toList());


        //3. 获取章节信息和单元信息
        List<Long> chapterIds = test.stream().map(CcTest::getChapterId).collect(Collectors.toList());
        List<CcChapter> chapters = Db.lambdaQuery(CcChapter.class).in(CcChapter::getId, chapterIds).list();
        List<Long> unitIds = chapters.stream().map(CcChapter::getUnitId).collect(Collectors.toList());
        List<CcUnit> units = Db.lambdaQuery(CcUnit.class).in(CcUnit::getId, unitIds).list();

        Map<Long, CcChapter> chapterMap = chapters.stream().collect(Collectors.toMap(CcChapter::getId, c -> c));
        Map<Long, CcUnit> unitMap = units.stream().collect(Collectors.toMap(CcUnit::getId, u -> u));

        //4.获取测试表详情
        List<CcTestDetail> testDetails = lambdaQuery().in(CcTestDetail::getTestId, testIds).list();
        Map<Long, List<CcTestDetail>> map = testDetails.stream().collect(Collectors.groupingBy(CcTestDetail::getTestId));


        //5. 构建返回结果
        List<CcTestDetailsDTO> testDTOS = new ArrayList<>(test.size());
        for (CcTest t : test) {
            CcTestDetailsDTO testDTO = new CcTestDetailsDTO();
            CcChapter chapter = chapterMap.get(t.getChapterId());
            List<CcTestDetail> details = map.get(t.getId());
            long count = details.stream().filter(c -> c.getResult() == CcCharacterTestState.READY).count();

            testDTO.setTestId(t.getId());
            testDTO.setChapter(chapter.getChapter());
            testDTO.setUnit(unitMap.get(chapter.getUnitId()).getUnit());
            testDTO.setCharacterTest(details);
            testDTO.setState(t.getState());
            testDTO.setPassCount(count);
            testDTO.setCreatedTime(t.getCreatedTime());
            testDTOS.add(testDTO);
        }

        return testDTOS;
    }
}
