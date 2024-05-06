package com.family.us.domain;

/**
 * 用户登录对象
 * 
 * @author ruoyi
 */
public class FamilyTelLoginBody
{
    /**
     * 手机号
     */
    private String Tel;

    /**
     * 验证码
     */
    private String code;

    /**
     * 唯一标识
     */
    private String uuid;

    public String getTel()
    {
        return Tel;
    }

    public void setTel(String Tel)
    {
        this.Tel = Tel;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }
}
