package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.*;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.common.AddressBuilder;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.shipping_method.ShippingMethod;
import com.commercetools.api.models.store.Store;
import com.training.handson.dto.AddressRequest;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class CartService {

    @Autowired
    private ProjectApiRoot apiRoot;

    @Autowired
    private String storeKey;

    @Autowired
    private StoreService storeService;

    public CompletableFuture<ApiHttpResponse<Cart>> getCartById(final String cartId) {

            return apiRoot
                    .inStore(storeKey)
                    .carts()
                    .withId(cartId)
                    .get()
                    .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> createAnonymousCart(
            final String sku,
            final Long quantity
//            final String supplyChannelKey,
//            final String distChannelKey
    ) {

        return storeService.getCurrentStore()
                .thenApply(ApiHttpResponse::getBody)
                .thenCompose(store -> {
                    String countryCode = store.getCountries().get(0).getCode();
                    String currencyCode = getCurrencyCodeByCountry(countryCode);
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
                });
    }

    public CompletableFuture<ApiHttpResponse<Cart>> createCustomerCart(
            final ApiHttpResponse<Customer> customerApiHttpResponse,
            final ApiHttpResponse<Store> storeApiHttpResponse,
            final String sku,
            final Long quantity,
            final String supplyChannelKey,
            final String distChannelKey) {

        final Customer customer = customerApiHttpResponse.getBody();
        final String countryCode = storeApiHttpResponse.getBody().getCountries().get(0).getCode();
        String currencyCode = getCurrencyCodeByCountry(countryCode);

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
                        .execute();
    }

    private String getCurrencyCodeByCountry(final String countryCode){
        return switch (countryCode) {
            case "US" -> "USD";
            case "UK" -> "GBP";
            default -> "EUR";
        };
    }

    public CompletableFuture<ApiHttpResponse<Cart>> addProductToCartBySkusAndChannel(
            final String cartId,
            final String sku,
            final Long quantity
//            final String supplyChannelKey,
//            final String distChannelKey
    ) {

        return this.getCartById(cartId)
            .thenApply(ApiHttpResponse::getBody)
            .thenCompose(cart -> {
                CartUpdateAction cartUpdateAction =
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
                                .actions(cartUpdateAction)
                        )
                        .execute();
            });
    }

    public CompletableFuture<ApiHttpResponse<Cart>> addDiscountToCart(
            final String cartId,
            final String code) {

        return this.getCartById(cartId)
                .thenApply(ApiHttpResponse::getBody)
                .thenCompose(cart -> {
                    CartUpdateAction cartUpdateAction =
                        CartAddDiscountCodeActionBuilder.of()
                            .code(code)
                            .build();

                    return
                            apiRoot
                                    .inStore(storeKey)
                                    .carts()
                                    .withId(cart.getId())
                                    .post(
                                        cartUpdateBuilder -> cartUpdateBuilder
                                            .version(cart.getVersion())
                                            .actions(cartUpdateAction)
                                    )
                                    .execute();
                });
    }

    public CompletableFuture<ApiHttpResponse<Cart>> setShippingAddress(
            final AddressRequest addressRequest) {

        Address address = AddressBuilder.of()
                .firstName(addressRequest.getFirstName())
                .lastName(addressRequest.getLastName())
                .country(addressRequest.getCountry())
                .email(addressRequest.getEmail())
                .build();

        return this.getCartById(addressRequest.getCartId())
            .thenApply(ApiHttpResponse::getBody)
            .thenCompose(cart -> {
                return
                    apiRoot
                        .inStore(storeKey)
                        .carts()
                        .withId(cart.getId())
                        .post(
                            cartUpdateBuilder -> cartUpdateBuilder
                                .version(cart.getVersion())
                                .plusActions(cartUpdateActionBuilder -> cartUpdateActionBuilder
                                        .setShippingAddressBuilder()
                                        .address(address))
                                .plusActions(cartUpdateActionBuilder -> cartUpdateActionBuilder
                                        .setCustomerEmailBuilder().email(addressRequest.getEmail()))
                        )
                        .execute();
            });

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

    public CompletableFuture<ApiHttpResponse<Cart>> setShippingMethod(final ApiHttpResponse<Cart> cartApiHttpResponse) {

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

}
