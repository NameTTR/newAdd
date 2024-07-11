package com.family.re.controller;


import com.family.re.domain.po.RePool;
import com.family.re.service.IRePoolService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.job.TaskException;
import lombok.AllArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;


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
@RequestMapping("/family/re")
public class RePoolController {

    private final IRePoolService rePoolService;

    /**
     * 删除奖品池列表
     * @param prizePoolId 奖品池id
     * @return 删除信息
     */
    @DeleteMapping("/prizeReachList/{prizePoolId}")
        public AjaxResult deletePrize(@PathVariable("prizePoolId") Long prizePoolId){
            return rePoolService.deletePrize(prizePoolId);
        }

    /**
     * 添加奖品池
     * @param pool 奖品池类，传递用户需要填写的数据
     * @return 添加信息
     */
    @PostMapping("/addPrizePool")
    public AjaxResult add(@RequestBody RePool pool){
        return rePoolService.add(pool);
    }

    /**
     * 修改奖品池
     * @param pool 奖品池类，传递用户需要填写的数据
     * @return 修改信息
     */
    @PutMapping()
    public AjaxResult upDate(@RequestBody RePool pool){
        return rePoolService.upDate(pool);
    }

    /**
     * 定时执行的模块(未开启)
     */
    @PostMapping
    public void reJob() throws SchedulerException, TaskException {
        rePoolService.reJob();
    }

}
