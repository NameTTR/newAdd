package com.family.pi.domain.dto;

import com.family.pi.domain.po.PiUnit;
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
 * @since 2024-05-04
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class PiUnitDTO {
    /**
     * 单元信息
     */
    private PiUnit unit;

    /**
     * 单元的内容
     */
    private List<PiPinyinDTO> pinyins;

}
