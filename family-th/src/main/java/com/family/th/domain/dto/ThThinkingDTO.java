package com.family.th.domain.dto;

import com.family.th.domain.po.ThThinking;
import com.family.th.enums.ThThinkingState;
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
public class ThThinkingDTO extends ThThinking {
    /**
     *  状态  0：未学；1：已学完；2：未掌握
     */
    private ThThinkingState state;

}
