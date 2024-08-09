package com.family.re.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.re.domain.po.ReMedia;
import com.family.re.mapper.ReMediaMapper;
import com.family.re.service.IReMediaService;
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
 * @since 2024-08-02
 */
@Service
public class ReMediaServiceImpl extends ServiceImpl<ReMediaMapper, ReMedia> implements IReMediaService {

    /**
     * 获取媒体
     * @param user 用户
     * @param mediaName 媒体名称
     * @return 媒体信息
     */
    @Override
    public AjaxResult getMedia(String user, String mediaName) {

        if (user.isEmpty()) {
            return AjaxResult.error("用户不能为空");
        }

        if (mediaName.isEmpty()) {
            return AjaxResult.error("媒体名称不能为空");
        }

        String url = lambdaQuery().eq(ReMedia::getMediaName, mediaName)
                .eq(ReMedia::getUser, user).one().getMediaUrl();
        if (url.isEmpty()) {
            return AjaxResult.error("没找到改媒体");
        }
        return AjaxResult.success("获取成功",url);
    }

    /**
     * 添加媒体
     * @param file 媒体文件
     * @param user 用户
     * @return 添加信息
     */
    @Override
    public AjaxResult addMedia(MultipartFile file, String user) {
        try {
            if (file.isEmpty()) {
                return AjaxResult.error("媒体不能为空");
            }
            if (user.isEmpty()) {
                return AjaxResult.error("用户不能为空");
            }

            ReMedia reMedia = new ReMedia();
            reMedia.setMediaName(file.getOriginalFilename());
            reMedia.setUser(user);

            String mediaUrl = FileUploadUtils
                    .upload(FamilyConfig.getMediaPath(), file, MimeTypeUtils.MEDIA_EXTENSION);
            if (mediaUrl.isEmpty()) {
                return AjaxResult.error("媒体上传失败");
            }
            String url = removeFirstTwoDirectories(mediaUrl);
            reMedia.setMediaUrl(url);
            if (!save(reMedia)) {
                return AjaxResult.error("媒体上传失败");
            }
            return AjaxResult.success("媒体上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("媒体上传逻辑失败");
        }
    }

    private String removeFirstTwoDirectories(String url) {
        int firstSlash = url.indexOf('/');
        int secondSlash = url.indexOf('/', firstSlash + 1);

        if (firstSlash!= -1 && secondSlash!= -1) {
            return url.substring(secondSlash);
        }
        return url;
    }

    /**
     * 删除媒体
     * @param user 用户
     * @param mediaName 媒体名称
     * @return 删除信息
     */
    @Override
    public AjaxResult deleteMedia(String user, String mediaName) {
        if(user.isEmpty()) {
            return AjaxResult.error("用户不能为空");
        }
        if (mediaName.isEmpty()) {
            return AjaxResult.error("媒体名称不能为空");
        }

        String mediaPath = "/home/ruoyi/uploadPath" + lambdaQuery().eq(ReMedia::getUser, user)
                .eq(ReMedia::getMediaName, mediaName).one().getMediaUrl();
        File mediaFile = new File(mediaPath);
        if (mediaFile.exists() && mediaFile.isFile()) {
            if (mediaFile.delete()) {
                AjaxResult.error("媒体删除成功");
            } else {
                AjaxResult.error("媒体删除失败");
            }
        } else {
            AjaxResult.error("图片不存在");
        }

        if (!lambdaUpdate().eq(ReMedia::getUser, user).eq(ReMedia::getMediaName, mediaName).remove()) {
            return AjaxResult.error("删除失败");
        }
        return AjaxResult.success("删除成功");
    }

    /**
     * 获取媒体列表
     * @param user 用户
     * @return 媒体列表
     */
    @Override
    public AjaxResult getMediaList(String user) {
        if (user.isEmpty()) {
            return AjaxResult.error("用户不能为空");
        }
        List<ReMedia> list = lambdaQuery().eq(ReMedia::getUser, user).list();
        return AjaxResult.success("获取成功",list);
    }
}
