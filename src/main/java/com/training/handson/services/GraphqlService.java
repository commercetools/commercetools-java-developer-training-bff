package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.graphql.api.GraphQLResponse;
import com.commercetools.graphql.api.GraphQL;
import com.commercetools.graphql.api.GraphQLData;
import com.commercetools.graphql.api.GraphQLRequest;
import com.commercetools.graphql.api.types.Order;
import com.commercetools.graphql.api.types.OrderQueryResult;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class GraphqlService {

    @Autowired
    private ProjectApiRoot apiRoot;

    public CompletableFuture<ResponseEntity<GraphQLResponse<OrderQueryResult>>> getOrderSummaryByEmail(final String customerEmail) {

        String query = "query($where:String!)  {\n" +
                "  orders(where: $where) {\n" +
                "    results {\n" +
                "      customerEmail\n" +
                "       customer {\n" +
                "       firstName\n" +
                "       lastName\n" +
                "       }\n" +
                "      lineItems {\n" +
                "        name(locale: \"en-US\")\n" +
                "      }\n" +
                "      CartTotal: totalPrice {centAmount currencyCode}\n" +
                "    }\n" +
                "  }\n" +
                "}";


        // Create the GraphQL request
        GraphQLRequest<OrderQueryResult> graphQLRequest = GraphQL
                .query(query)
                .variables(builder -> builder.addValue("where", "customerEmail=\""+customerEmail+"\""))
                .dataMapper(GraphQLData::getOrders)
                .build();

        // Execute the query
        return apiRoot
                .graphql()
                .query(graphQLRequest)
                .execute()
                .thenApply(response -> handleResponse(response.getBody(), null))
                .exceptionally(throwable -> handleResponse(null, throwable));
    }



    private <T> ResponseEntity<T> handleResponse(T body, Throwable throwable) {
        if (throwable != null) {
            logError(throwable);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        if (body instanceof GraphQLResponse && ((GraphQLResponse) body).getErrors() != null) {
            logError(new Exception("GraphQL returned errors"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }

        if (body == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(body);
    }

    private void logError(Throwable throwable) {
        System.err.println("Error occurred: " + throwable.getMessage());
        throwable.printStackTrace();
    }

}
