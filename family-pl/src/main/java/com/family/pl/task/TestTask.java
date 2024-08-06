package com.family.pl.task;

import com.family.pl.config.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 到达提醒时间后执行的提醒任务
 * </p>
 *
 * @author 高俊炜
 * @since 2024-7-9
 */
@Slf4j
@Component
public class TestTask {

    //private static final Logger logger = LoggerFactory.getLogger(TestTask.class);

    private SimpMessagingTemplate messagingTemplate;

    public TestTask() {
        this.messagingTemplate = SpringContextHolder.getBean(SimpMessagingTemplate.class);
    }
    public void test(){
        messagingTemplate.convertAndSend("/topic/messages", "到点了");
//        LocalTime localTime = LocalTime.now();
//        System.out.println("执行任务的时间"+localTime);
        log.info("Message sent to WebSocket: " + "到点了");
        System.out.println("执行任务");
    }
}