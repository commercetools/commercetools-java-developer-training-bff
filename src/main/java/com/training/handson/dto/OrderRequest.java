package com.training.handson.dto;

public class OrderRequest {

    private String cartId;
    private Long cartVersion;

    public String getCartId() { return cartId; }

    public void setCartId(String cartId) { this.cartId = cartId; }


    public Long getCartVersion() {
        return cartVersion;
    }

    public void setCartVersion(Long cartVersion) {
        this.cartVersion = cartVersion;
    }

}
