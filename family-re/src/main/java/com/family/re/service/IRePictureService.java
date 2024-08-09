package com.family.re.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.family.re.domain.po.RePicture;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-31
 */
public interface IRePictureService extends IService<RePicture> {

    /**
     * 获取图片
     * @param user 用户
     * @param pictureName 图片名称
     * @return 图片信息
     */
    AjaxResult getUrl(String user, String pictureName);

    /**
     * 添加图片
     * @param file 图片文件
     * @param user 用户
     * @return 添加信息
     */
    AjaxResult addPicture(MultipartFile file, String user);

    /**
     * 删除图片
     * @param user 用户
     * @param pictureName 图片名称
     * @return 删除信息
     */
    AjaxResult deletePicture(String user, String pictureName);

    /**
     * 获取图片列表
     * @param user 用户
     * @return 图片列表
     */
    AjaxResult getPictureList(String user);
}
