package com.training.handson.controllers;

import com.commercetools.graphql.api.GraphQLResponse;
import com.commercetools.graphql.api.types.OrderQueryResult;
import com.training.handson.services.GraphqlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/graphql")
public class GraphqlController {

    private final GraphqlService graphqlService;

    public GraphqlController(GraphqlService graphqlService) {
        this.graphqlService = graphqlService;
    }

    @GetMapping("/orders/{customerEmail}")
    public CompletableFuture<ResponseEntity<GraphQLResponse<OrderQueryResult>>> getOrder(@PathVariable String customerEmail) {
        return graphqlService.getOrderSummaryByEmail(customerEmail).thenApply(ResponseConverter::convert);
    }

}
