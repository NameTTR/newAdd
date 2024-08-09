package com.family.en.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 汉字测试 状态枚举类
 *
 * @author 陈文杰
 * @since 2024-06-25
 */
public enum EnWordTestState {
    ERROR(0, "错误"), // 错误
    READY(1, "正确"),  // 正确
    NOTFINISHED(2, "未测试"), // 未测试
    ;
    @EnumValue
    @JsonValue
    private int value;
    private String decs;
    EnWordTestState(int value, String decs) {
        this.value = value;
        this.decs = decs;
    }
}
