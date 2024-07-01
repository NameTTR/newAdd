package com.family.th.domain.dto;

import com.family.th.domain.po.ThChapter;
import com.family.th.domain.po.ThUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 单元-章节列表DTO
 *
 * @author 陈文杰
 * @since 2024-06-25
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ThUnitChapterDTO {

    /**
     * 单元信息
     */
    private ThUnit unit;

    /**
     * 章节列表
     */
    private List<ThChapter> chapters;
}
