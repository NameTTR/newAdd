package com.family.re.domain.dto;


import lombok.Data;

import java.util.List;

@Data
public class RePrizeDTO {

    /**
     * 公开奖品
     */
    private List<ReSelectedPrizesDTO> publicPrizes;

    /**
     * 私有奖品
     */
    private List<ReSelectedPrizesDTO> privatePrizes;

}
