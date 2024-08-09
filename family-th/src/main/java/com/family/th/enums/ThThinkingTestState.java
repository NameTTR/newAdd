package com.family.th.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 思维个体测试 状态枚举类
 *
 * @author 陈文杰
 * @since 2024-07-01
 */
public enum ThThinkingTestState {
    ERROR(0, "错误"), // 错误
    READY(1, "正确"),  // 正确
    NOTFINISHED(2, "未测试"), // 未测试
    FINISHING(3, "测试中"), // 测试中
    ;
    @EnumValue
    @JsonValue
    private int value;
    private String decs;
    ThThinkingTestState(int value, String decs) {
        this.value = value;
        this.decs = decs;
    }
}
