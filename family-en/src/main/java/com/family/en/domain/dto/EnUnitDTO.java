package com.family.en.domain.dto;

import com.family.en.domain.po.EnUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 单个单元DTO
 *
 * @author 陈文杰
 * @since 2024-06-25
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class EnUnitDTO {
    /**
     * 单元信息
     */
    private EnUnit unit;

    /**
     * 单元的内容
     */
    private List<EnChapterDTO> unitDate;

}
