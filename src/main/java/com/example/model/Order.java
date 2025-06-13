package com.example.model;

import java.util.List;

public class Order {
    private String orderId;
    private String userId;
    private List<OrderItem> items;
    private String status;

    private Order(Builder builder) {
        this.orderId = builder.orderId;
        this.userId = builder.userId;
        this.items = builder.items;
        this.status = builder.status;
    }

    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public List<OrderItem> getItems() { return items; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public static class Builder {
        private String orderId;
        private String userId;
        private List<OrderItem> items;
        private String status;

        public Builder orderId(String orderId) { this.orderId = orderId; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder items(List<OrderItem> items) { this.items = items; return this; }
        public Builder status(String status) { this.status = status; return this; }

        public Order build() {
            return new Order(this);
        }
    }
}
