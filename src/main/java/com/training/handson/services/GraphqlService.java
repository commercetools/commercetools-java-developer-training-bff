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
                .execute();
    }

}
