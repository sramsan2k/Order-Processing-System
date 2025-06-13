package com.example.kafka;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Component
public class KafkaBroker {

    private final Map<KafkaTopic, BlockingQueue<KafkaMessage>> topicQueues = new ConcurrentHashMap<>();
    private final Map<KafkaTopic, List<Consumer<KafkaMessage>>> subscribers = new ConcurrentHashMap<>();

    public KafkaBroker() {
        for (KafkaTopic topic : KafkaTopic.values()) {
            topicQueues.put(topic, new LinkedBlockingQueue<>());
            subscribers.put(topic, new CopyOnWriteArrayList<>());
            startConsumerThread(topic);
        }
    }

    private void startConsumerThread(KafkaTopic topic) {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    KafkaMessage message = topicQueues.get(topic).take(); // blocking
                    System.out.println("🧵 Kafka delivering: " + topic + " -> " + message.getKey() + "/" + message.getPayload());
                    for (Consumer<KafkaMessage> consumer : subscribers.get(topic)) {
                        consumer.accept(message);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        thread.setDaemon(true);
        thread.setName("KafkaConsumer-" + topic.name());
        thread.start();
    }

    public void publish(KafkaMessage message) {
        System.out.println("📤 Kafka published: " + message.getTopic() + " -> " + message.getKey() + "/" + message.getPayload());
        topicQueues.get(message.getTopic()).offer(message);
    }

    public void subscribe(KafkaTopic topic, Consumer<KafkaMessage> consumer) {
        System.out.println("🔔 Subscribed to topic: " + topic);
        subscribers.get(topic).add(consumer);
    }

    public BlockingQueue<KafkaMessage> getQueue(KafkaTopic topic) {
        return topicQueues.get(topic);
    }
}
