package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.order.Order;
import com.training.handson.dto.CustomFieldRequest;
import com.training.handson.dto.OrderRequest;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderService {

    @Autowired
    private ProjectApiRoot apiRoot;

    @Autowired
    private String storeKey;

    @Autowired
    private CustomerService customerService;

    public CompletableFuture<ApiHttpResponse<Order>> getOrderById(final String orderId) {

            return apiRoot
                    .inStore(storeKey)
                    .orders()
                    .withId(orderId)
                    .get()
                    .execute();
    }

    public CompletableFuture<ApiHttpResponse<Order>> getOrderByOrderNumber(final String orderNumber) {

        return apiRoot
                .inStore(storeKey)
                .orders()
                .withOrderNumber(orderNumber)
                .get()
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Order>> createOrder(
            final OrderRequest orderRequest) {

        return apiRoot
                .inStore(storeKey)
                .orders()
                .post(
                        orderFromCartDraftBuilder -> orderFromCartDraftBuilder
                                .cart(cartResourceIdentifierBuilder -> cartResourceIdentifierBuilder.id(orderRequest.getCartId()))
                                .version(orderRequest.getCartVersion())
                                .orderNumber("CT" + System.nanoTime())
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Order>> setCustomFields(
            final CustomFieldRequest customFieldRequest) {

        final String orderNumber = customFieldRequest.getOrderNumber();

        return getOrderByOrderNumber(orderNumber)
                .thenComposeAsync(orderApiHttpResponse -> {
                    if (customFieldRequest.isSave() && orderApiHttpResponse.getBody().getCustomerId() != null) {
                        customFieldRequest.setCustomerId(orderApiHttpResponse.getBody().getCustomerId());
                        customerService.setCustomFields(customFieldRequest);
                    }

                    return apiRoot
                            .inStore(storeKey)
                            .orders()
                            .withOrderNumber(orderNumber)
                            .post(
                                    updateBuilder -> updateBuilder
                                            .version(orderApiHttpResponse.getBody().getVersion())
                                            .plusActions(orderUpdateActionBuilder -> orderUpdateActionBuilder.setCustomTypeBuilder()
                                                    .type(typeResourceIdentifierBuilder -> typeResourceIdentifierBuilder.key("delivery-instructions"))
                                                    .fields(fieldContainerBuilder -> fieldContainerBuilder
                                                            .addValue("instructions", customFieldRequest.getInstructions())
                                                            .addValue("time", customFieldRequest.getTime())
                                                    )
                                            )
                            )
                            .execute();

                });
    }

    public CompletableFuture<ApiHttpResponse<Cart>> replicateOrderByOrderNumber(
            final String orderNumber) {

        return getOrderByOrderNumber(orderNumber)
            .thenComposeAsync(orderApiHttpResponse -> apiRoot
                .inStore(storeKey)
                .carts()
                .replicate()
                .post(
                        replicaCartDraftBuilder -> replicaCartDraftBuilder
                                .reference(referenceBuilder -> referenceBuilder.cartBuilder().id(orderApiHttpResponse.getBody().getCart().getId()))
                )
                .execute());
    }

}
