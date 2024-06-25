package com.family.en.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.en.domain.dto.EnWordDTO;
import com.family.en.domain.po.EnStudy;
import com.family.en.domain.po.EnWord;
import com.family.en.mapper.EnStudyMapper;
import com.family.en.service.IEnStudyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.stereotype.Service;

import static com.family.en.enums.EnWordState.LEARNED_FINISH;
import static com.family.en.enums.EnWordState.NOT_MASTERED;

/**
 * <p>
 * 单词学习记录表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-24
 */
@Service
public class EnStudyServiceImpl extends ServiceImpl<EnStudyMapper, EnStudy> implements IEnStudyService {

    /**
     * 更新单词学习记录
     * @param wordId 汉字id
     * @return
     */
    @Override
    public AjaxResult updateStudyRecord(Long wordId) {
        //1. 获取当前用户id
//        Long userId = SecurityUtils.getUserId();
        Long userId = 1L;

        //2. 获取当前单词学习记录
        EnStudy study = lambdaQuery()
                .eq(EnStudy::getUserId, userId)
                .eq(EnStudy::getWordId, wordId)
                .one();
        if (study == null) {
            //2.1. 如果不存在该单词的id，返回失败信息
            return AjaxResult.error();
        }

        //3. 更新单词学习记录表
        switch (study.getState()){
            case UNLEARNED:
            case NOT_MASTERED:
                study.setState(LEARNED_FINISH);
                break;
            case LEARNED_FINISH:
                study.setState(NOT_MASTERED);
                break;
        }
        boolean isSuccess = lambdaUpdate().eq(EnStudy::getUserId, userId)
                .eq(EnStudy::getWordId, wordId)
                .update(study);

        //4. 返回结果
        //4.1. 如果更新失败，返回失败信息
        if (!isSuccess){
            return AjaxResult.error("更新失败");
        }

        //4.2. 如果更新成功，返回更新后的单词信息
        EnWord one = Db.lambdaQuery(EnWord.class).eq(EnWord::getId, wordId).one();
        EnWordDTO updatedWord = BeanUtil.copyProperties(one, EnWordDTO.class);
        updatedWord.setState(study.getState());
        return AjaxResult.success(updatedWord);
    }
}
