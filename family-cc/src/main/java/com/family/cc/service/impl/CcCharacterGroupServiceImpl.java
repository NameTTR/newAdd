package com.family.cc.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.cc.domain.dto.CcCharacterGroupDTO;
import com.family.cc.domain.po.CcCharacter;
import com.family.cc.domain.po.CcCharacterGroup;
import com.family.cc.mapper.CcCharacterGroupMapper;
import com.family.cc.service.ICcCharacterGroupService;
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
 * 汉字组词表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
@Service
public class CcCharacterGroupServiceImpl extends ServiceImpl<CcCharacterGroupMapper, CcCharacterGroup> implements ICcCharacterGroupService {

    /**
     * 获取章节中汉字的组词信息
     * @param chapterId 章节ID
     * @return
     */
    @Override
    public AjaxResult getCcCharacterGroup(Long chapterId) {
        //1. 根据章节ID查询汉字列表
        List<CcCharacter> characters = Db.lambdaQuery(CcCharacter.class)
                .eq(CcCharacter::getChapterId, chapterId)
                .list();
        List<Long> characterIds = characters.stream().map(CcCharacter::getId).collect(Collectors.toList());

        //2. 获取汉字的词组信息
        List<CcCharacterGroup> characterGroups = Db.lambdaQuery(CcCharacterGroup.class)
                .in(CcCharacterGroup::getCharacterId, characterIds)
                .list();
        Map<Long, List<CcCharacterGroup>> groupMap = characterGroups.stream().collect(Collectors.groupingBy(CcCharacterGroup::getCharacterId));

        //3. 组装返回结果
        List<CcCharacterGroupDTO> groupList = new ArrayList<>();
        for (CcCharacter character : characters) {
            CcCharacterGroupDTO group = new CcCharacterGroupDTO();

            if (groupMap.get(character.getId()) == null) {
                group.setCompounds(Collections.emptyList());
                group.setSynonyms(Collections.emptyList());
                group.setAntonyms(Collections.emptyList());
                continue;
            }

            List<CcCharacterGroup> compounds = groupMap.get(character.getId()).stream().filter(c -> c.getType() == 1).collect(Collectors.toList());
            List<CcCharacterGroup> synonym = groupMap.get(character.getId()).stream().filter(c -> c.getType() == 2).collect(Collectors.toList());
            List<CcCharacterGroup> antonym = groupMap.get(character.getId()).stream().filter(c -> c.getType() == 3).collect(Collectors.toList());

            group.setCompounds(compounds);
            group.setSynonyms(synonym);
            group.setAntonyms(antonym);
            groupList.add(group);
        }

        return AjaxResult.success(groupList);
    }
}
