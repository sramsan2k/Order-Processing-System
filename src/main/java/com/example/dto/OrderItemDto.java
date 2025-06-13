package com.example.dto;

public class OrderItemDto {
    private String itemId;
    private int quantity;

    public OrderItemDto() {}

    private OrderItemDto(Builder builder) {
        this.itemId = builder.itemId;
        this.quantity = builder.quantity;
    }

    public String getItemId() { return itemId; }
    public int getQuantity() { return quantity; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String itemId;
        private int quantity;

        public Builder itemId(String itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public OrderItemDto build() {
            return new OrderItemDto(this);
        }
    }
}
