// File: com.example.kafka.KafkaMessage.java
package com.example.kafka;

public class KafkaMessage {
    private final KafkaTopic topic;
    private final String key;
    private final String payload;

    public KafkaMessage(KafkaTopic topic, String key, String payload) {
        this.topic = topic;
        this.key = key;
        this.payload = payload;
    }

    public KafkaTopic getTopic() { return topic; }
    public String getKey() { return key; }
    public String getPayload() { return payload; }
}
