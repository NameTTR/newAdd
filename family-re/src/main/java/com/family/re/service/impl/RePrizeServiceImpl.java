package com.family.re.service.impl;

import com.family.re.domain.dto.RePrizeDTO;
import com.family.re.domain.dto.ReSelectedPrizesDTO;
import com.family.re.domain.po.RePrize;
import com.family.re.mapper.RePrizeMapper;
import com.family.re.service.IRePrizeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.FamilyConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
     * 获取总奖品列表
     * @param name 上一层奖品池中选中的奖品
     * @return 总奖品列表
     */
    @Override
    public AjaxResult getPrizeList(List<String> name) {

        try {
            RePrizeDTO rePrizes = new RePrizeDTO();

            //获取公共奖品
            List<RePrize> list = lambdaQuery().isNull(RePrize::getCreatedUserId).list();
            rePrizes.setPublicPrizes(make(list,name));

            //获取私人奖品
            List<RePrize> list1 = lambdaQuery().eq(RePrize::getCreatedUserId, 1).list();
            rePrizes.setPrivatePrizes(make(list1,name));

            return AjaxResult.success(rePrizes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取奖品列表失败");
        }
    }

    List<ReSelectedPrizesDTO> make(List<RePrize> list,List<String> name) {
        List<ReSelectedPrizesDTO> reSelectedPrizesDTOS = new ArrayList<>(list.size());
        //判断是否在上一层奖品池中选中
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
     * 删除总池中的奖品
     * @param prizeId 总池中的奖品id
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
     * @param file 要添加的奖品图标
     * @param prizeName 要添加的奖品名称
     * @return 往奖品总池中添加奖品的结果
     */
    @Override
    public AjaxResult addPrize(MultipartFile file, String prizeName) {

        try {
            if(file.isEmpty()) {
                return AjaxResult.error("图片不能为空");
            }
            if (prizeName.isEmpty()) {
                return AjaxResult.error("奖品名称不能为空");
            }
            //判断奖品是否存在
            RePrize one = lambdaQuery().eq(RePrize::getCreatedUserId,1).eq(RePrize::getPrizeName,prizeName).one();
            if(one!=null)
                return AjaxResult.error("奖品已存在");

            RePrize rePrize = new RePrize();

            //添加奖品信息
            String iconUrl = FileUploadUtils.upload(FamilyConfig.getIconPath(), file, MimeTypeUtils.IMAGE_EXTENSION);
            //获取图片的url
            String url = removeFirstTwoDirectories(iconUrl);

            //判断图片是否上传成功
            if(url.isEmpty()) {
                return AjaxResult.error("图片上传失败");
            }
            rePrize.setPrizeIco(url);
            rePrize.setPrizeName(prizeName);
            rePrize.setCreatedUserId(1L);
            rePrize.setFlagDelete(0);

            //向数据库里添加奖品
            if(!save(rePrize))
                return AjaxResult.error("添加失败");
            return AjaxResult.success("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("添加奖品失败");
        }
    }

    /**
     * 获取图片的url
     * @param iconUrl 图片的原始url
     * @return 图片的绝对url
     */
    private String removeFirstTwoDirectories(String iconUrl) {
        int firstSlash = iconUrl.indexOf('/');
        int secondSlash = iconUrl.indexOf('/', firstSlash + 1);

        if (firstSlash!= -1 && secondSlash!= -1) {
            return iconUrl.substring(secondSlash);
        }
        return iconUrl;
    }

    /**
     * 修改总池中的奖品
     * @param rePrize 总池的奖品类型
     * @return 修改总池中的奖品的结果
     */
    @Override
    public AjaxResult revisionPrize(RePrize rePrize) {
        try {
            if(!lambdaUpdate()
                    .eq(RePrize::getId,rePrize.getId())
                    .set(RePrize::getPrizeIco,rePrize.getPrizeIco())
                    .set(RePrize::getPrizeName,rePrize.getPrizeName())
                    .set(RePrize::getUpdateTime, LocalDateTime.now())
                    .update())
                return AjaxResult.error("修改失败");
            return AjaxResult.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("修改奖品失败");
        }
    }

    /**
     * 抽奖
     * @return 随机数结果
     */
    @Override
    public AjaxResult lottery() {
        try {
            Random random = new Random();

            //生成随机数(0到7的随机数)
            int randomInt = random.nextInt(8);

            //如果随机数为4，则加1(给前端页面空出一个位置)
            if(randomInt==4) randomInt+=1;

            return AjaxResult.success(randomInt);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("抽奖失败");
        }
    }
}
