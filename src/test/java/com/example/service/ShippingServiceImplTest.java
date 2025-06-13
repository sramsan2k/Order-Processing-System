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

class ShippingServiceImplTest {

    private OrderRepository orderRepository;
    private KafkaBroker kafkaBroker;
    private ShippingServiceImpl shippingService;

    @BeforeEach
    void setUp() {
        // Create mocks for dependencies
        orderRepository = mock(OrderRepository.class);
        kafkaBroker = mock(KafkaBroker.class);

        // Instantiate the service with the mocked dependencies.
        shippingService = new ShippingServiceImpl(orderRepository, kafkaBroker);

        // Optionally, you can call the @PostConstruct method if needed:
        // shippingService.initKafkaConsumer();
    }

    @Test
    void testShipOrder_Success() {
        // Arrange: Prepare an order in "PROCESSING" state.
        Order order = new Order.Builder()
                .orderId("order1")
                .userId("user1")
                .status("PROCESSING")
                .items(List.of())
                .build();

        when(orderRepository.findById("order1")).thenReturn(order);

        // Act: Trigger the shipping manually via shipOrder().
        shippingService.shipOrder("order1");

        // Assert: Order status should be updated to "SHIPPED" and saved.
        assertEquals("SHIPPED", order.getStatus());
        verify(orderRepository).save(order);

        // And a Kafka message with the correct topic, key, and payload should be published.
        ArgumentCaptor<KafkaMessage> kafkaCaptor = ArgumentCaptor.forClass(KafkaMessage.class);
        verify(kafkaBroker).publish(kafkaCaptor.capture());
        KafkaMessage publishedMessage = kafkaCaptor.getValue();
        assertEquals(KafkaTopic.ORDER_SHIPPED, publishedMessage.getTopic());
        assertEquals("order1", publishedMessage.getKey());
        assertEquals("ORDER_SHIPPED", publishedMessage.getPayload());
    }

    @Test
    void testShipOrder_Failure_OrderNotFound() {
        // Arrange: No order exists with the given ID.
        when(orderRepository.findById("order2")).thenReturn(null);

        // Act: Attempt to ship a non-existent order.
        shippingService.shipOrder("order2");

        // Assert: Verify that save and publish are never called.
        verify(orderRepository, never()).save(any(Order.class));
        verify(kafkaBroker, never()).publish(any(KafkaMessage.class));
    }

    @Test
    void testShipOrder_Failure_OrderNotProcessing() {
        // Arrange: Prepare an order not in the "PROCESSING" state.
        Order order = new Order.Builder()
                .orderId("order3")
                .userId("user3")
                .status("PENDING")
                .items(List.of())
                .build();
        when(orderRepository.findById("order3")).thenReturn(order);

        // Act: Attempt shipping an order that is not in the processing state.
        shippingService.shipOrder("order3");

        // Assert: The order's status is not updated,
        // and neither save nor Kafka publish is invoked.
        verify(orderRepository, never()).save(any(Order.class));
        verify(kafkaBroker, never()).publish(any(KafkaMessage.class));
    }
}
