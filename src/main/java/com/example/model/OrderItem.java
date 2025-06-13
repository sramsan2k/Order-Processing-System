package com.example.model;

public class OrderItem {
    private String itemId;
    private int quantity;

    public OrderItem(String itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public String getItemId() { return itemId; }
    public int getQuantity() { return quantity; }
}
