package com.training.handson.controllers;

import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.order.Order;
import com.training.handson.dto.CustomFieldRequest;
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

        return orderService.createOrder(orderRequest);
    }

    @PostMapping("/custom-fields")
    public CompletableFuture<ResponseEntity<Order>> createCustomFields(@RequestBody CustomFieldRequest customFieldRequest) {

            return orderService.setCustomFields(customFieldRequest);

    }
}
