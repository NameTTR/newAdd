package com.family.re.controller;


import com.family.re.service.IRePoolDetailService;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 奖品池明细表 前端控制器
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-03
 */
@RestController
@AllArgsConstructor
@RequestMapping("/family/re/pool")
public class RePoolDetailController {

    private final IRePoolDetailService rePoolDetailService;

    /**
     * 获取指定奖品池明细列表
     * @param prizePoolId 奖品池id
     * @return 奖品池明细列表
     */
    @GetMapping("/detail/{prize_pool_id}")
    public AjaxResult getPoolDetailList(@PathVariable("prize_pool_id") Long prizePoolId) {
        return rePoolDetailService.getPoolDetailList(prizePoolId);
    }

    /**
     * 删除指定奖品池的奖品
     * @param prizePoolId 奖品池id
     * @param prizeId 奖品id
     * @return 删除奖品池奖品结果
     */
    @DeleteMapping("/prize/{prize_pool_id}/{prize_id}")
    public AjaxResult deletePoolDetailPrize(@PathVariable("prize_pool_id") Long prizePoolId,@PathVariable("prize_id") Long prizeId) {
        return rePoolDetailService.deletePoolDetailPrize(prizePoolId, prizeId);
    }

    /**
     * 往奖品池添加奖品
     * @param prizePoolId 奖品池id
     * @param prizeId 奖品id
     * @return 往奖品池添加奖品结果
     */
    @PostMapping("/prize")
    public AjaxResult addPoolPrize(Long prizePoolId ,Long prizeId) {
        return rePoolDetailService.addPoolPrize(prizePoolId, prizeId);
    }
}
