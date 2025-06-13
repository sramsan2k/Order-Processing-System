package com.example.model;

public class InventoryItem {
    private String itemId;
    private String itemName;
    private int availableQuantity;

    private InventoryItem(Builder builder) {
        this.itemId = builder.itemId;
        this.itemName = builder.itemName;
        this.availableQuantity = builder.availableQuantity;
    }

    public String getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int quantity) { this.availableQuantity = quantity; }

    public static class Builder {
        private String itemId;
        private String itemName;
        private int availableQuantity;

        public Builder withItemId(String itemId) { this.itemId = itemId; return this; }
        public Builder withItemName(String itemName) { this.itemName = itemName; return this; }
        public Builder withAvailableQuantity(int qty) { this.availableQuantity = qty; return this; }

        public InventoryItem build() {
            return new InventoryItem(this);
        }
    }
}
