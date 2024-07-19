package com.family.re.controller;


import com.family.re.constant.RewardConstants;
import com.family.re.domain.po.RePool;
import com.family.re.service.IRePoolService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.job.TaskException;
import lombok.AllArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * <p>
 * 奖品池表 前端控制器
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-03
 */
@RestController
@AllArgsConstructor
@RequestMapping("/family/re/pool")
public class RePoolController {

    private final IRePoolService rePoolService;

    /**
     * 删除奖品池列表
     * @param prizePoolId 奖品池id
     * @return 删除信息
     */
    @DeleteMapping("/{prize_pool_id}")
        public AjaxResult deletePrize(@PathVariable("prize_pool_id") Long prizePoolId) {
        Integer type = rePoolService.deletePrize(prizePoolId);
        if (type.equals(RewardConstants.REWARD_POOL_PRIZE_ERROR)) {
            return AjaxResult.error("删除奖品池的奖品失败");
        } else if (type.equals(RewardConstants.REWARD_POOL_ERROR)) {
            return AjaxResult.error("删除奖品池失败");
        } else {
            return AjaxResult.success("删除奖品池的奖品成功");
        }
    }
    

    /**
     * 添加奖品池
     * @param pool 奖品池类，传递用户需要填写的数据
     * @return 添加信息
     */
    @PostMapping()
    public AjaxResult addPool(@RequestBody RePool pool){
        return rePoolService.addPrizePool(pool);
    }

    /**
     * 修改奖品池
     * @param pool 奖品池类，传递用户需要填写的数据
     * @return 修改信息
     */
    @PutMapping()
    public AjaxResult revPool(@RequestBody RePool pool){
        return rePoolService.revisionPool(pool);
    }

//    /**
//     * 定时执行的模块(未开启)
//     */
//    @PostMapping
//    public void reJob() throws SchedulerException, TaskException {
//        rePoolService.reJob();
//    }

}
