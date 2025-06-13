package com.example.service;

import com.example.kafka.KafkaBroker;
import com.example.kafka.KafkaMessage;
import com.example.kafka.KafkaTopic;
import com.example.model.Order;
import com.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    private final OrderRepository orderRepository;
    private final KafkaBroker kafkaBroker;

    @Autowired
    public DeliveryServiceImpl(OrderRepository orderRepository, KafkaBroker kafkaBroker) {
        this.orderRepository = orderRepository;
        this.kafkaBroker = kafkaBroker;
    }

    /**
     * Subscribes to ORDER_SHIPPED events from Kafka and initiates delivery.
     */
    @PostConstruct
    public void initKafkaConsumer() {
        kafkaBroker.subscribe(KafkaTopic.ORDER_SHIPPED, message -> {
            String orderId = message.getKey();
            String event = message.getPayload();

            if ("ORDER_SHIPPED".equals(event)) {
                deliverOrder(orderId);
            }
        });

        System.out.println("📦 DeliveryService subscribed to ORDER_SHIPPED topic");
    }

    /**
     * Delivers an order and publishes ORDER_DELIVERED event.
     */
    @Override
    public void deliverOrder(String orderId) {
        Order order = orderRepository.findById(orderId);
        if (order != null && "SHIPPED".equals(order.getStatus())) {
            order.setStatus("DELIVERED");
            orderRepository.save(order);
            System.out.println("✅ Order delivered: " + orderId);

            // Publish final delivery event
            kafkaBroker.publish(new KafkaMessage(
                    KafkaTopic.ORDER_DELIVERED,
                    orderId,
                    "ORDER_DELIVERED"
            ));
        } else {
            System.out.println("❌ Delivery skipped. Order not in SHIPPED state: " + orderId);
        }
    }
}
