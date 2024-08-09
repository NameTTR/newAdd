package com.family.re.controller;


import com.family.re.service.IRePictureService;
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
 * @since 2024-07-31
 */
@RestController
@AllArgsConstructor
@RequestMapping("/family/re/picture")
public class RePictureController {

    private final IRePictureService rePictureService;

    /**
     * 获取图片
     * @param user 用户
     * @param pictureName 图片名称
     * @return 图片信息
     */
    @GetMapping("/{user}/{picture_name}")
    public AjaxResult getPicture(@PathVariable String user, @PathVariable("picture_name") String pictureName) {
        return rePictureService.getUrl(user,pictureName);
    }


    /**
     * 添加图片
     * @param file 图片文件
     * @param user 用户
     * @return 添加信息
     */
    @PostMapping()
    public AjaxResult addPicture(@RequestParam("picture_name") MultipartFile file, @RequestParam("user") String user) {
        return rePictureService.addPicture(file,user);
    }

    /**
     * 删除图片
     * @param user 用户
     * @param pictureName 图片名称
     * @return 删除信息
     */
    @DeleteMapping("/{user}/{picture_name}")
    public AjaxResult deletePicture(@PathVariable String user, @PathVariable("picture_name") String pictureName) {
        return rePictureService.deletePicture(user,pictureName);
    }

    /**
     * 获取图片列表
     * @param user 用户
     * @return 图片列表
     */
    @GetMapping("/{user}")
    public AjaxResult getPictureList(@PathVariable String user) {
        return rePictureService.getPictureList(user);
    }

}
