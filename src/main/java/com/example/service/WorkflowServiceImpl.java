package com.example.service;

import com.example.kafka.KafkaBroker;
import com.example.kafka.KafkaMessage;
import com.example.kafka.KafkaTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    private final KafkaBroker kafkaBroker;
    private final BlockingQueue<KafkaMessage> eventBuffer = new LinkedBlockingQueue<>();

    @Autowired
    public WorkflowServiceImpl(KafkaBroker kafkaBroker) {
        this.kafkaBroker = kafkaBroker;
    }

    @PostConstruct
    public void initKafkaSubscription() {
        kafkaBroker.subscribe(KafkaTopic.ORDER_PLACED, message -> {
            if ("ORDER_PLACED".equals(message.getPayload())) {
                eventBuffer.offer(message);
                System.out.println("📩 Workflow buffered order: " + message.getKey());
            }
        });
    }

    /**
     * Periodically polls buffered ORDER_PLACED events and publishes ORDER_PROCESSING events.
     */
    @Override
    @Scheduled(fixedRate = 60000) // every 60 seconds
    public void processPendingOrders() {
        while (!eventBuffer.isEmpty()) {
            KafkaMessage placedEvent = eventBuffer.poll();
            if (placedEvent != null) {
                String orderId = placedEvent.getKey();
                System.out.println("✅ Workflow picked up order: " + orderId + " for processing");

                kafkaBroker.publish(new KafkaMessage(
                        KafkaTopic.ORDER_PROCESSING,
                        orderId,
                        "ORDER_PROCESSING"
                ));
            }
        }
    }
}
