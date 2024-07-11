package com.family.re.controller;


import com.family.re.service.IRePrizeReachService;
import com.family.re.service.impl.RePrizeReachServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 奖品兑现表 前端控制器
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-03
 */
@RestController
@AllArgsConstructor
@RequestMapping("/family/re")
public class RePrizeReachController {

    private final IRePrizeReachService rePrizeReachService;

    /**
     * 获取奖品池兑现池和奖品池列表
     * @return 奖品池兑现池和奖品池列表
     */
    @GetMapping("/prizeReachList")
    public AjaxResult getList() {
        return rePrizeReachService.getList();
    }

    /**
     * 抽奖后的数据更新
     * @param prizeId 抽中的奖品id
     * @param reachPoolId 兑现池id
     * @return 更新结果
     */
    @PutMapping("/lotteryUpdate")
    public AjaxResult lotteryUpdate(Long prizeId, Long reachPoolId) {
        return rePrizeReachService.lotteryUpdate(prizeId, reachPoolId);
    }

}
