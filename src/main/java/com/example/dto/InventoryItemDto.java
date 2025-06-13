package com.example.dto;

public class InventoryItemDto {
    private String itemId;
    private String itemName;
    private int availableQty;

    public InventoryItemDto() {}  // <--- Add this no-arg constructor

    private InventoryItemDto(Builder builder) {
        this.itemId = builder.itemId;
        this.itemName = builder.itemName;
        this.availableQty = builder.availableQty;
    }

    // Add these setters for Jackson to populate fields
    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setAvailableQty(int availableQty) { this.availableQty = availableQty; }

    public String getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public int getAvailableQty() { return availableQty; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String itemId;
        private String itemName;
        private int availableQty;

        public Builder itemId(String itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder itemName(String itemName) {
            this.itemName = itemName;
            return this;
        }

        public Builder availableQty(int availableQty) {
            this.availableQty = availableQty;
            return this;
        }

        public InventoryItemDto build() {
            return new InventoryItemDto(this);
        }
    }
}

