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
public class ShippingServiceImpl implements ShippingService {

    private final OrderRepository orderRepository;
    private final KafkaBroker kafkaBroker;

    @Autowired
    public ShippingServiceImpl(OrderRepository orderRepository, KafkaBroker kafkaBroker) {
        this.orderRepository = orderRepository;
        this.kafkaBroker = kafkaBroker;
    }

    /**
     * Subscribes to ORDER_PROCESSING topic and buffers for validation.
     */
    @PostConstruct
    public void initKafkaConsumer() {
        kafkaBroker.subscribe(KafkaTopic.ORDER_PROCESSING, message -> {
            if ("ORDER_PROCESSING".equals(message.getPayload())) {
                String orderId = message.getKey();
                processShipping(orderId);
            }
        });

        System.out.println("✅ ShippingService subscribed to ORDER_PROCESSING topic");
    }

    /**
     * Process and ship order only if it's in PROCESSING state.
     */
    private void processShipping(String orderId) {
        Order order = orderRepository.findById(orderId);
        if (order != null && "PROCESSING".equals(order.getStatus())) {
            order.setStatus("SHIPPED");
            orderRepository.save(order);

            System.out.println("📦 Order shipped: " + orderId);

            kafkaBroker.publish(new KafkaMessage(
                    KafkaTopic.ORDER_SHIPPED,
                    orderId,
                    "ORDER_SHIPPED"
            ));
        } else {
            System.out.println("⚠️ Order not shipped. Invalid or stale state for order: " + orderId);
        }
    }

    /**
     * Manual trigger for shipping.
     */
    @Override
    public void shipOrder(String orderId) {
        processShipping(orderId);
    }
}
