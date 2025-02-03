package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.shipping_method.ShippingMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ShippingService {

    @Autowired
    private ProjectApiRoot apiRoot;

    @Autowired
    private String storeKey;

    public CompletableFuture<ResponseEntity<List<ShippingMethod>>> getShippingMethods() {

        // TODO: Return a list of shipping methods
        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Arrays.asList(ShippingMethod.of()))
        );
    }

    public CompletableFuture<ResponseEntity<List<ShippingMethod>>> getShippingMethodsByCountry(String countryCode) {

        // TODO: Return a list of shipping methods valid for a country
        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Arrays.asList(ShippingMethod.of()))
        );
    }

    public CompletableFuture<ResponseEntity<Boolean>> checkShippingMethodExistence(String key) {

        // TODO: Return true if a shipping method by key exists
        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Boolean.TRUE)
        );
    }

    public CompletableFuture<ResponseEntity<ShippingMethod>> getShippingMethodByKey(String key) {
        return apiRoot
                .shippingMethods()
                .withKey(key)
                .get()
                .withExpand("zoneRates[*].zone")
                .execute()
                .handle(ResponseHandler::handleResponse);
    }

}
