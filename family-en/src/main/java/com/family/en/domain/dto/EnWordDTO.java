package com.family.en.domain.dto;

import com.family.en.domain.po.EnSentence;
import com.family.en.domain.po.EnWord;
import com.family.en.enums.EnWordState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 单个单词DTO
 *
 * @author 陈文杰
 * @since 2024-06-25
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class EnWordDTO extends EnWord {
    /**
     * 普通句子
     */
    private List<EnSentence> ordinary;

    /**
     * 谚语
     */
    private List<EnSentence> proverb;

    /**
     *  状态  0：未学；1：已学完；2：未掌握
     */
    private EnWordState state;

}
