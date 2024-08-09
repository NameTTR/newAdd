package com.family.en.domain.dto;

import com.family.en.domain.po.EnChapter;
import com.family.en.enums.EnChapterState;
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
 * @since 2024-06-25
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class EnChapterDTO {
    /**
     * 章节信息
     */
    private EnChapter chapter;

    /**
     * 章节学习状态：0：未学；1：学习中；2：已学完
     */
    private EnChapterState state;

    /**
     * 章节中的单词列表
     */
    private List<EnWordDTO> words;
}
