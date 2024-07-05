package com.family.cc.domain.dto;

import com.family.cc.domain.po.CcCharacterGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 单个汉字组词DTO
 *
 * @author 陈文杰
 * @since 2024-07-04
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class CcCharacterGroupDTO {
    /**
     *  组词
     */
    private List<CcCharacterGroup> compounds;

    /**
     *  近义词
     */
    private List<CcCharacterGroup> synonyms;

    /**
     *  反义词
     */
    private List<CcCharacterGroup> antonyms;
}
