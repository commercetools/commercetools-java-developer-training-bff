package com.training.handson.controllers;

import com.commercetools.api.models.cart.Cart;
import com.training.handson.dto.CartCreateRequest;
import com.training.handson.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{cartId}")
    public CompletableFuture<ResponseEntity<Cart>> getCart(@PathVariable String cartId) {
        return cartService.getCartById(cartId);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<Cart>> createCart(
            @RequestBody CartCreateRequest cartCreateRequest) {

        String sku = cartCreateRequest.getSku();
        Long quantity = cartCreateRequest.getQuantity();
        String supplyChannel = cartCreateRequest.getSupplyChannel();
        String distributionChannel = cartCreateRequest.getDistributionChannel();

        return cartService.createAnonymousCart(
                sku,
                quantity
//                supplyChannel,
//                distributionChannel
        );
    }
}
