package com.family.cc.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 汉字测试 状态枚举类
 *
 * @author 陈文杰
 * @since 2024-05-04
 */
public enum CcCharacterTestState {
    ERROR(0, "错误"), // 错误
    RIGHT(1, "正确"),  // 正确
    NOTFINISHED(2, "未测试"), // 未测试
    ;
    @EnumValue
    @JsonValue
    private int value;
    private String decs;
    CcCharacterTestState(int value, String decs) {
        this.value = value;
        this.decs = decs;
    }
}
