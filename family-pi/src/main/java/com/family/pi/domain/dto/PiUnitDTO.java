package com.family.pi.domain.dto;

import com.family.pi.domain.po.PiUnit;
import com.family.pi.enums.PiUnitState;
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
     * 学习状态：0：未学；1：学习中；2：已学完
     */
    private PiUnitState state;

    /**
     * 声母
     */
    private List<PiPinyinDTO> initials;

    /**
     * 韵母
     */
    private List<PiPinyinDTO> finals;

    /**
     * 组合
     */
    private List<PiPinyinDTO> combinations;

}
