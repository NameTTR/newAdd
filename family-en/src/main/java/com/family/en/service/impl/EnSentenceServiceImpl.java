package com.family.en.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.en.domain.dto.EnSentenceDTO;
import com.family.en.domain.po.EnSentence;
import com.family.en.domain.po.EnWord;
import com.family.en.mapper.EnSentenceMapper;
import com.family.en.service.IEnSentenceService;
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
 * 单词句子表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-06-24
 */
@Service
public class EnSentenceServiceImpl extends ServiceImpl<EnSentenceMapper, EnSentence> implements IEnSentenceService {

    /**
     * 获取章节中单词的句子信息
     * @param chapterId 章节ID
     * @return
     */
    @Override
    public AjaxResult getEnSentence(Long chapterId) {
        //1. 根据章节ID查询汉字列表
        List<EnWord> words = Db.lambdaQuery(EnWord.class)
                .eq(EnWord::getChapterId, chapterId)
                .list();
        List<Long> wordsIds = words.stream().map(EnWord::getId).collect(Collectors.toList());

        //2. 获取汉字的词组信息
        List<EnSentence> characterGroups = Db.lambdaQuery(EnSentence.class)
                .in(EnSentence::getWordId, wordsIds)
                .list();
        Map<Long, List<EnSentence>> groupMap = characterGroups.stream().collect(Collectors.groupingBy(EnSentence::getWordId));

        //3. 组装返回结果
        List<EnSentenceDTO> groupList = new ArrayList<>();
        for (EnWord word : words) {
            EnSentenceDTO group = new EnSentenceDTO();

            if (groupMap.get(word.getId()) == null) {
                group.setOrdinary(Collections.emptyList());
                group.setProverb(Collections.emptyList());
                continue;
            }

            List<EnSentence> setOrdinary = groupMap.get(word.getId()).stream().filter(c -> c.getType() == 1).collect(Collectors.toList());
            List<EnSentence> setProverb = groupMap.get(word.getId()).stream().filter(c -> c.getType() == 2).collect(Collectors.toList());

            group.setOrdinary(setOrdinary);
            group.setProverb(setProverb);
            groupList.add(group);
        }

        return AjaxResult.success(groupList);
    }
}
