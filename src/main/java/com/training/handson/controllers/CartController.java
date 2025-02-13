package com.training.handson.controllers;

import com.commercetools.api.models.cart.Cart;
import com.training.handson.dto.AddressRequest;
import com.training.handson.dto.UpdateCartRequest;
import com.training.handson.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{cartId}")
    public CompletableFuture<ResponseEntity<Cart>> getCart(@PathVariable String cartId) {
        return cartService.getCartById(cartId).thenApply(ResponseConverter::convert);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<Cart>> createCart(
            @RequestBody UpdateCartRequest updateCartRequest) {

        String cartId = updateCartRequest.getCartId();
        String sku = updateCartRequest.getSku();
        String code = updateCartRequest.getCode();
        Long quantity = updateCartRequest.getQuantity();
        String supplyChannel = updateCartRequest.getSupplyChannel();
        String distributionChannel = updateCartRequest.getDistributionChannel();

        if(cartId == null || cartId.isEmpty()){
            return cartService.createAnonymousCart(
                    sku,
                    quantity
//                supplyChannel,
//                distributionChannel
            ).thenApply(ResponseConverter::convert);
        }
        if( code != null ) {
            return cartService.addDiscountToCart(
                    cartId,
                    code
            ).thenApply(ResponseConverter::convert);
        }
        return cartService.addProductToCartBySkusAndChannel(
                cartId,
                sku,
                quantity
//                supplyChannel,
//                distributionChannel
        ).thenApply(ResponseConverter::convert);
    }

    @PostMapping("/shipping-address")
    public CompletableFuture<ResponseEntity<Cart>> setShippingAddress(
            @RequestBody AddressRequest addressRequest) {

            return cartService.setShippingAddress(addressRequest).thenApply(ResponseConverter::convert);
    }

}
