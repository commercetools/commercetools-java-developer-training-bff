package com.training.handson.dto;

public class UpdateCartRequest {

    private String cartId;
    private String sku;
    private Long quantity;
    private String supplyChannel;
    private String distributionChannel;
    private String code;

    public String getCartId() { return cartId; }

    public void setCartId(String cartId) { this.cartId = cartId; }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getSupplyChannel() {
        return supplyChannel;
    }

    public void setSupplyChannel(String supplyChannel) {
        this.supplyChannel = supplyChannel;
    }

    public String getDistributionChannel() {
        return distributionChannel;
    }

    public void setDistributionChannel(String distributionChannel) {
        this.distributionChannel = distributionChannel;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
