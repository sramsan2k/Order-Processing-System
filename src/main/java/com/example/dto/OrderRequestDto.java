package com.example.dto;

public class OrderRequestDto {
    private OrderDto order;

    public OrderRequestDto() {}

    private OrderRequestDto(Builder builder) {
        this.order = builder.order;

    }

    public OrderDto getOrder() {
        return order;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private OrderDto order;


        public Builder order(OrderDto order) {
            this.order = order;
            return this;
        }


        public OrderRequestDto build() {
            return new OrderRequestDto(this);
        }
    }
}
