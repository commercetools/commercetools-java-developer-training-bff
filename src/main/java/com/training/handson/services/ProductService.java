package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.order.Order;
import com.commercetools.api.models.product.ProductProjection;
import com.commercetools.api.models.product_search.*;
import com.commercetools.api.models.search.*;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductService {

    @Autowired
    private ProjectApiRoot apiRoot;

    public CompletableFuture<ResponseEntity<ProductProjection>> getProductByKey(String productKey) {
        return apiRoot
                .productProjections()
                .withKey(productKey)
                .get()
                .execute()
                .handle(ResponseHandler::handleResponse);
    }

    public CompletableFuture<ResponseEntity<ProductPagedSearchResponse>> getProducts(
            String keyword,
            String storeKey,
            Boolean includeFacets) {

        ProductSearchRequestBuilder builder = ProductSearchRequestBuilder.of()
                .sort(
                        SearchSortingBuilder.of()
                                .field("variants.prices.centAmount")
                                .mode(SearchSortMode.MAX)
                                .order(SearchSortOrder.ASC)
                                .build()
                )
                .productProjectionParameters(ProductSearchProjectionParamsBuilder.of()
                        .priceCurrency("EUR")
                        .priceCountry("DE")
                        .build())
                .markMatchingVariants(true);

        if (includeFacets != null && includeFacets){
            builder.facets(createFacets()); // TODO: Implement createFacets()
        }

        // Check if at least one is provided, store and/or a keyword
        if (StringUtils.isNotEmpty(keyword) || StringUtils.isNotEmpty(storeKey)) {

            // Check if both are provided, store and a keyword
            if (StringUtils.isNotEmpty(keyword) && StringUtils.isNotEmpty(storeKey)) {

                final String storeId = getStoreId(storeKey);
                builder.query(createSearchQuery(keyword, storeId))
                        .productProjectionParameters(createProductProjectionParams(storeKey)); // TODO: Implement

            } else if (StringUtils.isNotEmpty(keyword)) { // Check if only keyword is provided

                builder.query(createFullTextQuery(keyword)); // TODO: Implement createFullTextQuery()

            } else if (StringUtils.isNotEmpty(storeKey)) { // Check if only store is provided
                final String storeId = getStoreId(storeKey);
                builder.query(createStoreQuery(storeId)) // TODO: Implement createStoreQuery()
                        .productProjectionParameters(createProductProjectionParams(storeKey));
            }
        }

        // TODO: Execute API query
        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(ProductPagedSearchResponse.of())
        );
    }

    private List<ProductSearchFacetExpression> createFacets(){
        return Arrays.asList(ProductSearchFacetDistinctExpression.of());
    }

    private String getStoreId(String storeKey) {
        return apiRoot.stores().withKey(storeKey).get().executeBlocking().getBody().getId();
    }

    private SearchQuery createSearchQuery(String keyword, String storeId) {
        return SearchAndExpressionBuilder.of()
                .and(Arrays.asList(
                        createFullTextQuery(keyword),
                        createStoreQuery(storeId)
                ))
                .build();
    }

    private SearchQuery createFullTextQuery(String keyword) {
        return SearchFullTextExpression.of();
    }

    private SearchQuery createStoreQuery(String storeId) {
        return SearchExactExpression.of();
    }

    private ProductSearchProjectionParams createProductProjectionParams(String storeKey) {
        return ProductSearchProjectionParams.of();
    }

}
