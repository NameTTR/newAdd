package com.family.pl.task;

import org.quartz.PersistJobDataAfterExecution;
import org.springframework.stereotype.Component;

/**
 * 功能：
 * 作者：Name
 * 日期：2024/5/19 17:26
 */
@Component
@PersistJobDataAfterExecution
public class TestTask {
    public void test(){

        System.out.println("执行任务");
    }
}