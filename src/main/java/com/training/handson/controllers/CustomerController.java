package com.training.handson.controllers;

import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.customer.CustomerDraft;
import com.commercetools.api.models.customer.CustomerSignInResult;
import com.training.handson.dto.CustomerCreateRequest;
import com.training.handson.services.CustomerService;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/{customerKey}")
    public CompletableFuture<ResponseEntity<Customer>> getCustomer(@PathVariable String customerKey) {
        return customerService.getCustomerByKey(customerKey);
    }

    @PostMapping("/signup")
    public CompletableFuture<ResponseEntity<CustomerSignInResult>> createCustomer(
            @RequestBody CustomerCreateRequest customerCreateRequest) {

        String email = customerCreateRequest.getEmail();
        String password = customerCreateRequest.getPassword();
        String customerKey = customerCreateRequest.getCustomerKey();
        String firstName = customerCreateRequest.getFirstName();
        String lastName = customerCreateRequest.getLastName();
        String country = customerCreateRequest.getCountry();

        return customerService.createCustomer(email, password, customerKey, firstName, lastName, country);
    }

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<CustomerSignInResult>> loginCustomer(
            @RequestBody CustomerCreateRequest customerCreateRequest) {

        String email = customerCreateRequest.getEmail();
        String password = customerCreateRequest.getPassword();
        String cartId = customerCreateRequest.getCartId();

        return customerService.loginCustomer(email, password, cartId);
    }
}
