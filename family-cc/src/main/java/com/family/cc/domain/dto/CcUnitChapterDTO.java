package com.family.cc.domain.dto;

import com.family.cc.domain.po.CcChapter;
import com.family.cc.domain.po.CcUnit;
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
 * @since 2024-05-04
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class CcUnitChapterDTO {

    /**
     * 单元信息
     */
    private CcUnit unit;

    /**
     * 章节列表
     */
    private List<CcChapter> chapters;
}
