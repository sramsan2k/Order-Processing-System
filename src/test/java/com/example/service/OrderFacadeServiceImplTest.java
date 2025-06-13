package com.example.service;

import com.example.dto.OrderDto;
import com.example.dto.OrderItemDto;
import com.example.dto.UserDto;
import com.example.model.Order;
import com.example.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderFacadeServiceImplTest {

    private OrderService orderService;
    private InventoryService inventoryService;
    private UserService userService;
    private OrderFacadeServiceImpl orderFacade;

    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);
        inventoryService = mock(InventoryService.class);
        userService = mock(UserService.class);
        orderFacade = new OrderFacadeServiceImpl(orderService, inventoryService, userService);
    }

    @Test
    void testPlaceOrderSuccess() {
        OrderItemDto itemDto = OrderItemDto.builder()
                .itemId("item1")
                .quantity(2)
                .build();

        OrderDto orderDto = OrderDto.builder()
                .userId("user1")
                .status("PENDING")
                .items(List.of(itemDto))
                .build();

        when(userService.userExists("user1")).thenReturn(true);
        when(inventoryService.reserveStock(orderDto.getItems())).thenReturn(true);

        orderFacade.placeOrder(orderDto);

        verify(orderService).createOrder(any(Order.class));
    }

    @Test
    void testPlaceOrderFails_UserNotFound() {
        OrderDto orderDto = OrderDto.builder()
                .userId("invalidUser")
                .status("PENDING")
                .items(List.of())
                .build();

        when(userService.userExists("invalidUser")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> orderFacade.placeOrder(orderDto));

        assertEquals("User does not exist", ex.getMessage());
        verify(orderService, never()).createOrder(any());
    }

    @Test
    void testPlaceOrderFails_StockUnavailable() {
        OrderItemDto itemDto = OrderItemDto.builder().itemId("item1").quantity(1).build();
        OrderDto orderDto = OrderDto.builder().userId("user2").items(List.of(itemDto)).status("PENDING").build();

        when(userService.userExists("user2")).thenReturn(true);
        when(inventoryService.reserveStock(orderDto.getItems())).thenReturn(false);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> orderFacade.placeOrder(orderDto));

        assertEquals("Stock unavailable", ex.getMessage());
        verify(orderService, never()).createOrder(any());
    }

    @Test
    void testCancelOrder() {
        orderFacade.cancelOrder("order123");
        verify(orderService).cancelOrder("order123");
    }

    @Test
    void testGetOrder() {
        Order mockOrder = new Order.Builder()
                .orderId("o1")
                .userId("u1")
                .status("PENDING")
                .items(List.of(new OrderItem("item1", 1)))
                .build();

        when(orderService.getOrder("o1")).thenReturn(mockOrder);

        Order result = orderFacade.getOrder("o1");

        assertNotNull(result);
        assertEquals("u1", result.getUserId());
    }

    @Test
    void testGetUserOrders() {
        Order mockOrder = new Order.Builder()
                .orderId("o2")
                .userId("u2")
                .status("PENDING")
                .items(List.of())
                .build();

        when(orderService.getOrdersByUser("u2")).thenReturn(List.of(mockOrder));

        List<Order> orders = orderFacade.getUserOrders("u2");

        assertEquals(1, orders.size());
        assertEquals("u2", orders.get(0).getUserId());
    }

    @Test
    void testRegisterUser() {
        UserDto userDto = UserDto.builder()
                .userId("u1")
                .name("Test")
                .email("t@example.com")
                .build();

        orderFacade.registerUser(userDto);
        verify(userService).registerUser(userDto);
    }

    @Test
    void testGetUserById() {
        UserDto userDto = UserDto.builder()
                .userId("u1")
                .name("Test")
                .email("t@example.com")
                .build();

        when(userService.getUserById("u1")).thenReturn(userDto);

        UserDto result = orderFacade.getUserById("u1");

        assertEquals("Test", result.getName());
        assertEquals("t@example.com", result.getEmail());
    }
}
