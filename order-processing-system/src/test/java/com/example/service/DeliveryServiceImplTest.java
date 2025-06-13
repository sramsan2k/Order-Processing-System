package com.example.service;

import com.example.kafka.KafkaBroker;
import com.example.kafka.KafkaMessage;
import com.example.kafka.KafkaTopic;
import com.example.model.Order;
import com.example.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryServiceImplTest {

    private OrderRepository orderRepository;
    private KafkaBroker kafkaBroker;
    private DeliveryServiceImpl deliveryService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        kafkaBroker = mock(KafkaBroker.class);
        deliveryService = new DeliveryServiceImpl(orderRepository, kafkaBroker);
    }

    @Test
    void testDeliverOrder_Success() {
        Order order = new Order.Builder()
                .orderId("order123")
                .userId("user1")
                .status("SHIPPED")
                .items(List.of())
                .build();

        when(orderRepository.findById("order123")).thenReturn(order);

        deliveryService.deliverOrder("order123");

        // assert status update
        assertEquals("DELIVERED", order.getStatus());
        verify(orderRepository).save(order);

        // assert Kafka message
        ArgumentCaptor<KafkaMessage> kafkaCaptor = ArgumentCaptor.forClass(KafkaMessage.class);
        verify(kafkaBroker).publish(kafkaCaptor.capture());

        KafkaMessage msg = kafkaCaptor.getValue();
        assertEquals(KafkaTopic.ORDER_DELIVERED, msg.getTopic());
        assertEquals("order123", msg.getKey());
        assertEquals("ORDER_DELIVERED", msg.getPayload());
    }

    @Test
    void testDeliverOrder_Failure_InvalidStatus() {
        Order order = new Order.Builder()
                .orderId("order456")
                .userId("user2")
                .status("PENDING")
                .items(List.of())
                .build();

        when(orderRepository.findById("order456")).thenReturn(order);

        deliveryService.deliverOrder("order456");

        verify(orderRepository, never()).save(any());
        verify(kafkaBroker, never()).publish(any());
    }

    @Test
    void testDeliverOrder_Failure_OrderNotFound() {
        when(orderRepository.findById("unknown")).thenReturn(null);

        deliveryService.deliverOrder("unknown");

        verify(orderRepository, never()).save(any());
        verify(kafkaBroker, never()).publish(any());
    }
}
