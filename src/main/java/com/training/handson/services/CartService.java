package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.shipping_method.ShippingMethod;
import com.training.handson.dto.AddressRequest;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class CartService {

    @Autowired
    private ProjectApiRoot apiRoot;

    @Autowired
    private String storeKey;

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

        // TODO: Create a cart with anonymousId and add SKU as a line item
        return CompletableFuture.completedFuture(
                new ApiHttpResponse<>(501, null, Cart.of())
        );
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

        // TODO: Add SKU to the cart
        return CompletableFuture.completedFuture(
                new ApiHttpResponse<>(501, null, Cart.of())
        );
    }

    public CompletableFuture<ApiHttpResponse<Cart>> addDiscountToCart(
            final String cartId,
            final String code) {

        // TODO: Set Discount code in the cart
        return CompletableFuture.completedFuture(
                new ApiHttpResponse<>(501, null, Cart.of())
        );
    }

    public CompletableFuture<ApiHttpResponse<Cart>> setShippingAddress(
            final AddressRequest addressRequest) {

        // TODO: Set Shipping address on the cart
        // TODO: Set default Shipping Method (update setShipping method, if needed)
        return CompletableFuture.completedFuture(
                new ApiHttpResponse<>(501, null, Cart.of())
        );

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

}
