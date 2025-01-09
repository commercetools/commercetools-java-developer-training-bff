package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.*;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.order.Order;
import com.commercetools.api.models.shipping_method.ShippingMethod;
import com.commercetools.api.models.store.Store;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderService {

    @Autowired
    private ProjectApiRoot apiRoot;

    @Autowired
    private String storeKey;

    @Autowired
    private StoreService storeService;

    public CompletableFuture<ResponseEntity<Order>> getOrderById(final String orderId) {

            return apiRoot
                    .inStore(storeKey)
                    .orders()
                    .withId(orderId)
                    .get()
                    .execute()
                    .thenApply(ApiHttpResponse::getBody)
                    .handle(this::handleResponse);
    }

    public CompletableFuture<ResponseEntity<Order>> createOrder(
            final String cartId,
            final Long cartVersion) {

        return apiRoot
                .inStore(storeKey)
                .orders()
                .post(
                        orderFromCartDraftBuilder -> orderFromCartDraftBuilder
                                .cart(cartResourceIdentifierBuilder -> cartResourceIdentifierBuilder.id(cartId))
                                .version(cartVersion)
                                .orderNumber("CT" + System.nanoTime())
                )
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle(this::handleResponse);
    }
    public CompletableFuture<ResponseEntity<Cart>> replicateOrderByOrderNumber(
            final String orderNumber) {

        return apiRoot.inStore(storeKey).orders().withOrderNumber(orderNumber).get().execute()
            .thenComposeAsync(orderApiHttpResponse -> apiRoot
                .inStore(storeKey)
                .carts()
                .replicate()
                .post(
                        replicaCartDraftBuilder -> replicaCartDraftBuilder
                                .reference(referenceBuilder -> referenceBuilder.cartBuilder().id(orderApiHttpResponse.getBody().getCart().getId()))
                )
                .execute())
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
        throwable.printStackTrace();
    }

}
