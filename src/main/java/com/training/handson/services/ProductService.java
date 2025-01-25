package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.CartResourceIdentifierBuilder;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.common.AddressBuilder;
import com.commercetools.api.models.customer.*;
import com.commercetools.api.models.customer_group.CustomerGroup;
import com.commercetools.api.models.product.ProductProjection;
import com.commercetools.api.models.product_search.*;
import com.commercetools.api.models.search.*;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.Console;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductService {

    @Autowired
    private ProjectApiRoot apiRoot;

    public CompletableFuture<ResponseEntity<ProductPagedSearchResponse>> getProducts(
            String keyword,
            String storeKey,
            Boolean includeFacets) {

        ProductSearchRequestBuilder builder = ProductSearchRequestBuilder.of();

        return apiRoot
                .products()
                .search()
                .post(builder.build())
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle(this::handleResponse);
    }

    public CompletableFuture<ResponseEntity<ProductProjection>> getProductByKey(String productKey) {
        return apiRoot
                .productProjections()
                .withKey(productKey)
                .get()
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle(this::handleResponse);
    }

    private <T> ResponseEntity<T> handleResponse(T body, Throwable throwable) {
        if (throwable != null) {
            logError(throwable);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } else {
            if (body == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(body);
        }
    }

    private void logError(Throwable throwable) {
        System.err.println("Error occurred: " + throwable.getMessage());
    }

}
