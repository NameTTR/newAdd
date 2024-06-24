package com.family.us.service;

import com.family.us.domain.VO.UsUserRegisterVO;
import com.ruoyi.common.core.domain.AjaxResult;

public interface RegisterService {
    AjaxResult register(UsUserRegisterVO user);
}
