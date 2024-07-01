package com.family.pi.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 拼音测试 状态枚举类
 *
 * @author 陈文杰
 * @since 2024-05-04
 */
public enum PiPinyinTestState {
    ERROR(0, "错误"), // 错误
    RIGHT(1, "正确"),  // 正确
    NOTFINISHED(2, "未完成"), // 未完成
    ;
    @EnumValue
    @JsonValue
    private int value;
    private String decs;
    PiPinyinTestState(int value, String decs) {
        this.value = value;
        this.decs = decs;
    }
}
