package com.training.handson.controllers;

import com.commercetools.api.models.product_search.ProductPagedSearchResponse;
import com.training.handson.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{keyword}")
    public CompletableFuture<ResponseEntity<ProductPagedSearchResponse>> getProducts(@PathVariable String keyword) {
        return productService.getProducts(keyword);
    }
}
