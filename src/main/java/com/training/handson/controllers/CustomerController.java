package com.training.handson.controllers;

import com.commercetools.api.models.customer.Customer;
import com.training.handson.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
