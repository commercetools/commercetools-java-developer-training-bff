package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.order.Order;
import com.training.handson.dto.CustomFieldRequest;
import com.training.handson.dto.OrderRequest;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

    public CompletableFuture<ResponseEntity<Order>> createOrder(
            final OrderRequest orderRequest) {

        // TODO: Create an order using the cardId and version in the request
        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Order.of())
        );
    }

    public CompletableFuture<ResponseEntity<Order>> setCustomFields(
            final CustomFieldRequest customFieldRequest) {

        final String orderNumber = customFieldRequest.getOrderNumber();

        // TODO: Update the order with custom delivery instructions
        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Order.of())
        );
    }

    public CompletableFuture<ResponseEntity<Order>> getOrderByOrderNumber(final String orderNumber) {

        return apiRoot
                .inStore(storeKey)
                .orders()
                .withOrderNumber(orderNumber)
                .get()
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle(this::handleResponse);
    }

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
    private <T> ResponseEntity<T> handleResponse(T body, Throwable throwable) {
        if (throwable != null) {
            logError(throwable);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
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
