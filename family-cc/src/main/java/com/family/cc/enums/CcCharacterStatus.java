package com.family.cc.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 汉字 状态枚举类
 *
 * @author 陈文杰
 * @since 2024-05-04
 */
public enum CcCharacterStatus {
    UnLEARNED(0, "未学习"),   // 未学习
    LEARNED_FINISH(1, "已学完"),    // 已学完
    NOT_MASTERED(2, "未掌握")   // 未掌握
    ;
    @EnumValue
    @JsonValue
    private int value;
    private String decs;
    CcCharacterStatus(int value, String decs) {
        this.value = value;
        this.decs = decs;
    }
}
