package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.customer.CustomerSignInResult;
import com.commercetools.api.models.customer_group.CustomerGroup;
import com.training.handson.dto.CustomFieldRequest;
import com.training.handson.dto.CustomerCreateRequest;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
                .handle(ResponseHandler::handleResponse);
    }

    public CompletableFuture<ResponseEntity<Customer>> getCustomerById(String customerId) {
        return apiRoot
                .inStore(storeKey)
                .customers()
                .withId(customerId)
                .get()
                .execute()
                .handle(ResponseHandler::handleResponse);
    }


    public CompletableFuture<ResponseEntity<CustomerSignInResult>> createCustomer(
            final CustomerCreateRequest customerCreateRequest) {

        // TODO: Create (signup) a customer and assign anonymous cart in the request to them
        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(CustomerSignInResult.of())
        );
    }

    public CompletableFuture<ResponseEntity<CustomerSignInResult>> loginCustomer(
            final CustomerCreateRequest customerCreateRequest) {

        // TODO: Login (signin) a customer and assign anonymous cart in the request to them
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

        // TODO: Set a custom type reference and update custom field values from the request
        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Customer.of())
        );
    }

}
