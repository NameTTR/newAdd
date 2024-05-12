package com.family.us.service;

import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.stereotype.Service;

@Service
public interface SmsLoginService {
    AjaxResult smsLogin(String tel, String smsCode, String uuid);
}
