package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.*;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.shipping_method.ShippingMethod;
import com.commercetools.api.models.store.Store;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartService {

    @Autowired
    private ProjectApiRoot apiRoot;

    @Autowired
    private String storeKey;

    @Autowired
    private StoreService storeService;

    public CompletableFuture<ResponseEntity<Cart>> getCartById(final String cartId) {

            return apiRoot
                    .inStore(storeKey)
                    .carts()
                    .withId(cartId)
                    .get()
                    .execute()
                    .thenApply(ApiHttpResponse::getBody)
                    .handle(this::handleResponse);
    }

    public CompletableFuture<ResponseEntity<Cart>> createCustomerCart(
            final ApiHttpResponse<Customer> customerApiHttpResponse,
            final ApiHttpResponse<Store> storeApiHttpResponse,
            final String sku,
            final Long quantity,
            final String supplyChannelKey,
            final String distChannelKey) {

        final Customer customer = customerApiHttpResponse.getBody();
        final String countryCode = storeApiHttpResponse.getBody().getCountries().get(0).getCode();
        String currencyCode;
        switch (countryCode) {
            case "US":
                currencyCode = "USD";
                break;
            case "UK":
                currencyCode = "GBP";
                break;
            default:
                currencyCode = "EUR";
                break;
        }
        return
            apiRoot
                .inStore(storeKey)
                .carts()
                .post(
                    cartDraftBuilder -> cartDraftBuilder
                        .currency(currencyCode)
                        .deleteDaysAfterLastModification(90L)
                        .customerEmail(customer.getEmail())
                        .customerId(customer.getId())
                        .country(countryCode)
                        .shippingAddress(customer.getAddresses().stream()
                            .filter(address -> address.getId().equals(customer.getDefaultShippingAddressId()))
                            .findFirst()
                            .orElse(null))
                        .addLineItems(lineItemDraftBuilder -> lineItemDraftBuilder
                                .sku(sku)
                                .supplyChannel(channelResourceIdentifierBuilder ->
                                        channelResourceIdentifierBuilder.key(supplyChannelKey))
                                .distributionChannel(channelResourceIdentifierBuilder ->
                                        channelResourceIdentifierBuilder.key(distChannelKey))
                                .quantity(quantity)
                                .build())
                        .inventoryMode(InventoryMode.NONE)
                )
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle(this::handleResponse);
    }

    public CompletableFuture<ResponseEntity<Cart>> createAnonymousCart(
            final String sku,
            final Long quantity
//            final String supplyChannelKey,
//            final String distChannelKey
    ) {

        return storeService.getCurrentStore()
                .thenApply(ApiHttpResponse::getBody)
                .thenCompose(store -> {
                    String countryCode = store.getCountries().get(0).getCode();
                    String currencyCode = switch (countryCode) {
                        case "US" -> "USD";
                        case "UK" -> "GBP";
                        default -> "EUR";
                    };
                    return apiRoot
                                .inStore(storeKey)
                                .carts()
                                .post(
                                        cartDraftBuilder -> cartDraftBuilder
                                                .currency(currencyCode)
                                                .deleteDaysAfterLastModification(90L)
                                                .anonymousId("an" + System.nanoTime())
                                                .country(countryCode)
                                                .addLineItems(lineItemDraftBuilder -> lineItemDraftBuilder
                                                        .sku(sku)
//                                                        .supplyChannel(channelResourceIdentifierBuilder ->
//                                                                channelResourceIdentifierBuilder.key(supplyChannelKey))
//                                                        .distributionChannel(channelResourceIdentifierBuilder ->
//                                                                channelResourceIdentifierBuilder.key(distChannelKey))
                                                        .quantity(quantity)
                                                        .build())
                                )
                                .execute();
                })
                .thenApply(ApiHttpResponse::getBody)
                .handle(this::handleResponse);
    }

    public CompletableFuture<ApiHttpResponse<Cart>> replicateOrderByOrderNumber(
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
            .execute());
    }

    public CompletableFuture<ResponseEntity<Cart>> addProductToCartBySkusAndChannel(
            final String cartAId,
            final String sku,
            final Long quantity
//            final String supplyChannelKey,
//            final String distChannelKey
    ) {

        return this.getCartById(cartAId)
            .thenApply(HttpEntity::getBody)
            .thenCompose(cart -> {
                CartUpdateAction cartAddLineItemAction =
                    CartAddLineItemActionBuilder.of()
                        .sku(sku)
                        .quantity(quantity)
//                        .supplyChannel(
//                                channelResourceIdentifierBuilder -> channelResourceIdentifierBuilder.key(supplyChannelKey))
//                        .distributionChannel(
//                                channelResourceIdentifierBuilder -> channelResourceIdentifierBuilder.key(distChannelKey))
                        .build();

                return
                    apiRoot
                        .inStore(storeKey)
                        .carts()
                        .withId(cart.getId())
                        .post(
                            cartUpdateBuilder -> cartUpdateBuilder
                                .version(cart.getVersion())
                                .actions(cartAddLineItemAction)
                        )
                        .execute()
                        .thenApply(ApiHttpResponse::getBody)
                        .handle(this::handleResponse);
            });
    }

    public CompletableFuture<ApiHttpResponse<Cart>> addDiscountToCart(
            final ApiHttpResponse<Cart> cartApiHttpResponse,
            final String code) {

            final Cart cart = cartApiHttpResponse.getBody();
            return apiRoot
                    .inStore(storeKey)
                    .carts()
                    .withId(cart.getId())
                    .post(
                            cartUpdateBuilder -> cartUpdateBuilder
                                    .version(cart.getVersion())
                                    .plusActions(
                                            cartUpdateActionBuilder -> cartUpdateActionBuilder.addDiscountCodeBuilder()
                                                    .code(code)
                                    )
                    )
                    .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> setShippingAddress(
            final ApiHttpResponse<Cart> cartApiHttpResponse,
            final Address address) {

        final Cart cart = cartApiHttpResponse.getBody();
        try {
            return apiRoot
                    .inStore(storeKey)
                    .carts()
                    .withId(cart.getId())
                    .post(
                            cartUpdateBuilder -> cartUpdateBuilder
                                    .version(cart.getVersion())
                                    .plusActions(
                                            cartUpdateActionBuilder -> cartUpdateActionBuilder
                                                    .setShippingAddressBuilder()
                                                    .address(address)
                                    )
                    )
                    .execute();
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            throw new RuntimeException();
        }

    }

    public CompletableFuture<ApiHttpResponse<Cart>> freezeCart(final ApiHttpResponse<Cart> cartApiHttpResponse) {

        final Cart cart = cartApiHttpResponse.getBody();
        return
                apiRoot
                        .inStore(storeKey)
                        .carts()
                        .withId(cart.getId())
                        .post(
                                cartUpdateBuilder -> cartUpdateBuilder
                                        .version(cart.getVersion())
                                        .plusActions(
                                                CartUpdateActionBuilder::freezeCartBuilder
                                        )
                        )
                        .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> unfreezeCart(final ApiHttpResponse<Cart> cartApiHttpResponse) {

        final Cart cart = cartApiHttpResponse.getBody();
        return
                apiRoot
                        .inStore(storeKey)
                        .carts()
                        .withId(cart.getId())
                        .post(
                                cartUpdateBuilder -> cartUpdateBuilder
                                        .version(cart.getVersion())
                                        .plusActions(
                                                CartUpdateActionBuilder::unfreezeCartBuilder
                                        )
                        )
                        .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> recalculate(final ApiHttpResponse<Cart> cartApiHttpResponse) {

        final Cart cart = cartApiHttpResponse.getBody();
        return
                apiRoot
                        .inStore(storeKey)
                        .carts()
                        .withId(cart.getId())
                        .post(
                                cartUpdateBuilder -> cartUpdateBuilder
                                        .version(cart.getVersion())
                                        .plusActions(
                                                cartUpdateActionBuilder -> cartUpdateActionBuilder
                                                        .recalculateBuilder()
                                                        .updateProductData(true)
                                        )
                        )
                        .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> setShipping(final ApiHttpResponse<Cart> cartApiHttpResponse) {

        final Cart cart = cartApiHttpResponse.getBody();

        final ShippingMethod shippingMethod =
            apiRoot
                .shippingMethods()
                .matchingCart()
                .get()
                .withCartId(cart.getId())
                .executeBlocking()
                .getBody().getResults().get(0);
        return apiRoot
            .inStore(storeKey)
            .carts()
            .withId(cart.getId())
            .post(
                cartUpdateBuilder -> cartUpdateBuilder
                    .version(cart.getVersion())
                    .plusActions(
                        cartUpdateActionBuilder -> cartUpdateActionBuilder
                            .setShippingMethodBuilder()
                            .shippingMethod(
                                shippingMethodResourceIdentifierBuilder -> shippingMethodResourceIdentifierBuilder
                                    .id(shippingMethod.getId())
                            )
                    )
            )
            .execute();
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