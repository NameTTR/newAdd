package com.family.cc.domain.dto;

import com.family.cc.domain.po.CcCharacter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 单个汉字DTO
 *
 * @author 陈文杰
 * @since 2024-05-04
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class CcCharacterDTO extends CcCharacter {
    /**
     *  状态  0：未学；1：已学完；2：未掌握
     */
    private Integer status;

}
