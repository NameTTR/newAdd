package com.family.cc.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.family.cc.domain.dto.ExcelEntity;
import com.family.cc.domain.po.CcCharacter;
import com.family.cc.domain.po.CcCharacterGroup;
import com.family.cc.mapper.CcCharacterMapper;
import com.family.cc.service.ICcCharacterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.cc.util.ExcelReader;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 汉字章节表 服务实现类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-04-28
 */
@Service
@AllArgsConstructor
public class CcCharacterServiceImpl extends ServiceImpl<CcCharacterMapper, CcCharacter> implements ICcCharacterService {

    private final ExcelReader excelReader;
    /**
     * 导入数据
     * @param tempFile 导入的文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importData(File tempFile) {
        //1. 读取Excel文件
        List<ExcelEntity> excel = excelReader.readExcel(tempFile);

        //2. 重组数据
        List<CcCharacter> characters = new ArrayList<>(excel.size());
        List<List<CcCharacterGroup>> characterOfgroup  = new ArrayList<>(excel.size()*14);
        excel.forEach(excelEntity -> {
            //2.1 保存汉字信息
            CcCharacter ccCharacter = new CcCharacter();
            ccCharacter.setCharacter(excelEntity.getCharacter());
            ccCharacter.setPinyin(excelEntity.getPinyin());
            ccCharacter.setPinyinRead("读音");
            ccCharacter.setTranslation(excelEntity.getTranslation());
            ccCharacter.setTranslationRead("读音");
            ccCharacter.setChapterId(excelEntity.getChapterId());
            characters.add(ccCharacter);

            //2.2 汉字组词信息
            List<CcCharacterGroup> group = new ArrayList<>(14);
            excelEntity.getCompounds().forEach(compound -> {
                CcCharacterGroup characterGroup = new CcCharacterGroup();
                characterGroup.setType(1);
                characterGroup.setWords(compound);
                characterGroup.setRead("读音");
                group.add(characterGroup);
            });
            excelEntity.getSynonyms().forEach(synonym -> {
                CcCharacterGroup characterGroup = new CcCharacterGroup();
                characterGroup.setType(2);
                characterGroup.setWords(synonym);
                characterGroup.setRead("读音");
                group.add(characterGroup);
            });
            excelEntity.getAntonyms().forEach(antonym -> {
                CcCharacterGroup characterGroup = new CcCharacterGroup();
                characterGroup.setType(3);
                characterGroup.setWords(antonym);
                characterGroup.setRead("读音");
                group.add(characterGroup);
            });
            characterOfgroup.add(group);
        });

        //3. 保存汉字信息
        boolean isSaved = saveOrUpdateBatch(characters);
        if (!isSaved){
            throw new RuntimeException("保存失败");
        }

        //4. 保存汉字组词信息
        for (int i = 0; i < characters.size(); i++) {
            Long id = characters.get(i).getId();
            characterOfgroup.get(i).forEach(group -> group.setCharacterId(id));
        }
        List<CcCharacterGroup> list = characterOfgroup.stream().flatMap(List::stream).collect(Collectors.toList());
        boolean isGroupSaved = Db.saveOrUpdateBatch(list);
        if (!isGroupSaved){
            throw new RuntimeException("保存失败");
        }
    }
}
