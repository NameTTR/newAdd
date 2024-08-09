package com.family.re.domain.dto;

import com.family.re.domain.po.RePrize;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class ReSelectedPrizesDTO {

    /**
     * 奖品
     */
    private RePrize prize;

    /**
     * 是否选中
     */
    private boolean selected;

}
