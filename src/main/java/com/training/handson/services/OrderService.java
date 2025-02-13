package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.order.Order;
import com.training.handson.dto.CustomFieldRequest;
import com.training.handson.dto.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderService {

    @Autowired
    private ProjectApiRoot apiRoot;

    @Autowired
    private String storeKey;

    public CompletableFuture<ApiHttpResponse<Order>> createOrder(
            final OrderRequest orderRequest) {

        // TODO: Create an order using the cardId and version in the request
        return CompletableFuture.completedFuture(
                new ApiHttpResponse<>(501, null, Order.of())
        );
    }

    public CompletableFuture<ApiHttpResponse<Order>> setCustomFields(
            final CustomFieldRequest customFieldRequest) {

        final String orderNumber = customFieldRequest.getOrderNumber();

        // TODO: Update the order with custom delivery instructions
        return CompletableFuture.completedFuture(
                new ApiHttpResponse<>(501, null, Order.of())
        );
    }

    public CompletableFuture<ApiHttpResponse<Order>> getOrderByOrderNumber(final String orderNumber) {

        return apiRoot
                .inStore(storeKey)
                .orders()
                .withOrderNumber(orderNumber)
                .get()
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Order>> getOrderById(final String orderId) {

        return apiRoot
                .inStore(storeKey)
                .orders()
                .withId(orderId)
                .get()
                .execute();
    }

}
