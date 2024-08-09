package com.family.re.service;

import com.family.re.domain.po.ReMedia;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 朱桐山
 * @since 2024-08-02
 */
public interface IReMediaService extends IService<ReMedia> {

    /**
     * 获取媒体
     * @param user 用户
     * @param mediaName 媒体名称
     * @return 媒体信息
     */
    AjaxResult getMedia(String user, String mediaName);

    /**
     * 添加媒体
     * @param file 媒体文件
     * @param user 用户
     * @return 添加信息
     */
    AjaxResult addMedia(MultipartFile file, String user);

    /**
     * 删除媒体
     * @param user 用户
     * @param mediaName 媒体名称
     * @return 删除信息
     */
    AjaxResult deleteMedia(String user, String mediaName);

    /**
     * 获取媒体列表
     * @param user 用户
     * @return 媒体列表
     */
    AjaxResult getMediaList(String user);

}
