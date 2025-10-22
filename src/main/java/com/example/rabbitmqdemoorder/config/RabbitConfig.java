package com.example.rabbitmqdemoorder.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.CorrelationDataPostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 구성
 * Spring이 RabbitMQ와 통신할 수 있도록 Exchange, Queue, Binding을 등록하고 메시지 직렬화(JSON 변환), 전송 템플릿 설정을 담당.
 * 즉, 메시지가 어디로 가고, 어떤 포맷으로 변환되고, 어떻게 전송되는지를 정의하는 클래스이다.
 */
@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "orders.exchange"; // 메시지를 어디로 라우팅할지 결정하는 중앙 허브
    public static final String QUEUE = "orders.created.q"; // 메시지가 실제로 쌓이는 곳 (Consumer가 여기서 꺼냄)
    public static final String ROUTING_KEY = "orders.created"; // 메시지를 특정 큐로 보내는 기준이 되는 문자열

    // Exchange 정의 (메시지 라우팅 허브)
    @Bean
    TopicExchange ordersExchange() {
        return new TopicExchange(EXCHANGE, true, false); // TopicExchange: 라우팅 키 패턴에 따라 큐로 보냄
        // durable(true): 브로커 재시작 시 남길지 결정
        // autoDelete(false): 자동 삭제 결정
        // => "orders.exchange"라는 내구성 있는 토픽 익스체인지 생성
    }

    // Queue 정의 (메시지 저장소)
    @Bean
    Queue ordersCreatedQueue() {
        return QueueBuilder.durable(QUEUE).build(); // QueueBuilder: 옵션을 쉽게 붙이기 위한 빌더 (TTL, DLQ 등 가능)
        // durable(Queue): 서버 재시작 후에도 유지
        // => "orders.created.q"라는 지속성 큐 생성
    }

    // Binding (Exchange <-> Queue 연결)
    @Bean
    Binding ordersBinding() {
        // Binding: 메시지를 어떤 규칙으로 라우팅할지 연결하는 설정
        return BindingBuilder.bind(ordersCreatedQueue())// "orders.exchange"를
                .to(ordersExchange()) // "orders.created.q"로
                .with(ROUTING_KEY); // 라우팅 키 "orders.created"일 때 전달
    }

    // 메시지 변환기 (JSON 직렬화)
    @Bean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
        // RabbitTemplate은 byte 배열로 메시지를 주고 받는데, 이 설정 덕분에 객체를 그대로 송신/수신 가능해진다.
    }

    // RabbitTemplate 설정 (메시지 송신 담당)
    @Bean
    RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory cf) {
        // ConnectionFactory: RabbitMQ 정보를 관리하는 Bean
        RabbitTemplate template = new RabbitTemplate(cf); // RabbitTemplate: 메시지를 보내는 핵심 컴포넌트 (Producer에서 사용)
        template.setMessageConverter(jackson2JsonMessageConverter());
        template.setMandatory(true); // unroutable message return 콜백 활성화 (메시지가 어느 큐에도 라우팅되지 못했을 때 알림을 받도록 설정. 없으면 그냥 유실됨)
        return template;
        // => 보낼 때 자동으로 JSON 변환 + 라우팅 실패 감지 가능
    }

    // 퍼블리셔 confirm 데이터 설정
    @Bean
    CorrelationDataPostProcessor correlationDataPostProcessor() {
        return (message, correlationData) -> correlationData;
        // Confirm Callback을 사용할 때, 메시지 전송 시 함께 넘기는 CorrelationData 객체를 조작할 수 있게 해줌. 여기선 특별한 변경 없이 그대로 반환.
        // 추후 RabbitTemplate의 ConfirmCallback을 등록해서 "메시지가 브로커까지 도달했는가?" 확인할 때 사용 가능.
    }


    // 전체 동작 흐름
    // 1. Producer: OrderCreatedEvent 전송
    // 2. Exchange: 라우팅 키 매핑
    // 3. Queue
    // 4. Consumer
}
