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
@RequestMapping("/family/rePrize")
public class RePrizeController {

    private final IRePrizeService rePrizeService;


    /**
     * 获取奖品列表
     * @return 奖品列表
     */
    @GetMapping("/getPrizeList")
    public AjaxResult getPrizeList(@RequestBody List<String> name) {
        return rePrizeService.getPrizeList(name);
    }

    /**
     * 删除奖品
     * @param prizeId 奖品id
     * @return 删除结果
     */
    @DeleteMapping("/deletePrize/{prizeId}")
    public AjaxResult deletePrize(@PathVariable Long prizeId) {
        return rePrizeService.deletePrize(prizeId);
    }

    /**
     * 添加奖品
     * @param prizeIco 奖品图标
     * @param prizeName 奖品名称
     * @return 添加结果
     */
    @PostMapping()
    public AjaxResult addPrize(String prizeIco,String prizeName) {
        return rePrizeService.addPrize(prizeIco,prizeName);
    }

    /**
     * 修改奖品
     * @param rePrize 奖品
     * @return 修改结果
     */
    @PutMapping("/changePrize")
    public AjaxResult changePrize(@RequestBody RePrize rePrize) {
        return rePrizeService.changePrize(rePrize);
    }

    /**
     * 抽奖
     * @param count 奖品数量
     * @return 随机数结果
     */
    @GetMapping("/lottery/{count}")
    public AjaxResult lottery( @PathVariable int count) {
        return rePrizeService.lottery( count);
    }

}
