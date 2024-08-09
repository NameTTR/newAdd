package com.family.pl.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * SpringContextHolder类用于存储和提供ApplicationContext实例。
 * 通过实现ApplicationContextAware接口，自动注入ApplicationContext。
 * 该类提供静态方法，以便在任何地方获取Bean实例，无需直接依赖于ApplicationContext。
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {

    /**
     * 存储ApplicationContext实例的静态变量。
     */
    private static ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的回调方法。
     * 该方法用于存储传入的ApplicationContext。
     *
     * @param applicationContext 应用程序上下文，包含所有Bean的集合。
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextHolder.applicationContext = applicationContext;
    }

    /**
     * 通过类类型获取Bean实例。
     * 这允许以类型安全的方式获取Bean，无需知道其确切的Bean名称。
     *
     * @param requiredType 需要的Bean的类类型。
     * @return 符合requiredType类型的Bean实例。
     */
    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    /**
     * 通过Bean名称获取Bean实例。
     * 这种方法提供了获取特定名称Bean的灵活性，特别是在需要非类型安全的方式获取Bean时。
     *
     * @param name 需要的Bean的名称。
     * @return 与名称匹配的Bean实例。
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }
}

