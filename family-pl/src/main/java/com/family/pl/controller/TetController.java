package com.family.pl.controller;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class TetController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 开发中
     * @return
     */
    @GetMapping("family/sendMessage")
    public String sendDirectMessage(){
        String messageId = UUID.randomUUID().toString();
        String messageData = "test message,hello!";
        String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Map<String,Object> map = new HashMap<>();
        map.put("messageId",messageId);
        map.put("data",messageData);
        map.put("current",current);
        rabbitTemplate.convertAndSend("TestDirectExchange", "123", map, new CorrelationData(UUID.randomUUID().toString()));
        return "ok";
    }
}
