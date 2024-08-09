package com.family.common.config;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpConfig {
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES) // 设置连接超时时间
                .readTimeout(10, TimeUnit.MINUTES)    // 设置读取超时时间
                .writeTimeout(10, TimeUnit.MINUTES)   // 设置写入超时时间
                .build();
    }
}