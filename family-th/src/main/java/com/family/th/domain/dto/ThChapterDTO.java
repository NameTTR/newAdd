package com.family.th.domain.dto;

import com.family.th.domain.po.ThChapter;
import com.family.th.enums.ThChapterState;
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
 * @since 2024-07-01
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ThChapterDTO {
    /**
     * 章节信息
     */
    private ThChapter chapter;

    /**
     * 章节学习状态：0：未学；1：学习中；2：已学完
     */
    private ThChapterState state;

    /**
     * 章节中的单词列表
     */
    private List<ThThinkingDTO> words;
}
