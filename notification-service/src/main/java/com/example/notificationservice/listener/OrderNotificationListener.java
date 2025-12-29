package com.example.notificationservice.listener;

import com.example.notificationservice.dto.OrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderNotificationListener {

    @Value("${mq.order.topic.exchange}")
    private String topicExchange;

    // 1. АСТАНА – тек Астана
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "astana_orders_queue"),
            exchange = @Exchange(value = "${mq.order.topic.exchange}", type = ExchangeTypes.TOPIC),
            key = "order.astana"
    ))
    public void receiveAstanaOrder(OrderDTO order) {
        log.info("=== ASTANA ORDER ===");
        log.info("Restaurant: {}", order.getRestaurant());
        log.info("Courier: {}", order.getCourier());
        log.info("Foods: {}", order.getFoods());
        log.info("Status: {}", order.getStatus());
        log.info("==================================");
    }

    // 2. АЛМАТЫ – тек Алматы
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "almaty_orders_queue"),
            exchange = @Exchange(value = "${mq.order.topic.exchange}", type = ExchangeTypes.TOPIC),
            key = "order.almaty"
    ))
    public void receiveAlmatyOrder(OrderDTO order) {
        log.info("=== ALMATY ORDER ===");
        log.info("Restaurant: {}", order.getRestaurant());
        log.info("Courier: {}", order.getCourier());
        log.info("Foods: {}", order.getFoods());
        log.info("Status: {}", order.getStatus());
        log.info("==================================");
    }

    // 3. ҚАРАҒАНДЫ → Астана + Алматы кезектеріне
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "astana_orders_queue"),
            exchange = @Exchange(value = "${mq.order.topic.exchange}", type = ExchangeTypes.TOPIC),
            key = "order.karag"
    ))
    public void receiveKaragInAstana(OrderDTO order) {
        log.info("=== KARAGANDA ORDER IN ASTANA QUEUE ===");
        log.info("Restaurant: {}", order.getRestaurant());
        log.info("==================================");
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "almaty_orders_queue"),
            exchange = @Exchange(value = "${mq.order.topic.exchange}", type = ExchangeTypes.TOPIC),
            key = "order.karag"
    ))
    public void receiveKaragInAlmaty(OrderDTO order) {
        log.info("=== KARAGANDA ORDER IN ALMATY QUEUE ===");
        log.info("Restaurant: {}", order.getRestaurant());
        log.info("==================================");
    }

    // 4. БАРЛЫҚ БАСҚА ҚАЛАЛАР → all_orders_queue
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "all_orders_queue"),
            exchange = @Exchange(value = "${mq.order.topic.exchange}", type = ExchangeTypes.TOPIC),
            key = "order.#"
    ))
    public void receiveAllOtherOrders(OrderDTO order) {
        String region = extractRegion(order); // мысалы: shymkent, pavlodar
        if (!region.equals("astana") && !region.equals("almaty") && !region.equals("karag")) {
            log.info("=== OTHER CITY ORDER (in ALL queue) ===");
            log.info("Region: {}", region);
            log.info("Restaurant: {}", order.getRestaurant());
            log.info("=================================");
        }
    }

    // Қосымша: region анықтау үшін (OrderPublisher-де region беріледі)
    private String extractRegion(OrderDTO order) {
        // Егер OrderDTO-ға region қосылмаса, routing key-ден анықтау керек
        // Бұл жерде қарапайым мысал:
        return "unknown"; // немесе @Header("amqp_receivedRoutingKey") арқылы
    }
}