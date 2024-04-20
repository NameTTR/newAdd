package com.family.us.controller;

import com.family.us.domain.UsUser;
import com.family.us.service.IUsUserService;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户Controller
 *
 * @author ruoyi
 * @date 2024-04-17
 */
@RestController
@RequestMapping("/family/us")
public class UsUserController extends BaseController
{
    @Autowired
    private IUsUserService usUserService;

    /**
     * 获取用户详细信息
     */
    @GetMapping(value = "/info/{ID}")
    public AjaxResult getInfo(@PathVariable("ID") Integer ID) {
        return success(usUserService.selectUsUserByID(ID));
    }

    /**
     * 修改用户信息
     */
    @PutMapping("/updateProfile")
    public AjaxResult edit(@RequestBody UsUser usUser) {

        UsUser nowUsUser = usUserService.selectUsUserByID(usUser.getID());

        if(nowUsUser == null){
            return error("该用户不存在");
        }

        if(StringUtils.isNotEmpty(usUser.getNickname())) {
            nowUsUser.setNickname(usUser.getNickname());
        }
        if(usUser.getSex() == null) {
            if(usUser.getSex() == 1 || usUser.getSex() == 2) {
                nowUsUser.setSex(usUser.getSex());
            } else{
                return error("格式不符合");
            }
        }
        if(usUser.getAgeBegin() != null) {
            if(usUser.getAgeBegin() >= 150){
                return error("年龄过大");
            }
            nowUsUser.setAgeBegin(usUser.getAgeBegin());
        }
        if(usUser.getAgeEnd() != null) {
            if(usUser.getAgeEnd() == null){
                return error("未设置开始年龄");
            }
            if(usUser.getAgeEnd() >= 150){
                return error("年龄过大");
            }
            nowUsUser.setAgeEnd(usUser.getAgeEnd());
        }
        if(usUser.getGrade() != null) {
            if(usUser.getGrade() >= 0 && usUser.getGrade() <= 22){
                nowUsUser.setGrade(usUser.getGrade());
            } else {
                return error("格式不符合");
            }
        }
        if(usUser.getRole() != null) {
            if(usUser.getRole() >= 0 && usUser.getRole() <= 4) {
                nowUsUser.setNickname(usUser.getNickname());
            } else{
                return error("格式不符合");
            }
        }
        if(StringUtils.isNotEmpty(usUser.getDistrictName())) {
            nowUsUser.setDistrictName(usUser.getDistrictName());
        }
        if(StringUtils.isNotEmpty(usUser.getJobName())) {
            nowUsUser.setJobName(usUser.getJobName());
        }
        if(StringUtils.isNotEmpty(usUser.getTel())){
            if(!usUserService.checkTelUnique(usUser)){
                return error("修改用户失败，手机号码已存在");
            } else {
                nowUsUser.setTel(usUser.getTel());
            }
        }
        if(usUser.getNotice() != null) {
            if(usUser.getNotice() == 0 || usUser.getNotice() == 1){
                nowUsUser.setNickname(usUser.getNickname());
            } else {
                return error("格式不符合");
            }
        }
        if(usUser.getBells() != null){
            if(usUser.getBells() > 0){
                nowUsUser.setBells(usUser.getBells());
            }else {
              return error("格式不符合");
            }
        }
        if(usUser.getVibrate() != null) {
            if(usUser.getVibrate() >= 0 && usUser.getNotice() <= 2){
                nowUsUser.setVibrate(usUser.getVibrate());
            } else {
                return error("格式不符合");
            }
        }
        if(usUser.getPrivacyRecommend() != null) {
            if(usUser.getPrivacyRecommend() >= 0 && usUser.getPrivacyRecommend() <= 1){
                nowUsUser.setPrivacyRecommend(usUser.getPrivacyRecommend());
            } else {
                return error("格式不符合");
            }
        }
        if(usUser.getPrivacyCamera() != null) {
            if(usUser.getPrivacyCamera() >= 0 && usUser.getPrivacyCamera() <= 1){
                nowUsUser.setPrivacyCamera(usUser.getPrivacyCamera());
            } else {
                return error("格式不符合");
            }
        }
        if(usUser.getPrivacyAlbum() != null) {
            if(usUser.getPrivacyAlbum() >= 0 && usUser.getPrivacyAlbum() <= 1){
                nowUsUser.setPrivacyAlbum(usUser.getPrivacyAlbum());
            }
            else {
                return error("格式不符合");
            }
        }
        if(usUser.getPrivacyMike() != null) {
            if(usUser.getPrivacyMike() >= 0 && usUser.getPrivacyMike() <= 1){
                nowUsUser.setPrivacyMike(usUser.getPrivacyMike());
            }
            else {
                return error("格式不符合");
            }
        }
        if(usUser.getTeenageMode() != null) {
            if(usUser.getTeenageMode() >= 0 && usUser.getTeenageMode() <= 1){
                nowUsUser.setTeenageMode(usUser.getTeenageMode());
            }
            else {
                return error("格式不符合");
            }
        }
        if(StringUtils.isNotEmpty(usUser.getTel())){
            if(!usUserService.checkTelUnique(usUser)){
                return error("修改用户失败，手机号码已存在");
            }
        }
        if(usUser.getBalance() != null) {
            if(usUser.getBalance() < 0){
                return error("格式不符合");
            }
            nowUsUser.setBalance(usUser.getBalance());
        }
        if(usUser.getCancellation() != null){
            if(usUser.getCancellation() == 0 || usUser.getCancellation() == 1){
                return error("格式不符合");
            }
            nowUsUser.setCancellation(usUser.getCancellation());
        }
        if(usUser.getCountTask() != null) {
            if(usUser.getCountTask() < 0){
                return error("格式不符合");
            }
            nowUsUser.setCountTask(usUser.getCountTask());
        }
        if(usUser.getCountTool() != null){
            if(usUser.getCountTool() < 0){
                return error("格式不符合");
            }
            nowUsUser.setCountTool(usUser.getCountTool());
        }
        if(usUser.getCountQuestions() != null){
            if(usUser.getCountQuestions() < 0){
                return error("格式不符合");
            }
            nowUsUser.setCountQuestions(usUser.getCountQuestions());
        }
        if(usUser.getCountTest() != null){
            if(usUser.getCountTest() < 0){
                return error("格式不符合");
            }
            nowUsUser.setCountTest(usUser.getCountTest());
        }
        return toAjax(usUserService.updateUsUser(nowUsUser));
    }

