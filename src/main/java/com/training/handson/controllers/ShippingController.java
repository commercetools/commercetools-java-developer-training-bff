package com.training.handson.controllers;

import com.commercetools.api.models.shipping_method.ShippingMethod;
import com.training.handson.services.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/shippingmethods")
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @GetMapping("/")
    public CompletableFuture<ResponseEntity<List<ShippingMethod>>> getShippingMethods() {
        return shippingService.getShippingMethods();
    }

    @GetMapping("/{key}")
    public CompletableFuture<ResponseEntity<ShippingMethod>> getShippingMethodByKey(
            @PathVariable String key) {
        return shippingService.getShippingMethodByKey(key);
    }

    @GetMapping("/exists/{key}")
    public CompletableFuture<ResponseEntity<Boolean>> checkShippingMethodExistence(@PathVariable String key) {
        return shippingService.checkShippingMethodExistence(key);
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<ShippingMethod>>> getShippingMethods(
            @RequestParam("countryCode") String countryCode) {
        return shippingService.getShippingMethodsByCountry(countryCode);
    }
}
