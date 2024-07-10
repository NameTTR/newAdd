package com.family.common.sevice;

import com.family.common.po.ChatTTSPo;
import com.family.common.po.ChatTTSResult;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
public class OkHttpService {

    private final OkHttpClient okHttpClient;

    private final String chatTTSUrl = "http://127.0.0.1:9966/tts";
    public ChatTTSResult sendGetRequest(String text){
        try {
            // 创建请求参数对象
            ChatTTSPo details = new ChatTTSPo();
            details.setText(text);
            Gson gson = new Gson();

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

            // 设置请求体和请求类型
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
}