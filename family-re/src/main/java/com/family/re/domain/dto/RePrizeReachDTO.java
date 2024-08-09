package com.family.re.domain.dto;


import com.family.re.domain.po.RePool;
import com.family.re.domain.po.RePrizeReach;
import lombok.Data;

import java.util.List;

@Data
public class RePrizeReachDTO {
    /**
     * 奖励池的表
     */
    private List<RePool> PlanList;
    /**
     * 未抽奖的奖励池的表
     */
    private List<RePrizeReach> UnPrizeList;
    /**
     * 已经抽奖的奖励池的表
     */
    private List<RePrizeReach> PrizeList;
}
