package com.family.pb.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 绘本阅读记录 状态枚举类
 *
 * @author 陈文杰
 * @since 2024-06-25
 */
public enum PbBooksState {
    UNREAD(0, "未阅读"),   // 未阅读
    READ_FINISH(1, "已阅读"),    // 已阅读
    ;
    @EnumValue
    @JsonValue
    private int value;
    private String decs;
    PbBooksState(int value, String decs) {
        this.value = value;
        this.decs = decs;
    }
}
