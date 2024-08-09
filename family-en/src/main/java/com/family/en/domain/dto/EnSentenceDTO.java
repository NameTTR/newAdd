package com.family.en.domain.dto;

import com.family.en.domain.po.EnSentence;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 单个单词句子DTO
 *
 * @author 陈文杰
 * @since 2024-07-04
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class EnSentenceDTO {
    /**
     * 普通句子
     */
    private List<EnSentence> ordinary;

    /**
     * 谚语
     */
    private List<EnSentence> proverb;
}
