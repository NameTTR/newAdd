package com.family.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatisPlus配置类
 * 用于配置MyBatisPlus的相关拦截器，以支持分页插件等功能。
 * @author 高俊炜
 * @date 2024/7/24 15:10
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 配置MybatisPlusInterceptor拦截器
     * 该拦截器用于增强MyBatisPlus的功能，如分页插件的实现。
     *
     * @return MybatisPlusInterceptor实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 创建分页插件实例，并设置数据库类型为MySQL
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        // 将分页插件添加到拦截器链中
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }
}
