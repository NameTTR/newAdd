package com.family.pi.domain.dto;

import com.family.pi.domain.po.PiUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 单元列表DTO
 *
 * @author 陈文杰
 * @since 2024-05-04
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class PiUnitListDTO {

    /**
     * 当前用户学习的单元名称
     */
    private String unitName;

    /**
     * 该单元的Id
     */
    private Long unitId;

    /**
     * 单元列表
     */
    private List<PiUnit> unitList;
}
