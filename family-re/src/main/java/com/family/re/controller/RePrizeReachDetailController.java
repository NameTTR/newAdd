package com.family.re.controller;


import com.family.re.service.IRePrizeReachDetailService;
import com.family.re.service.impl.RePrizeReachDetailServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 奖品池兑现明细表 前端控制器
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-04
 */
@RestController
@AllArgsConstructor
@RequestMapping("/family/re/reach")
public class RePrizeReachDetailController {

    private final IRePrizeReachDetailService rePrizeReachDetailService;

    /**
     * 查询奖品池兑现明细表
     * @param prizeReachId 奖品池兑现id
     * @return 奖品池兑现明细表
     */
    @GetMapping("/detail/{prize_reach_id}")
    public AjaxResult getPrizeReachDetail(@PathVariable("prize_reach_id") Long prizeReachId) {
        return rePrizeReachDetailService.getList(prizeReachId);
    }

}
