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
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "storeKey", required = false) String storeKey,
            @RequestParam(value = "facets", required = false) Boolean includeFacets) {
        return productService.getProducts(keyword, storeKey, includeFacets);
    }

    @GetMapping("/{key}")
    public CompletableFuture<ResponseEntity<ProductProjection>> getProductByKey(@PathVariable String key) {
        return productService.getProductByKey(key);
    }
}
