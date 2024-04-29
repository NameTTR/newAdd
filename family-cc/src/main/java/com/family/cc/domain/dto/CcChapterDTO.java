package com.family.cc.domain.dto;

import com.family.cc.domain.po.CcCharacter;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
public class CcChapterDTO extends BaseEntity {
    /**
     * 章节名称
     */
    private String chapterName;
    /**
     * 章节中的汉字列表
     */
    private List<CcCharacter> characters;
}
