package com.example.config;

/**
 * Centralized place to define Kafka topics used throughout the system.
 */
public class KafkaTopics {

    public static final String ORDER_CREATED = "order.created";
    public static final String ORDER_CANCELLED = "order.cancelled";
    public static final String ORDER_PROCESSING = "order.processing";
    public static final String ORDER_SHIPPED = "order.shipped";
    public static final String ORDER_DELIVERED = "order.delivered";

    public static final String INVENTORY_RESERVED = "inventory.reserved";
    public static final String INVENTORY_FAILED = "inventory.failed";

    public static final String PAYMENT_SUCCESS = "payment.success";
    public static final String PAYMENT_FAILED = "payment.failed";

    public static final String SHIPMENT_ASSIGNED = "shipment.assigned";
    public static final String SHIPMENT_DELIVERED = "shipment.delivered";

    // Add more topics as your system grows
}
