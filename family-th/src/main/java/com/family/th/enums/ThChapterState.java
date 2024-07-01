package com.family.th.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 章节 状态枚举类
 *
 * @author 陈文杰
 * @since 2024-07-01
 */
public enum ThChapterState {
    UNLEARNED0(0, "未学"), // 未学
    LEARNING(1, "学习中"),  // 学习中
    LEARNED(2, "已学完")   // 已学完
    ;
    @EnumValue
    @JsonValue
    private int value;
    private String decs;
    ThChapterState(int value, String decs) {
        this.value = value;
        this.decs = decs;
    }
}
