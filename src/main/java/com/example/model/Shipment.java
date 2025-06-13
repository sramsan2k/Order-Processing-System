package com.example.model;

import java.time.LocalDateTime;

public class Shipment {
    private String shipmentId;
    private String orderId;
    private String status; // e.g., CREATED, DISPATCHED, IN_TRANSIT, DELIVERED
    private String trackingNumber;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    private Shipment(Builder builder) {
        this.shipmentId = builder.shipmentId;
        this.orderId = builder.orderId;
        this.status = builder.status;
        this.trackingNumber = builder.trackingNumber;
        this.shippedAt = builder.shippedAt;
        this.deliveredAt = builder.deliveredAt;
    }

    public String getShipmentId() { return shipmentId; }
    public String getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public String getTrackingNumber() { return trackingNumber; }
    public LocalDateTime getShippedAt() { return shippedAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }

    public void setStatus(String status) { this.status = status; }
    public void setShippedAt(LocalDateTime shippedAt) { this.shippedAt = shippedAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    public static class Builder {
        private String shipmentId;
        private String orderId;
        private String status;
        private String trackingNumber;
        private LocalDateTime shippedAt;
        private LocalDateTime deliveredAt;

        public Builder shipmentId(String shipmentId) { this.shipmentId = shipmentId; return this; }
        public Builder orderId(String orderId) { this.orderId = orderId; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder trackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; return this; }
        public Builder shippedAt(LocalDateTime shippedAt) { this.shippedAt = shippedAt; return this; }
        public Builder deliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; return this; }

        public Shipment build() {
            return new Shipment(this);
        }
    }
}
