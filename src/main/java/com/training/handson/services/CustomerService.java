package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.cart.CartResourceIdentifier;
import com.commercetools.api.models.cart.CartResourceIdentifierBuilder;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.common.AddressBuilder;
import com.commercetools.api.models.customer.*;
import com.commercetools.api.models.customer_group.CustomerGroup;
import com.commercetools.api.models.order.Order;
import com.training.handson.dto.CustomFieldRequest;
import com.training.handson.dto.CustomerCreateRequest;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Service
public class CustomerService {

    @Autowired
    private ProjectApiRoot apiRoot;

    @Autowired
    private String storeKey;


    public CompletableFuture<ResponseEntity<Customer>> getCustomerByKey(String customerKey) {
        return apiRoot
                .inStore(storeKey)
                .customers()
                .withKey(customerKey)
                .get()
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle(this::handleResponse);
    }

    public CompletableFuture<ResponseEntity<Customer>> getCustomerById(String customerId) {
        return apiRoot
                .inStore(storeKey)
                .customers()
                .withId(customerId)
                .get()
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle(this::handleResponse);
    }


    public CompletableFuture<ResponseEntity<CustomerSignInResult>> createCustomer(
            final CustomerCreateRequest customerCreateRequest) {

        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(CustomerSignInResult.of())
        );
    }

    public CompletableFuture<ResponseEntity<CustomerSignInResult>> loginCustomer(
            final CustomerCreateRequest customerCreateRequest) {

        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(CustomerSignInResult.of())
        );
    }


    public CompletableFuture<ApiHttpResponse<CustomerGroup>> getCustomerGroupByKey(String customerGroupKey) {
        return
            apiRoot
                .customerGroups()
                .withKey(customerGroupKey)
                .get()
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Customer>> assignCustomerToCustomerGroup(
            final String customerKey,
            final String customerGroupKey) {

        return getCustomerByKey(customerKey)
            .thenComposeAsync(customerApiHttpResponse ->
                apiRoot
                    .inStore(storeKey)
                    .customers()
                    .withKey(customerKey)
                    .post(
                        customerUpdateBuilder -> customerUpdateBuilder
                            .version(customerApiHttpResponse.getBody().getVersion())
                            .plusActions(
                                customerUpdateActionBuilder -> customerUpdateActionBuilder
                                    .setCustomerGroupBuilder()
                                    .customerGroup(customerGroupResourceIdentifierBuilder -> customerGroupResourceIdentifierBuilder.key(customerGroupKey))
                            )
                    )
                    .execute()
            );
    }

    public CompletableFuture<ResponseEntity<Customer>> setCustomFields(
            final CustomFieldRequest customFieldRequest) {

        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Customer.of())
        );
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
