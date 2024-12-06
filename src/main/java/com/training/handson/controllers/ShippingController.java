package com.training.handson.controllers;

import com.commercetools.api.models.product.ProductProjection;
import com.commercetools.api.models.product_search.ProductPagedSearchResponse;
import com.commercetools.api.models.shipping_method.ShippingMethod;
import com.training.handson.services.ProductService;
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

    @Autowired
    private ShippingService shippingService;

    @GetMapping("/")
    public CompletableFuture<ResponseEntity<ShippingMethod[]>> getShippingMethods() {
        return shippingService.getShippingMethods();
    }

    @GetMapping("/{key}")
    public CompletableFuture<ResponseEntity<ShippingMethod>> getShippingMethodByKey(
            @PathVariable String key) {
        return shippingService.getShippingMethodByKey(key);
    }

    @GetMapping("/exists/{key}")
    public CompletableFuture<ResponseEntity<ShippingMethod[]>> checkShippingMethodExistence(@PathVariable String key) {
        return shippingService.checkShippingMethodExistence(key)
                .thenApply(statusCode -> {
                    if (statusCode == HttpStatus.OK.value()) {
                        return ResponseEntity.ok(new ShippingMethod[0]);
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ShippingMethod[0]);
                    }
                });
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<ShippingMethod[]>> getShippingMethods(
            @RequestParam("countryCode") String countryCode) {
        return shippingService.getShippingMethodsByCountry(countryCode);
    }
}