    /**
     * 重置密码
     */
    @PutMapping("/updatePwd")
    public AjaxResult updatePwd(@RequestParam("ID") Integer ID,
                                @RequestParam("oldPassword") String oldPassword,
                                @RequestParam("newPassword") String newPassword)
    {
        UsUser nowUsUser = usUserService.selectUsUserByID(ID);
        String password = nowUsUser.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password))
        {
            return error("修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, password))
        {
            return error("新密码不能与旧密码相同");
        }
        newPassword = SecurityUtils.encryptPassword(newPassword);
        if (usUserService.resetUserPwd(ID, newPassword) > 0)
        {
            return success();
        }
        return error("修改密码异常，请联系管理员");
    }

    /**
     * 头像上传
     */
    @PostMapping("/avatar")
    public AjaxResult avatar(@RequestParam("ID") Integer ID, @RequestParam("avatarfile") MultipartFile file) throws Exception
    {
        if (!file.isEmpty())
        {
            UsUser nowUsUser = usUserService.selectUsUserByID(ID);
            String avatar = FileUploadUtils.upload(RuoYiConfig.getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION);
            if (usUserService.updateUserAvatar(nowUsUser.getID(), avatar))
            {
                AjaxResult ajax = AjaxResult.success();
                ajax.put("imgUrl", avatar);
                return ajax;
            }
        }
        return error("上传图片异常，请联系管理员");
    }

    /**
     * 修改用户背景
     */
    @PostMapping("/background")
    public AjaxResult background(@RequestParam("ID") Integer ID, @RequestParam("backgroundfile") MultipartFile file) throws Exception
    {
        if (!file.isEmpty())
        {
            UsUser nowUsUser = usUserService.selectUsUserByID(ID);
            String background = FileUploadUtils.upload(RuoYiConfig.getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION);
            if (usUserService.updateUserBackground(nowUsUser.getID(), background))
            {
                AjaxResult ajax = AjaxResult.success();
                ajax.put("imgUrl", background);
                return ajax;
            }
        }
        return error("上传图片异常，请联系管理员");
    }
}
