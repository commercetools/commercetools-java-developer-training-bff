package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.CartResourceIdentifierBuilder;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.common.AddressBuilder;
import com.commercetools.api.models.customer.*;
import com.commercetools.api.models.customer_group.CustomerGroup;
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

    public CompletableFuture<ResponseEntity<CustomerSignInResult>> createCustomer(
            final String email,
            final String password,
            final String anonymousCartId) {

        return apiRoot
            .inStore(storeKey)
                .customers()
                .post(
                        customerDraftBuilder -> customerDraftBuilder
                                .email(email)
                                .password(password)
                                .key("ct-" + System.nanoTime())
                                .anonymousCart(CartResourceIdentifierBuilder.of().id(anonymousCartId).build())
                )
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle(this::handleResponse);
    }

    public CompletableFuture<ResponseEntity<CustomerSignInResult>> createCustomer(
            final String email,
            final String password,
            final String customerKey,
            final String firstName,
            final String lastName,
            final String country) {

        return apiRoot
            .inStore(storeKey)
            .customers()
            .post(
                customerDraftBuilder -> customerDraftBuilder
                    .email(email)
                    .password(password)
                    .firstName(firstName)
                    .lastName(lastName)
                    .key(customerKey)
                    .addresses(
                        AddressBuilder.of()
                            .key(customerKey + "-default-address")
                            .firstName(firstName)
                            .lastName(lastName)
                            .country(country)
                            .build()
                    )
                    .defaultShippingAddress(0)
//                    .stores(StoreResourceIdentifierBuilder.of().key(storeKey).build())
                )
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle(this::handleResponse);
    }

    public CompletableFuture<ResponseEntity<CustomerSignInResult>> loginCustomer(
            final String customerEmail,
            final String password) {
        CustomerSignin customerSignin = CustomerSigninBuilder.of()
                .email(customerEmail)
                .password(password)
                .build();
        return apiRoot
                .inStore(storeKey)
                .login()
                .post(customerSignin)
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle(this::handleResponse);
    }

    public CompletableFuture<ResponseEntity<CustomerSignInResult>> loginCustomer(
            final String customerEmail,
            final String password,
            final String anonymousCartId) {
        CustomerSignin customerSignin = CustomerSigninBuilder.of()
                .email(customerEmail)
                .password(password)
                .anonymousCart(CartResourceIdentifierBuilder.of()
                        .id(anonymousCartId)
                        .build())
//                .anonymousCartSignInMode(AnonymousCartSignInMode.MERGE_WITH_EXISTING_CUSTOMER_CART)
                .build();
        return apiRoot
                .inStore(storeKey)
                .login()
                .post(customerSignin)
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle(this::handleResponse);
    }

    public CompletableFuture<ApiHttpResponse<CustomerToken>> createEmailVerificationToken(
            final ApiHttpResponse<CustomerSignInResult> customerSignInResultApiHttpResponse,
            final long timeToLiveInMinutes
    ) {

        final Customer customer = customerSignInResultApiHttpResponse.getBody().getCustomer();

        return
            apiRoot
                .inStore(storeKey)
                .customers()
                .emailToken()
                .post(
                    customerCreateEmailTokenBuilder -> customerCreateEmailTokenBuilder
                        .id(customer.getId())
                        .ttlMinutes(timeToLiveInMinutes)
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<CustomerToken>> createEmailVerificationToken(final Customer customer, final long timeToLiveInMinutes) {

        return
            apiRoot
                .inStore(storeKey)
                .customers()
                .emailToken()
                .post(
                    customerCreateEmailTokenBuilder -> customerCreateEmailTokenBuilder
                        .id(customer.getId())
                        .ttlMinutes(timeToLiveInMinutes)
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Customer>> verifyEmail(final ApiHttpResponse<CustomerToken> customerTokenApiHttpResponse) {

        final CustomerToken customerToken = customerTokenApiHttpResponse.getBody();

        return
            apiRoot
                .inStore(storeKey)
                .customers()
                .emailConfirm()
                .post(
                    customerEmailVerifyBuilder ->customerEmailVerifyBuilder
                        .tokenValue(customerToken.getValue())
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Customer>> verifyEmail(final CustomerToken customerToken) {

        return
            apiRoot
                .inStore(storeKey)
                .customers()
                .emailConfirm()
                .post(
                    customerEmailVerifyBuilder ->customerEmailVerifyBuilder
                        .tokenValue(customerToken.getValue())
                    )
                .execute();
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

    public CompletableFuture<ApiHttpResponse<Customer>> addDefaultShippingAddressToCustomer(
            final String customerKey,
            final Address address) {

        return getCustomerByKey(customerKey)
                .thenComposeAsync(customerApiHttpResponse ->
                        apiRoot
                                .inStore(storeKey)
                                .customers()
                                .withKey(customerKey)
                                .post(
                                        CustomerUpdateBuilder.of()
                                                .actions(
                                                        Arrays.asList(
                                                                CustomerAddAddressActionBuilder.of()
                                                                        .address(address)
                                                                        .build(),
                                                                CustomerSetDefaultShippingAddressActionBuilder.of()
                                                                        .addressKey(address.getKey())
                                                                        .build()
                                                        )
                                                )
                                                .version(customerApiHttpResponse.getBody().getVersion())
                                                .build()
                                )
                                .execute()
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
        throwable.printStackTrace();
    }

}
