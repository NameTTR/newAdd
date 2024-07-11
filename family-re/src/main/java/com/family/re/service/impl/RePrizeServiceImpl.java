package com.family.re.service.impl;

import com.family.re.domain.dto.RePrizeDTO;
import com.family.re.domain.dto.ReSelectedPrizesDTO;
import com.family.re.domain.po.RePrize;
import com.family.re.mapper.RePrizeMapper;
import com.family.re.service.IRePrizeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <p>
 * 奖品表 服务实现类
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-03
 */
@Service
public class RePrizeServiceImpl extends ServiceImpl<RePrizeMapper, RePrize> implements IRePrizeService {


    /**
     * 获取奖品列表
     * @return 奖品列表
     */
    @Override
    public AjaxResult getPrizeList(List<String> name) {

        RePrizeDTO rePrizes = new RePrizeDTO();

        //公共奖品
        List<RePrize> list = lambdaQuery().isNull(RePrize::getCreatedUserId).list();

        rePrizes.setPublicPrizes(make(list,name));

        //私人奖品
        List<RePrize> list1 = lambdaQuery().eq(RePrize::getCreatedUserId, 1).list();
        rePrizes.setPrivatePrizes(make(list1,name));

        return AjaxResult.success(rePrizes);
    }

    List<ReSelectedPrizesDTO> make(List<RePrize> list,List<String> name) {
        List<ReSelectedPrizesDTO> reSelectedPrizesDTOS = new ArrayList<>(list.size());
        boolean selected = false;
        for (RePrize rePrize : list) {
            for (String s : name) {
                if (s.equals(rePrize.getPrizeName())) {
                    selected = true;
                    break;
                }

            }
            reSelectedPrizesDTOS.add(ReSelectedPrizesDTO.of(rePrize,selected));
            selected = false;
        }
        return reSelectedPrizesDTOS;
    }


    /**
     * 删除奖品
     * @param prizeId 奖品id
     * @return 删除结果
     */
    @Override
    public AjaxResult deletePrize(Long prizeId) {
        if(!lambdaUpdate().eq(RePrize::getId,prizeId).remove())
            return AjaxResult.error("删除失败");
        return AjaxResult.success("删除成功");
    }

    /**
     * 添加奖品
     * @param prizeIco 奖品图标
     * @param prizeName 奖品名称
     * @return 添加结果
     */
    @Override
    public AjaxResult addPrize(String prizeIco, String prizeName) {

        RePrize one = lambdaQuery().eq(RePrize::getCreatedUserId,1).eq(RePrize::getPrizeName,prizeName).one();
        if(one!=null)
            return AjaxResult.error("奖品已存在");

        RePrize rePrize = new RePrize();

        rePrize.setPrizeIco(prizeIco);
        rePrize.setPrizeName(prizeName);
        rePrize.setCreatedUserId(1L);
        rePrize.setFlagDelete(0);
        if(!save(rePrize))
            return AjaxResult.error("添加失败");
        return AjaxResult.success("添加成功");
    }

    /**
     * 修改奖品
     * @param rePrize 奖品
     * @return 修改结果
     */
    @Override
    public AjaxResult changePrize(RePrize rePrize) {
        if(!lambdaUpdate()
                .eq(RePrize::getId,rePrize.getId())
                .set(RePrize::getPrizeIco,rePrize.getPrizeIco())
                .set(RePrize::getPrizeName,rePrize.getPrizeName())
                .set(RePrize::getUpdateTime, LocalDateTime.now())
                .update())
            return AjaxResult.error("修改失败");
        return AjaxResult.success("修改成功");
    }

    /**
     * 抽奖
     * @param count 奖品数量
     * @return 随机数结果
     */
    @Override
    public AjaxResult lottery(int count) {
        Random random = new Random();
        int randomInt = random.nextInt(count-1);
        if(randomInt==4) randomInt+=1;
        return AjaxResult.success(randomInt);
    }
}
