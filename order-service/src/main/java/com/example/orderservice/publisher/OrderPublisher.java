package com.example.orderservice.publisher;

import com.example.orderservice.dto.OrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${mq.order.topic.exchange}")
    private String topicExchange;

    public void sendOrderToPrepare(OrderDTO order, String region) {
        String routingKey = "order." + region;
        log.info("Sending order to region: {} with routing key: {}", region, routingKey);
        rabbitTemplate.convertAndSend(topicExchange, routingKey, order);
    }
}


