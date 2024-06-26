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

    public static final Integer TASK_COMMPLETE = 1;

    public static final Integer TASK_NOT_COMMPLETE = 0;

    public static final Integer TASK_DELETE = 1;

    public static final Integer TASK_NOT_DELETE = 0;

    public static final Integer TASK_REAPEAT = 1;

    public static final Integer TASK_NOT_REAPRAT = 0;

    public static final Integer TASK_LABEL = 1;

    public static final Integer TASK_NOT_LABEL = 0;

    public static final Integer TASK_END = 1;

    public static final Integer TASK_NOT_END = 0;

    public static final Integer TASK_HAVE_CHILE = 1;

    public static final Integer TASK_HAVE_NOT_CHILE = 0;

    public static final Integer TASK_TIMEOUT = 1;

    public static final Integer TASK_NOT_TIMEOUT = 0;

    public static final String TASK_SKIP = "taskSkip";

    public static final Integer TASK_REMIND = 1;

    public static final Integer TASK_NOT_REMIND = 1;
}