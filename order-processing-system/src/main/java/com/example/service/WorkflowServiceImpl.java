package com.example.service;

import com.example.kafka.KafkaBroker;
import com.example.kafka.KafkaMessage;
import com.example.kafka.KafkaTopic;
import com.example.model.Order;
import com.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    private final KafkaBroker kafkaBroker;
    private final OrderRepository orderRepository;
    private final BlockingQueue<KafkaMessage> eventBuffer = new LinkedBlockingQueue<>();

    @Autowired
    public WorkflowServiceImpl(KafkaBroker kafkaBroker, OrderRepository orderRepository) {
        this.kafkaBroker = kafkaBroker;
        this.orderRepository = orderRepository;
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
     * Periodically polls buffered ORDER_PLACED events and publishes ORDER_PROCESSING events,
     * but only if the order is still in PENDING state.
     */
    @Override
    @Scheduled(fixedRate = 60000) // every 60 seconds
    public void processPendingOrders() {
        while (!eventBuffer.isEmpty()) {
            KafkaMessage placedEvent = eventBuffer.poll();
            if (placedEvent != null) {
                String orderId = placedEvent.getKey();
                Order order = orderRepository.findById(orderId);
                if (order != null && "PENDING".equals(order.getStatus())) {
                    order.setStatus("PROCESSING");
                    orderRepository.save(order);

                    kafkaBroker.publish(new KafkaMessage(
                            KafkaTopic.ORDER_PROCESSING,
                            orderId,
                            "ORDER_PROCESSING"
                    ));

                    System.out.println("✅ Workflow transitioned order to PROCESSING: " + orderId);
                } else {
                    System.out.println("⚠️ Skipped order " + orderId + ": Not in PENDING state.");
                }
            }
        }
    }
}
