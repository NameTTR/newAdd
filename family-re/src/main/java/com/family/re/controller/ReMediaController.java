package com.family.re.controller;


import com.family.re.service.IReMediaService;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 朱桐山
 * @since 2024-08-02
 */
@RestController
@AllArgsConstructor
@RequestMapping("/family/re/media")
public class ReMediaController {

    private final IReMediaService reMediaService;


    /**
     * 获取媒体
     * @param user 用户
     * @param mediaName 媒体名称
     * @return 媒体信息
     */
    @GetMapping("/{user}/{media_name}")
    public AjaxResult getMedia(@PathVariable String user, @PathVariable("media_name") String mediaName) {
        return reMediaService.getMedia(user, mediaName);
    }

    /**
     * 添加媒体
     * @param file 媒体文件
     * @param user 用户
     * @return 添加信息
     */
    @PostMapping()
    public AjaxResult addMedia(@RequestParam("media_name") MultipartFile file, @RequestParam("user") String user) {
        return reMediaService.addMedia(file, user);
    }

    /**
     * 删除媒体
     * @param user 用户
     * @param mediaName 媒体名称
     * @return 删除信息
     */
    @DeleteMapping("/{user}/{media_name}")
    public AjaxResult deleteMedia(@PathVariable String user, @PathVariable("media_name") String mediaName) {
        return reMediaService.deleteMedia(user, mediaName);
    }

    /**
     * 获取媒体列表
     * @param user 用户
     * @return 媒体列表
     */
    @GetMapping("/{user}")
    public AjaxResult getMediaList(@PathVariable String user) {
        return reMediaService.getMediaList(user);
    }

}
