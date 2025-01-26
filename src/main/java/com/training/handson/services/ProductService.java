package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
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
                .thenApply(ApiHttpResponse::getBody)
                .handle(this::handleResponse);
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
            builder.facets(createFacets());
        }

        if (StringUtils.isNotEmpty(keyword) || StringUtils.isNotEmpty(storeKey)) {

            if (StringUtils.isNotEmpty(keyword) && StringUtils.isNotEmpty(storeKey)) {

                final String storeId = getStoreId(storeKey);
                builder.query(createSearchQuery(keyword, storeId))
                        .productProjectionParameters(createProductProjectionParams(storeKey));

            } else if (StringUtils.isNotEmpty(keyword)) {

                builder.query(createFullTextQuery(keyword));

            } else if (StringUtils.isNotEmpty(storeKey)) {
                final String storeId = getStoreId(storeKey);
                builder.query(createStoreQuery(storeId))
                        .productProjectionParameters(createProductProjectionParams(storeKey));
            }
        }

        return apiRoot
                .products()
                .search()
                .post(builder.build())
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle(this::handleResponse);
    }

    private List<ProductSearchFacetExpression> createFacets(){
        return Arrays.asList(ProductSearchFacetDistinctExpressionBuilder.of()
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
        );
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
        return SearchFullTextExpressionBuilder.of()
                .fullText(SearchFullTextValueBuilder.of()
                        .field("name")
                        .value(keyword)
                        .language("en-US")
                        .mustMatch(SearchMatchType.ANY)
                        .build())
                .build();
    }

    private SearchQuery createStoreQuery(String storeId) {
        return SearchExactExpressionBuilder.of()
                .exact(SearchAnyValueBuilder.of()
                        .field("stores")
                        .value(storeId)
                        .fieldType(SearchFieldType.SET_REFERENCE)
                        .build())
                .build();
    }

    private ProductSearchProjectionParams createProductProjectionParams(String storeKey) {
        return ProductSearchProjectionParamsBuilder.of()
                .storeProjection(storeKey)
                .priceCurrency("EUR")
                .priceCountry("DE")
                .build();
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
