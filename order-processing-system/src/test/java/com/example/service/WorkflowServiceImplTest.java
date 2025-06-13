package com.example.service;

import com.example.kafka.KafkaBroker;
import com.example.kafka.KafkaMessage;
import com.example.kafka.KafkaTopic;
import com.example.model.Order;
import com.example.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class WorkflowServiceImplTest {

    private KafkaBroker kafkaBroker;
    private OrderRepository orderRepository;
    private WorkflowServiceImpl workflowService;
    private Consumer<KafkaMessage> capturedSubscriber;

    @BeforeEach
    void setUp() {
        kafkaBroker = mock(KafkaBroker.class);
        orderRepository = mock(OrderRepository.class);

        // Capture the subscriber
        doAnswer(invocation -> {
            KafkaTopic topic = invocation.getArgument(0);
            Consumer<KafkaMessage> consumer = invocation.getArgument(1);
            if (topic == KafkaTopic.ORDER_PLACED) {
                capturedSubscriber = consumer;
            }
            return null;
        }).when(kafkaBroker).subscribe(any(), any());

        workflowService = new WorkflowServiceImpl(kafkaBroker, orderRepository);
        workflowService.initKafkaSubscription();
    }

    @Test
    void testWorkflowProcessesPendingOrderAndPublishesProcessingEvent() {
        // Build mock order using Builder pattern
        Order mockOrder = new Order.Builder()
                .orderId("order123")
                .userId("user11")
                .status("PENDING")
                .build();

        when(orderRepository.findById("order123")).thenReturn(mockOrder);

        KafkaMessage placedMessage = new KafkaMessage(KafkaTopic.ORDER_PLACED, "order123", "ORDER_PLACED");
        capturedSubscriber.accept(placedMessage);

        workflowService.processPendingOrders();

        verify(orderRepository).save(mockOrder);
        ArgumentCaptor<KafkaMessage> publishedCaptor = ArgumentCaptor.forClass(KafkaMessage.class);
        verify(kafkaBroker).publish(publishedCaptor.capture());

        KafkaMessage published = publishedCaptor.getValue();
        assertEquals(KafkaTopic.ORDER_PROCESSING, published.getTopic());
        assertEquals("order123", published.getKey());
        assertEquals("ORDER_PROCESSING", published.getPayload());
    }

    @Test
    void testWorkflowSkipsNonPendingOrder() {
        // Build CANCELLED order
        Order mockOrder = new Order.Builder()
                .orderId("order456")
                .userId("user22")
                .status("CANCELLED")
                .build();

        when(orderRepository.findById("order456")).thenReturn(mockOrder);

        KafkaMessage placedMessage = new KafkaMessage(KafkaTopic.ORDER_PLACED, "order456", "ORDER_PLACED");
        capturedSubscriber.accept(placedMessage);

        workflowService.processPendingOrders();

        verify(kafkaBroker, never()).publish(any());
        verify(orderRepository, never()).save(any());
    }
}
