package com.example.dto;

import java.util.List;

public class OrderDto {
    private String orderId;
    private String userId;
    private String status;
    private List<OrderItemDto> items;

    public OrderDto() {}

    private OrderDto(Builder builder) {
        this.orderId = builder.orderId;
        this.userId = builder.userId;
        this.status = builder.status;
        this.items = builder.items;
    }

    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public String getStatus() { return status; }
    public List<OrderItemDto> getItems() { return items; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String orderId;
        private String userId;
        private String status;
        private List<OrderItemDto> items;

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder items(List<OrderItemDto> items) {
            this.items = items;
            return this;
        }

        public OrderDto build() {
            return new OrderDto(this);
        }
    }
}
