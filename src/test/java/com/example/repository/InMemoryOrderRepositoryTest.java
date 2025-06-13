package com.example.repository;

import com.example.model.Order;
import com.example.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryOrderRepositoryTest {

    private InMemoryOrderRepository orderRepository;

    private Order sampleOrder1;
    private Order sampleOrder2;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();

        OrderItem item1 = new OrderItem("item1", 2);
        OrderItem item2 = new OrderItem("item2", 3);

        sampleOrder1 = new Order.Builder()
                .orderId("o1")
                .userId("u1")
                .status("PENDING")
                .items(List.of(item1))
                .build();

        sampleOrder2 = new Order.Builder()
                .orderId("o2")
                .userId("u2")
                .status("SHIPPED")
                .items(List.of(item2))
                .build();
    }

    @Test
    void testSaveAndFindById() {
        orderRepository.save(sampleOrder1);

        Order found = orderRepository.findById("o1");

        assertNotNull(found);
        assertEquals("u1", found.getUserId());
        assertEquals("PENDING", found.getStatus());
        assertEquals(1, found.getItems().size());
        assertEquals("item1", found.getItems().get(0).getItemId());
    }

    @Test
    void testDelete() {
        orderRepository.save(sampleOrder1);
        orderRepository.delete("o1");

        assertNull(orderRepository.findById("o1"));
    }

    @Test
    void testFindAll() {
        orderRepository.save(sampleOrder1);
        orderRepository.save(sampleOrder2);

        List<Order> allOrders = orderRepository.findAll();

        assertEquals(2, allOrders.size());
    }

    @Test
    void testGetOrdersByStatus() {
        orderRepository.save(sampleOrder1);
        orderRepository.save(sampleOrder2);

        List<Order> pendingOrders = orderRepository.getOrdersByStatus("PENDING");

        assertEquals(1, pendingOrders.size());
        assertEquals("o1", pendingOrders.get(0).getOrderId());
    }

    @Test
    void testGetOrdersByUser() {
        orderRepository.save(sampleOrder1);
        orderRepository.save(sampleOrder2);

        List<Order> user1Orders = orderRepository.getOrdersByUser("u1");

        assertEquals(1, user1Orders.size());
        assertEquals("o1", user1Orders.get(0).getOrderId());
    }
}
