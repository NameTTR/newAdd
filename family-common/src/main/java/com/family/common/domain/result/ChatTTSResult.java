package com.family.common.domain.result;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * ChatTTS模型返回结果
 * 文本转语音
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-10
 */
@Data
public class ChatTTSResult {
    /**
     * 返回码 0：成功，
     */
    private int code;
    private String msg;
    private List<Audio_Files> audio_files;
    private String filename;
    private String url;

    @Data
    public static class Audio_Files {
        private double audio_duration;
        private String filename;
        private String url;
        private double inference_time;
    }
}