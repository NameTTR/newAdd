package com.family.re.controller;


import com.family.re.domain.po.RePrize;
import com.family.re.service.IRePrizeService;
import com.family.re.service.impl.RePrizeServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 奖品表 前端控制器
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-03
 */
@RestController
@AllArgsConstructor
@RequestMapping("/family/re/prize")
public class RePrizeController {

    private final IRePrizeService rePrizeService;


    /**
     * 获取总奖品列表
     * @param name 上一层奖品池中选中的奖品
     * @return 总奖品列表
     */
    @GetMapping()
    public AjaxResult getPrizeList(@RequestParam List<String> name) {
        return rePrizeService.getPrizeList(name);
    }

    /**
     * 删除总池中的奖品
     * @param prizeId 总池中的奖品id
     * @return 删除奖品总池中奖品的结果
     */
    @DeleteMapping("/{prize_id}")
    public AjaxResult deletePrize(@PathVariable("prize_id") Long prizeId) {
        return rePrizeService.deletePrize(prizeId);
    }

    /**
     * 添加奖品
     * @param prizeIco 要添加的奖品图标
     * @param prizeName 要添加的奖品名称
     * @return 往奖品总池中添加奖品的结果
     */
    @PostMapping()
    public AjaxResult addPrize(String prizeIco, String prizeName) {
        return rePrizeService.addPrize(prizeIco, prizeName);
    }

    /**
     * 修改总池中的奖品
     * @param rePrize 总池的奖品类型
     * @return 修改总池中的奖品的结果
     */
    @PutMapping()
    public AjaxResult revisionPrize(@RequestBody RePrize rePrize) {
        return rePrizeService.revisionPrize(rePrize);
    }

    /**
     * 抽奖
     * @return 随机数结果
     */
    @GetMapping("/lottery")
    public AjaxResult lottery() {
        return rePrizeService.lottery();
    }

}
