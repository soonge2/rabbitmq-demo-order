package com.example.rabbitmqdemoorder.message;

import java.time.Instant;

/**
 * 메시지 모델
 */
public record OrderCreatedEvent (
        Long orderId,
        String userId,
        int amount,
        Instant createdAt
) {}