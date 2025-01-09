package com.training.handson.controllers;

import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.order.Order;
import com.training.handson.dto.OrderRequest;
import com.training.handson.dto.UpdateCartRequest;
import com.training.handson.services.CartService;
import com.training.handson.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{orderId}")
    public CompletableFuture<ResponseEntity<Order>> getOrder(@PathVariable String orderId) {
        return orderService.getOrderById(orderId);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<Order>> createOrder(
            @RequestBody OrderRequest orderRequest) {

        String cartId = orderRequest.getCartId();
        Long cartVersion = orderRequest.getCartVersion();
        System.out.println(cartVersion);
        return orderService.createOrder(cartId, cartVersion);
    }
}
