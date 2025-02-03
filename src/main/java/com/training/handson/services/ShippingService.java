package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.shipping_method.ShippingMethod;
import com.commercetools.api.models.shipping_method.ShippingMethodPagedQueryResponse;
import io.vrap.rmf.base.client.ApiHttpResponse;
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

    public CompletableFuture<ResponseEntity<ShippingMethodPagedQueryResponse>> getShippingMethods() {
        return apiRoot
                .shippingMethods()
                .get()
                .withExpand("zoneRates[*].zone")
                .execute()
                .handle(ResponseHandler::handleResponse);
    }

    public CompletableFuture<ResponseEntity<List<ShippingMethod>>> getShippingMethodsByCountry(String countryCode) {
        return apiRoot
                .shippingMethods()
                .matchingLocation()
                .get()
                .addCountry(countryCode)
                .withExpand("zoneRates[*].zone")
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle((shippingMethods, throwable) -> {
                    if (shippingMethods == null || shippingMethods.getResults().isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Arrays.asList(ShippingMethod.of()));
                    }
                    return ResponseEntity.ok(shippingMethods.getResults());
                });
    }

    public CompletableFuture<ResponseEntity<Boolean>> checkShippingMethodExistence(String key) {
        return apiRoot
                .shippingMethods()
                .withKey(key)
                .head()
                .execute()
                .thenApply(response -> {
                    boolean exists = response.getStatusCode() == 200;
                    return ResponseEntity.ok(exists);
                })
                .handle((responseEntity, throwable) -> {
                   if(throwable != null) {
                       return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
                   }
                   return responseEntity;
                });
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
