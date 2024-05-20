package com.family.pl.constant;

import com.ruoyi.common.constant.Constants;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/5/20 9:04
 */
public class TaskConstants extends Constants {
    /**
     * 定时任务白名单配置（仅允许访问的包名，如其他需要可以自行添加）
     */
    public static final String[] JOB_WHITELIST_STR = { "com.ruoyi.quartz.task", "com.family.pl.task" };

    /**
     * 定时任务违规的字符
     */
    public static final String[] JOB_ERROR_STR = { "java.net.URL", "javax.naming.InitialContext", "org.yaml.snakeyaml",
            "org.springframework", "org.apache", "com.ruoyi.common.utils.file", "com.ruoyi.common.config", "com.ruoyi.generator" };
}