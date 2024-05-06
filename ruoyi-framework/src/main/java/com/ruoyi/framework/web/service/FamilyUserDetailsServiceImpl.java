package com.ruoyi.framework.web.service;

import com.family.us.domain.FamilyLoginUser;
import com.family.us.domain.UsUser;
import com.family.us.service.IUsUserService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户验证处理
 *
 * @author ruoyi
 */
@Service
public class FamilyUserDetailsServiceImpl extends UserDetailsServiceImpl
{
    private static final Logger log = LoggerFactory.getLogger(FamilyUserDetailsServiceImpl.class);

    @Autowired
    private IUsUserService userService;
    
    @Autowired
    private FamilyPasswordService passwordService;

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException
    {
        UsUser user = userService.selectUsUserByAccount(account);
        if (StringUtils.isNull(user))
        {
            log.info("登录用户：{} 不存在.", account);
            throw new ServiceException(MessageUtils.message("user.not.exists"));
        }
        else if (UserStatus.DELETED.getCode().equals(user.getFlagDelete()))
        {
            log.info("登录用户：{} 已被删除.", account);
            throw new ServiceException(MessageUtils.message("user.password.delete"));
        }

        passwordService.validate(user);

        return createLoginUser(user);
    }

    public UserDetails createLoginUser(UsUser user)
    {
        return new FamilyLoginUser(user.getID(), user);
    }
}
