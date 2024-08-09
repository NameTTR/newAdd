package com.family.re.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.re.domain.po.RePicture;
import com.family.re.mapper.RePictureMapper;
import com.family.re.service.IRePictureService;
import com.ruoyi.common.config.FamilyConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 朱桐山
 * @since 2024-07-31
 */
@Service
public class RePictureServiceImpl extends ServiceImpl<RePictureMapper, RePicture> implements IRePictureService {

    /**
     * 获取图片
     * @param user 用户
     * @param pictureName 图片名称
     * @return 图片信息
     */
    @Override
    public AjaxResult getUrl(String user, String pictureName) {

        if(user.isEmpty()) {
            return AjaxResult.error("用户不能为空");
        }

        if(pictureName.isEmpty()) {
            return AjaxResult.error("图片名称不能为空");
        }

        String url = lambdaQuery().eq(RePicture::getPictureName, pictureName)
                .eq(RePicture::getUser, user).one().getPictureUrl();
        if (url.isEmpty()) {
            return AjaxResult.error("没找到该图片");
        }
        return AjaxResult.success("获取成功",url);
    }

    /**
     * 添加图片
     * @param file 图片文件
     * @param user 用户
     * @return 添加信息
     */
    @Override
    public AjaxResult addPicture(MultipartFile file, String user) {
    try {
        if (file.isEmpty()) {
            return AjaxResult.error("图片不能为空");
        }
        if (user.isEmpty()) {
            return AjaxResult.error("用户不能为空");
        }

        RePicture rePicture = new RePicture();
        rePicture.setPictureName(file.getOriginalFilename());
        rePicture.setUser(user);

        String pictureUrl = FileUploadUtils
                .upload(FamilyConfig.getPicturePath(), file, MimeTypeUtils.IMAGE_EXTENSION);
        if (pictureUrl.isEmpty()) {
            return AjaxResult.error("图片上传失败");
        }
        String url = removeFirstTwoDirectories(pictureUrl);
        rePicture.setPictureUrl(url);
        if (!save(rePicture)) {
            return AjaxResult.error("图片保存失败");
        }
        return AjaxResult.success("图片上传成功");
    } catch (Exception e) {
        e.printStackTrace();
        return AjaxResult.error("图片上传失败");
        }
    }

    /**
     * 获取图片的url
     * @param url 图片的原始url
     * @return 图片的绝对url
     */
    private String removeFirstTwoDirectories(String url) {
        int firstSlash = url.indexOf('/');
        int secondSlash = url.indexOf('/', firstSlash + 1);

        if (firstSlash!= -1 && secondSlash!= -1) {
            return url.substring(secondSlash);
        }
        return url;
    }

    /**
     * 删除图片
     * @param user 用户
     * @param pictureName 图片名称
     * @return 删除信息
     */
    @Override
    public AjaxResult deletePicture(String user, String pictureName) {
        if(user.isEmpty()) {
            return AjaxResult.error("用户不能为空");
        }
        if (pictureName.isEmpty()) {
            return AjaxResult.error("图片名称不能为空");
        }

        String imagePath = "/home/ruoyi/uploadPath" + lambdaQuery().eq(RePicture::getUser, user)
                .eq(RePicture::getPictureName, pictureName).one().getPictureUrl();
        File imageFile = new File(imagePath);
        if (imageFile.exists() && imageFile.isFile()) {
            if (imageFile.delete()) {
                AjaxResult.error("图片删除成功");
            } else {
                AjaxResult.error("图片删除失败");
            }
        } else {
            AjaxResult.error("图片不存在");
        }

        if (!lambdaUpdate().eq(RePicture::getUser, user).eq(RePicture::getPictureName, pictureName).remove()) {
            return AjaxResult.error("删除失败");
        }
        return AjaxResult.success("删除成功");
    }

    /**
     * 获取图片列表
     * @param user 用户
     * @return 图片列表
     */
    @Override
    public AjaxResult getPictureList(String user) {
        if(user.isEmpty()) {
            return AjaxResult.error("用户不能为空");
        }
        List<RePicture> list = lambdaQuery().eq(RePicture::getUser, user).list();
        return AjaxResult.success("获取成功",list);
    }
}
