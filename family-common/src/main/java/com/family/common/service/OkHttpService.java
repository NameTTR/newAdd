package com.family.common.service;

import com.family.common.domain.po.ChatTTSPo;
import com.family.common.domain.result.ChatTTSResult;
import com.family.common.domain.result.WhisperResult;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * OkHttp服务类
 * </p>
 *
 * @author 陈文杰
 * @since 2024-07-10
 */
@Service
@AllArgsConstructor
public class OkHttpService {

    private final OkHttpClient okHttpClient;

    private final Gson gson;

    // chatTTS接口地址
    private final String chatTTSUrl = "http://127.0.0.1:9966/tts";

    // whisper接口地址8.134.55.161
    private final String whisperUrl = "http://192.168.66.100:8000/whisper/";

    /**
     * 文本转语音接口
     * @param text 文本内容
     * @return
     */
    public ChatTTSResult getAudioOfChatTTS(String text){
        try {
            // 创建请求参数对象
            ChatTTSPo details = new ChatTTSPo();
            details.setText(text);
            // 构建 POST 请求的参数
            String postData = "text="+details.getText()
                    + "&prompt="+details.getPrompt()
                    + "&voice="+details.getVoice()
                    + "&temperature="+details.getTemperature()
                    + "&top_p="+details.getTopP()
                    + "&top_k="+details.getTopK()
                    + "&refine_max_new_token="+details.getRefineMaxNewToken()
                    + "&infer_max_new_token="+details.getInferMaxNewToken()
                    + "&skip_refine="+details.getSkipRefine()
                    + "&is_split="+details.getIsSplit()
                    + "&custom_voice="+details.getCustomVoice();

            // 设置请求体和请求内容类型
            RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), postData);

            // 构建请求
            Request request = new Request.Builder()
                    .url(chatTTSUrl)
                    .post(body)
                    .build();

            // 发送请求并处理响应
            try (Response response = okHttpClient.newCall(request).execute()) {
                return gson.fromJson(response.body().string(), ChatTTSResult.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 语音转文本接口
     *
     * @param files 语音文件
     * @return
     */
    public WhisperResult getTextOfWhisper(MultipartFile[] files){
        try {
            // 构建文件请求体
            MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            for (MultipartFile file : files) {
                RequestBody fileBody = RequestBody.create(MediaType.parse("audio/mpeg"), file.getBytes());
                requestBodyBuilder.addFormDataPart("files", file.getOriginalFilename(),fileBody);
            }

            // 构建multipart请求体
            MultipartBody requestBody = requestBodyBuilder.build();

            // 构建请求
            Request request = new Request.Builder()
                    .url(whisperUrl)
                    .post(requestBody)
                    .build();

            // 发送请求并处理响应
            try (Response response = okHttpClient.newCall(request).execute()) {
                return gson.fromJson(response.body().string(), WhisperResult.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}