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

class OrderServiceImplTest {

    private OrderRepository orderRepository;
    private KafkaBroker kafkaBroker;
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        kafkaBroker = mock(KafkaBroker.class);
        orderService = new OrderServiceImpl(orderRepository, kafkaBroker);
    }

    @Test
    void testCreateOrder_PublishesKafkaEventAndSaves() {
        Order order = new Order.Builder()
                .orderId("o1")
                .userId("u1")
                .status("PENDING")
                .items(List.of())
                .build();

        orderService.createOrder(order);

        verify(orderRepository).save(order);

        ArgumentCaptor<KafkaMessage> kafkaCaptor = ArgumentCaptor.forClass(KafkaMessage.class);
        verify(kafkaBroker).publish(kafkaCaptor.capture());

        KafkaMessage message = kafkaCaptor.getValue();
        assertEquals(KafkaTopic.ORDER_PLACED, message.getTopic());
        assertEquals("o1", message.getKey());
        assertEquals("ORDER_PLACED", message.getPayload());
    }

    @Test
    void testGetOrderById() {
        Order order = new Order.Builder().orderId("o2").userId("u2").status("PENDING").items(List.of()).build();
        when(orderRepository.findById("o2")).thenReturn(order);

        Order result = orderService.getOrder("o2");

        assertNotNull(result);
        assertEquals("u2", result.getUserId());
    }

    @Test
    void testGetOrdersByUser() {
        Order order = new Order.Builder().orderId("o3").userId("u3").status("PENDING").items(List.of()).build();
        when(orderRepository.getOrdersByUser("u3")).thenReturn(List.of(order));

        List<Order> orders = orderService.getOrdersByUser("u3");

        assertEquals(1, orders.size());
        assertEquals("u3", orders.get(0).getUserId());
    }

    @Test
    void testCancelOrder_Success() {
        Order order = new Order.Builder().orderId("o4").userId("u4").status("PENDING").items(List.of()).build();
        when(orderRepository.findById("o4")).thenReturn(order);

        orderService.cancelOrder("o4");

        assertEquals("CANCELLED", order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testCancelOrder_InvalidState_ThrowsException() {
        Order order = new Order.Builder().orderId("o5").userId("u5").status("SHIPPED").items(List.of()).build();
        when(orderRepository.findById("o5")).thenReturn(order);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderService.cancelOrder("o5"));
        assertEquals("Only PENDING orders can be cancelled.", ex.getMessage());

        verify(orderRepository, never()).save(any());
    }

    @Test
    void testUpdateOrderStatus() {
        Order order = new Order.Builder().orderId("o6").userId("u6").status("PENDING").items(List.of()).build();
        when(orderRepository.findById("o6")).thenReturn(order);

        orderService.updateOrderStatus("o6", "PROCESSING");

        assertEquals("PROCESSING", order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testGetOrdersByStatus() {
        Order order = new Order.Builder().orderId("o7").userId("u7").status("SHIPPED").items(List.of()).build();
        when(orderRepository.getOrdersByStatus("SHIPPED")).thenReturn(List.of(order));

        List<Order> result = orderService.getOrdersByStatus("SHIPPED");

        assertEquals(1, result.size());
        assertEquals("SHIPPED", result.get(0).getStatus());
    }
}
