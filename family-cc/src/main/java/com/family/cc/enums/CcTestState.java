package com.family.cc.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 测试 状态枚举类
 *
 * @author 陈文杰
 * @since 2024-05-04
 */
public enum CcTestState {
    NOTFINISHED(0, "未完成"), // 未完成
    DOING(1, "进行中"), // 进行中
    FINISHED(2, "已完成"), // 已完成
    ;
    @EnumValue
    @JsonValue
    private int value;
    private String decs;
    CcTestState(int value, String decs) {
        this.value = value;
        this.decs = decs;
    }

}
