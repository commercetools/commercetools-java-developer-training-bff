package com.training.handson.controllers;

import com.commercetools.api.models.product.Product;
import com.commercetools.api.models.product.ProductProjection;
import com.commercetools.api.models.product_search.ProductPagedSearchResponse;
import com.training.handson.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public CompletableFuture<ResponseEntity<ProductPagedSearchResponse>> getProducts(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "facet", required = false) String facet,
            @RequestParam(value = "value", required = false) String value) {
        return productService.getProducts(keyword);
    }


    @GetMapping("/{key}")
    public CompletableFuture<ResponseEntity<ProductProjection>> getProductByKey(@PathVariable String key) {
        return productService.getProductByKey(key);
    }
}
