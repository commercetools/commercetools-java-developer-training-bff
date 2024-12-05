package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.CartResourceIdentifierBuilder;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.common.AddressBuilder;
import com.commercetools.api.models.customer.*;
import com.commercetools.api.models.customer_group.CustomerGroup;
import com.commercetools.api.models.shipping_method.ShippingMethod;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ShippingService {

    @Autowired
    private ProjectApiRoot apiRoot;

    @Autowired
    private String storeKey;

    public CompletableFuture<ResponseEntity<ShippingMethod[]>> getShippingMethods() {
        return apiRoot
                .shippingMethods()
                .get()
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle((shippingMethods, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);  // Customize response as needed
                    } else {
                        if (shippingMethods == null || shippingMethods.getResults().isEmpty()) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ShippingMethod[0]);
                        }
                        return ResponseEntity.ok(shippingMethods.getResults().toArray(new ShippingMethod[0]));
                    }
                });
    }

    public CompletableFuture<ResponseEntity<ShippingMethod>> getShippingMethodByKey(String key) {
        return apiRoot
                .shippingMethods()
                .withKey(key)
                .get()
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle((shippingMethod, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);  // Customize response as needed
                    } else {
                        if (shippingMethod == null) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                        }
                        return ResponseEntity.ok(shippingMethod);
                    }
                });
    }

    public CompletableFuture<Integer> checkShippingMethodExistence(String key) {
        return apiRoot
                .shippingMethods()
                .withKey(key)
                .head()
                .execute()
                .thenApply(ApiHttpResponse::getStatusCode);
    }


}
