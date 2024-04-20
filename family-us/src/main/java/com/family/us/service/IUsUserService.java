package com.family.us.service;

import com.family.us.domain.UsUser;

import java.util.List;

/**
 * 用户Service接口
 * 
 * @author ruoyi
 * @date 2024-04-17
 */
public interface IUsUserService 
{
    /**
     * 查询用户
     * 
     * @param ID 用户主键
     * @return 用户
     */
    public UsUser selectUsUserByID(Integer ID);

    /**
     * 修改用户
     * 
     * @param usUser 用户
     * @return 结果
     */
    public int updateUsUser(UsUser usUser);

    /**
     * 检查手机号是否唯一
     *
     * @param usUser 需要用户
     * @return 结果
     */
    public boolean checkTelUnique(UsUser usUser);

    /**
     * 重置密码
     *
     * @param ID,newPassword 需要用户主键和新密码
     * @return 结果
     */
    public int resetUserPwd(Integer ID, String newPassword);

    /**
     * 更换图片
     *
     * @param ID,avatar 需要用户主键和图片路径
     * @return 结果
     */
    public boolean updateUserAvatar(Integer ID, String avatar);

    /**
     * 更换背景图
     *
     * @param ID,background 需要用户主键和图片路径
     * @return 结果
     */
    public boolean updateUserBackground(Integer ID, String background);
}
