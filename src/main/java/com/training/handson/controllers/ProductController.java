package com.training.handson.controllers;

import com.commercetools.api.models.product.ProductProjection;
import com.commercetools.api.models.product_search.ProductPagedSearchResponse;
import com.training.handson.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<ProductPagedSearchResponse>> getProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "storeKey", required = false) String storeKey,
            @RequestParam(value = "facets", required = false) Boolean includeFacets) {
        return productService.getProducts(keyword, storeKey, includeFacets).thenApply(ResponseConverter::convert);
    }

    @GetMapping("/{key}")
    public CompletableFuture<ResponseEntity<ProductProjection>> getProductByKey(@PathVariable String key) {
        return productService.getProductByKey(key).thenApply(ResponseConverter::convert);
    }
}
