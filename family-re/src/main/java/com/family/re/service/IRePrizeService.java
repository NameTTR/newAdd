package com.family.re.service;

import com.family.re.domain.po.RePrize;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 奖品表 服务类
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-03
 */
public interface IRePrizeService extends IService<RePrize> {

    /**
     * 获取总奖品列表
     * @param name 上一层奖品池中选中的奖品
     * @return 总奖品列表
     */
    AjaxResult getPrizeList(List<String> name);

    /**
     * 删除总池中的奖品
     * @param prizeId 总池中的奖品id
     * @return 删除结果
     */
    AjaxResult deletePrize(Long prizeId);

    /**
     * 添加奖品
     * @param file 要添加的奖品图标
     * @param prizeName 要添加的奖品名称
     * @return 往奖品总池中添加奖品的结果
     */
    AjaxResult addPrize(MultipartFile file, String prizeName);

    /**
     * 修改总池中的奖品
     * @param rePrize 总池的奖品类型
     * @return 修改总池中的奖品的结果
     */
    AjaxResult revisionPrize(RePrize rePrize);

    /**
     * 抽奖
     * @return 随机数结果
     */
    AjaxResult lottery();
}
