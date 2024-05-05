package com.family.cc.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 汉字测试 状态枚举类
 *
 * @author 陈文杰
 * @since 2024-05-04
 */
public enum CcChaptersTestStatus {
    ERROR(0, "错误"), // 错误
    READY(1, "正确")  // 正确
    ;
    @EnumValue
    @JsonValue
    private int value;
    private String decs;
    CcChaptersTestStatus(int value, String decs) {
        this.value = value;
        this.decs = decs;
    }
}
