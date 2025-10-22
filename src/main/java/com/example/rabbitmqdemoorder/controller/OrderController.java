package com.example.rabbitmqdemoorder.controller;

import com.example.rabbitmqdemoorder.config.RabbitConfig;
import com.example.rabbitmqdemoorder.message.OrderCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * Producer (REST -> RabbitMQ 발행)
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final RabbitTemplate rabbitTemplate;

    public OrderController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/create")
    public String create(@RequestParam Long orderId,
                         @RequestParam String userId,
                         @RequestParam int amount) {

        OrderCreatedEvent event = new OrderCreatedEvent(orderId, userId, amount, Instant.now());

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                event
        );

        return "published: " + event.orderId();
    }
}
