package com.example.rabbitmqdemoorder.listener;

import com.example.rabbitmqdemoorder.config.RabbitConfig;
import com.example.rabbitmqdemoorder.message.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumer (@RabbitListener)
 */
@Component
public class OrderCreatedListener {
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onMessage(OrderCreatedEvent event) {
        log.info("[OrderCreatedListener] received: orderId={}, userId={}, amount={}, at={}",
                event.orderId(), event.userId(), event.amount(), event.createdAt());

        // TODO: 여기서 실제 비즈니스 로직 수행 (예: 알림 보내기, 결제 시작 등)
    }
}
