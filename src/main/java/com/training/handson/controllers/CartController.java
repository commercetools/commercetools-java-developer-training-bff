package com.training.handson.controllers;

import com.commercetools.api.models.cart.Cart;
import com.training.handson.dto.LineItemRequest;
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
            @RequestBody LineItemRequest lineItemRequest) {

        String cartId = lineItemRequest.getCartId();
        String sku = lineItemRequest.getSku();
        Long quantity = lineItemRequest.getQuantity();
        String supplyChannel = lineItemRequest.getSupplyChannel();
        String distributionChannel = lineItemRequest.getDistributionChannel();

        if(cartId == null || cartId.isEmpty()){
            return cartService.createAnonymousCart(
                    sku,
                    quantity
//                supplyChannel,
//                distributionChannel
            );
        }
        else {
            return cartService.addProductToCartBySkusAndChannel(
                    cartId,
                    sku,
                    quantity
//                supplyChannel,
//                distributionChannel
            );
        }

    }
}
