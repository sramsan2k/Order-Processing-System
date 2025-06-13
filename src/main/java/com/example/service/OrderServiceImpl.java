package com.example.service;

import com.example.kafka.KafkaBroker;
import com.example.kafka.KafkaMessage;
import com.example.kafka.KafkaTopic;
import com.example.model.Order;
import com.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final KafkaBroker kafkaBroker;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, KafkaBroker kafkaBroker) {
        this.orderRepository = orderRepository;
        this.kafkaBroker = kafkaBroker;
    }

    /**
     * Subscribes to lifecycle Kafka topics and updates order statuses.
     */
    @PostConstruct
    public void subscribeToKafkaEvents() {
        kafkaBroker.subscribe(KafkaTopic.ORDER_PROCESSING, message -> {
            if ("ORDER_PROCESSING".equals(message.getPayload())) {
                updateOrderStatus(message.getKey(), "PROCESSING");
            }
        });

        kafkaBroker.subscribe(KafkaTopic.ORDER_SHIPPED, message -> {
            if ("ORDER_SHIPPED".equals(message.getPayload())) {
                updateOrderStatus(message.getKey(), "SHIPPED");
            }
        });

        kafkaBroker.subscribe(KafkaTopic.ORDER_DELIVERED, message -> {
            if ("ORDER_DELIVERED".equals(message.getPayload())) {
                updateOrderStatus(message.getKey(), "DELIVERED");
            }
        });

        System.out.println("✅ OrderService Kafka consumers subscribed.");
    }

    @Override
    public void createOrder(Order order) {
        orderRepository.save(order);
        kafkaBroker.publish(new KafkaMessage(
                KafkaTopic.ORDER_PLACED,
                order.getOrderId(),
                "ORDER_PLACED"
        ));
        System.out.println("📦 Order created and ORDER_PLACED event published: " + order.getOrderId());
    }

    @Override
    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<Order> getOrdersByUser(String userId) {
        return orderRepository.getOrdersByUser(userId);
    }

    @Override
    public void cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId);
        if (order != null && "PENDING".equalsIgnoreCase(order.getStatus())) {
            order.setStatus("CANCELLED");
            orderRepository.save(order);
            System.out.println("❌ Order cancelled: " + orderId);
        } else {
            throw new IllegalStateException("Only PENDING orders can be cancelled.");
        }
    }

    @Override
    public void updateOrderStatus(String orderId, String status) {
        Order order = orderRepository.findById(orderId);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
            System.out.println("✅ Order status updated: " + orderId + " -> " + status);
        }
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.getOrdersByStatus(status);
    }
}
