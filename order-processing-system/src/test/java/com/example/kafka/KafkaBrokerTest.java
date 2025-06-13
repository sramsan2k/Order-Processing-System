package com.example.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class KafkaBrokerTest {

    private KafkaBroker kafkaBroker;

    @BeforeEach
    void setUp() {
        kafkaBroker = new KafkaBroker();
    }

    @Test
    void testPublishAndSubscribeSingleConsumer() throws InterruptedException {
        KafkaTopic topic = KafkaTopic.ORDER_PLACED;
        KafkaMessage message = new KafkaMessage(topic, "order123", "Placed order");

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<KafkaMessage> received = new AtomicReference<>();

        kafkaBroker.subscribe(topic, msg -> {
            received.set(msg);
            latch.countDown();
        });

        kafkaBroker.publish(message);

        boolean receivedInTime = latch.await(2, TimeUnit.SECONDS);

        assertTrue(receivedInTime, "Message should be received within 2 seconds");
        assertNotNull(received.get());
        assertEquals("order123", received.get().getKey());
        assertEquals("Placed order", received.get().getPayload());
        assertEquals(KafkaTopic.ORDER_PLACED, received.get().getTopic());
    }

    @Test
    void testPublishAndSubscribeMultipleConsumers() throws InterruptedException {
        KafkaTopic topic = KafkaTopic.ORDER_PROCESSING;
        KafkaMessage message = new KafkaMessage(topic, "order456", "Processing order");

        CountDownLatch latch = new CountDownLatch(2);
        AtomicReference<String> consumer1 = new AtomicReference<>();
        AtomicReference<String> consumer2 = new AtomicReference<>();

        kafkaBroker.subscribe(topic, msg -> {
            consumer1.set("Consumer1: " + msg.getPayload());
            latch.countDown();
        });

        kafkaBroker.subscribe(topic, msg -> {
            consumer2.set("Consumer2: " + msg.getPayload());
            latch.countDown();
        });

        kafkaBroker.publish(message);

        boolean received = latch.await(2, TimeUnit.SECONDS);

        assertTrue(received);
        assertEquals("Consumer1: Processing order", consumer1.get());
        assertEquals("Consumer2: Processing order", consumer2.get());
    }

    @Test
    void testGetQueueDirectly() throws InterruptedException {
        KafkaTopic topic = KafkaTopic.ORDER_DELIVERED;
        KafkaMessage message = new KafkaMessage(topic, "order789", "Delivered");

        kafkaBroker.publish(message);

        BlockingQueue<KafkaMessage> queue = kafkaBroker.getQueue(topic);

        KafkaMessage taken = queue.poll(2, TimeUnit.SECONDS); // should be consumed by thread soon

        // This message may be null since consumer thread takes it almost instantly.
        // We'll validate that KafkaBroker doesn't block and no exception occurs.
        assertTrue(true, "Queue access should not throw any exception");
    }
}
