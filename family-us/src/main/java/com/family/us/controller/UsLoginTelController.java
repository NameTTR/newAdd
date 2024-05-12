package com.family.us.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/4/29 14:27
 */
@RestController
@RequestMapping("/family/us")
public class UsLoginTelController extends BaseController {



    @PostMapping("/sendCode")
    public AjaxResult sendCode(String tel){
        String code = "123456";
        Map data = new HashMap();
        data.put("code", code);
        return AjaxResult.success(data);
    }

//    @PostMapping("/loginByTel")
//    public AjaxResult loginByTel(@RequestBody FamilyTelLoginBody familyTelLoginBody){
//        AjaxResult ajax = AjaxResult.success();
//        String token = loginByTelService.login(familyTelLoginBody.getUsername(), familyTelLoginBody.getPassword(), familyTelLoginBody.getCode(),
//                familyTelLoginBody.getUuid());
//        ajax.put(Constants.TOKEN, token);
//        return ajax;
//    }
}