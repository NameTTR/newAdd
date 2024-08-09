package com.family.cc.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.cc.domain.po.CcCharacter;
import com.family.cc.domain.po.CcStudy;
import com.family.cc.enums.CcCharacterState;
import com.family.cc.mapper.CcStudyMapper;
import com.family.cc.service.ICcStudyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.family.cc.enums.CcCharacterState.LEARNED_FINISH;
import static com.family.cc.enums.CcCharacterState.NOT_MASTERED;

/**
 * <p>
 * 汉字学习记录表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
@Service
public class CcStudyServiceImpl extends ServiceImpl<CcStudyMapper, CcStudy> implements ICcStudyService {

    /**
     * 更新汉字学习记录
     * @param characterId 汉字id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateStudyRecord(Long characterId) {
        try {
            //1. 获取当前用户id
//        Long userId = SecurityUtils.getUserId();
            Long userId = 1L;

            //2. 获取当前汉字学习记录
            CcStudy study = lambdaQuery()
                    .eq(CcStudy::getUserId, userId)
                    .eq(CcStudy::getCharacterId, characterId)
                    .one();
            if (study == null) {
                //2.1. 如果不存在该汉字的id，返回失败信息
                return AjaxResult.error();
            }

            //3. 更新汉字学习记录表
            switch (study.getState()){
                case UNLEARNED:
                case NOT_MASTERED:
                    study.setState(LEARNED_FINISH);
                    break;
                case LEARNED_FINISH:
                    study.setState(NOT_MASTERED);
                    break;
            }
            boolean isSuccess = lambdaUpdate().eq(CcStudy::getUserId, userId)
                    .eq(CcStudy::getCharacterId, characterId)
                    .set(CcStudy::getState, study.getState())
                    .update();

            //4. 返回结果
            //4.1. 如果更新失败，返回失败信息
            if (!isSuccess){
                return AjaxResult.error("更新失败");
            }

            //4.2. 如果更新成功，返回更新后的汉字信息
            return AjaxResult.success(study);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("更新学习记录失败");

        }
    }

    /**
     * 新增用户汉字学习记录
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult addStudyRecord() {
        try {
            //1.获取用户id
//        Long userId = SecurityUtils.getUserId();
            Long userId = 1L;

            //2.添加所有汉字学习记录
            List<CcCharacter> characters = Db.lambdaQuery(CcCharacter.class).list();
            List<Long> Ids = characters.stream().map(CcCharacter::getId).collect(Collectors.toList());

            List<CcStudy> characterStudies = new ArrayList<>(characters.size());
            characterStudies.addAll(Ids.stream().map(id -> {
                CcStudy characterStudy = new CcStudy();
                characterStudy.setCharacterId(id);
                characterStudy.setState(CcCharacterState.UNLEARNED);
                characterStudy.setUserId(userId);
                return characterStudy;
            }).collect(Collectors.toList()));

            boolean isSaved = saveOrUpdateBatch(characterStudies);
            if(!isSaved){
                return AjaxResult.error("新增用户汉字学习记录失败！");
            }

            return AjaxResult.success("新增用户汉字学习记录成功！");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("新增用户汉字学习记录失败！");
        }
    }
}
