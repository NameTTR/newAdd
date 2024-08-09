package com.family.en.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 测试 状态枚举类
 *
 * @author 陈文杰
 * @since 2024-06-25
 */
public enum EnTestState {
    NOTFINISHED(0, "未完成"), // 未完成
    DOING(1, "进行中"), // 进行中
    FINISHED(2, "已完成"), // 已完成
    ;
    @EnumValue
    @JsonValue
    private int value;
    private String decs;
    EnTestState(int value, String decs) {
        this.value = value;
        this.decs = decs;
    }

}
