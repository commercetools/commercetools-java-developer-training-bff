package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.CartResourceIdentifierBuilder;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.common.AddressBuilder;
import com.commercetools.api.models.customer.*;
import com.commercetools.api.models.customer_group.CustomerGroup;
import com.commercetools.api.models.product.ProductProjection;
import com.commercetools.api.models.product_search.*;
import com.commercetools.api.models.search.*;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductService {

    @Autowired
    private ProjectApiRoot apiRoot;

    @Autowired
    private String storeKey;


    public CompletableFuture<ResponseEntity<ProductProjection>> getProductByKey(String productKey) {
        return apiRoot
                .productProjections()
                .withKey(productKey)
                .get()
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle((product, throwable) -> {
                    if (throwable != null) {
                        // Log the exception and return a response with an appropriate error status
                        throwable.printStackTrace();
                        // Return a ResponseEntity with 500 server error
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);  // Customize response as needed
                    } else {
                        if (product == null) {
                            // If product is null, return 404 Not Found
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                        }
                        // Return the product with a 200 OK status
                        return ResponseEntity.ok(product);
                    }
                });
    }

    public CompletableFuture<ResponseEntity<ProductPagedSearchResponse>> getProducts(String keyword) {
        return apiRoot
                .products()
                .search()
                .post(
                        ProductSearchRequestBuilder.of()
                                .query(SearchFullTextExpressionBuilder.of()
                                        .fullText(SearchFullTextValueBuilder.of()
                                                .field("name")
                                                .value(keyword)
                                                .language("en-US")
                                                .mustMatch(SearchMatchType.ANY)
                                                .build())
                                        .build()
                                )
                                .sort(
                                        SearchSortingBuilder.of()
                                                .field("variants.prices.centAmount")
                                                .mode(SearchSortMode.MAX)
                                                .order(SearchSortOrder.ASC)
                                                .build()
                                )
                                .productProjectionParameters(ProductSearchProjectionParamsBuilder.of()
                                        .storeProjection(storeKey)
                                        .priceCurrency("EUR")
                                        .priceCountry("DE")
                                        .build())
                                .markMatchingVariants(true)
                                .facets(
                                        Arrays.asList(ProductSearchFacetDistinctExpressionBuilder.of()
                                                .distinct(
                                                        ProductSearchFacetDistinctValueBuilder.of()
                                                                .name("Color")
                                                                .field("variants.attributes.color")
                                                                .fieldType(SearchFieldType.LTEXT)
                                                                .language("en-US")
                                                                .level(ProductSearchFacetCountLevelEnum.VARIANTS)
                                                                .scope(ProductSearchFacetScopeEnum.ALL)
                                                                .build()
                                                )
                                                .build(),
                                                ProductSearchFacetDistinctExpressionBuilder.of()
                                                        .distinct(
                                                                ProductSearchFacetDistinctValueBuilder.of()
                                                                        .name("Finish")
                                                                        .field("variants.attributes.finish")
                                                                        .fieldType(SearchFieldType.LTEXT)
                                                                        .language("en-US")
                                                                        .level(ProductSearchFacetCountLevelEnum.VARIANTS)
                                                                        .scope(ProductSearchFacetScopeEnum.ALL)
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                )
                                .build()
                )
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle((productPagedSearchResponse, throwable) -> {
                    if (throwable != null) {
                        // Log the exception and return a response with an appropriate error status
                        throwable.printStackTrace();
                        // Return a ResponseEntity with 500 server error
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);  // Customize response as needed
                    } else {
                        if (productPagedSearchResponse == null) {
                            // If product response is null, return 404 Not Found
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                        }
                        // Return the products with a 200 OK status
                        return ResponseEntity.ok(productPagedSearchResponse);
                    }
                });
    }
}
