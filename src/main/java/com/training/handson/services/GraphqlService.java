package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.graphql.api.GraphQL;
import com.commercetools.graphql.api.GraphQLData;
import com.commercetools.graphql.api.GraphQLRequest;
import com.commercetools.graphql.api.GraphQLResponse;
import com.commercetools.graphql.api.types.OrderQueryResult;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class GraphqlService {

    @Autowired
    private ProjectApiRoot apiRoot;


    public CompletableFuture<ApiHttpResponse<GraphQLResponse<OrderQueryResult>>> getOrderSummaryByEmail(final String customerEmail) {

        // TODO: Use GraphQL Explorer to build a query that returns orders for the email in the request.
        // TODO: including customer's name

        String query = "";

        // Create the GraphQL request
        GraphQLRequest<OrderQueryResult> graphQLRequest = GraphQL
                .query(query)
                .variables(builder -> builder.addValue("where", "customerEmail=\"" +customerEmail+"\""))
                .dataMapper(GraphQLData::getOrders)
                .build();

        // Execute the query
        return apiRoot
                .graphql()
                .query(graphQLRequest)
                .execute();
    }

}
