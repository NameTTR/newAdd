package com.family.cc.domain.dto;

import com.family.cc.domain.po.CcChapter;
import com.family.cc.domain.po.CcCharacter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 单个章节DTO
 *
 * @author 陈文杰
 * @since 2024-05-04
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class CcChapterDTO{
    /**
     * 章节信息
     */
    private CcChapter chapter;

    /**
     * 章节中的汉字列表
     */
    private List<CcCharacterDTO> characters;
}
