package com.family.common.domain.result;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * whisper模型返回结果
 * 语音转文字
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-11
 */
@Data
public class WhisperResult {
    /**
     * 是否成功 成功：1 失败：0
     */
    private int code;

    /**
     * 信息
     */
    private String msg;

    /**
     * 返回的结果
     */
    private List<String> text;
}
