package com.example.service;

import com.example.kafka.KafkaBroker;
import com.example.kafka.KafkaMessage;
import com.example.kafka.KafkaTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class WorkflowServiceImplTest {

    private KafkaBroker kafkaBroker;
    private WorkflowServiceImpl workflowService;
    private Consumer<KafkaMessage> capturedSubscriber;

    @BeforeEach
    void setUp() {
        kafkaBroker = mock(KafkaBroker.class);

        // Capture the subscriber that would be registered during init
        doAnswer(invocation -> {
            KafkaTopic topic = invocation.getArgument(0);
            Consumer<KafkaMessage> consumer = invocation.getArgument(1);
            if (topic == KafkaTopic.ORDER_PLACED) {
                capturedSubscriber = consumer;
            }
            return null;
        }).when(kafkaBroker).subscribe(any(), any());

        workflowService = new WorkflowServiceImpl(kafkaBroker);
        workflowService.initKafkaSubscription();
    }

    @Test
    void testWorkflowProcessesBufferedOrderAndPublishesProcessingEvent() {
        // Simulate receiving an ORDER_PLACED message via Kafka
        KafkaMessage placedMessage = new KafkaMessage(KafkaTopic.ORDER_PLACED, "order123", "ORDER_PLACED");
        capturedSubscriber.accept(placedMessage); // simulate KafkaBroker delivering the message

        // Now trigger scheduled processing
        workflowService.processPendingOrders();

        // Verify it publishes ORDER_PROCESSING
        ArgumentCaptor<KafkaMessage> publishedCaptor = ArgumentCaptor.forClass(KafkaMessage.class);
        verify(kafkaBroker).publish(publishedCaptor.capture());

        KafkaMessage published = publishedCaptor.getValue();
        assertEquals(KafkaTopic.ORDER_PROCESSING, published.getTopic());
        assertEquals("order123", published.getKey());
        assertEquals("ORDER_PROCESSING", published.getPayload());
    }
}
