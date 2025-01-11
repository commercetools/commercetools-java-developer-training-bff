package com.training.handson.controllers;

import com.commercetools.api.models.custom_object.CustomObject;
import com.training.handson.dto.CustomObjectRequest;
import com.training.handson.services.CustomizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.commercetools.api.models.type.*;

import java.util.concurrent.CompletableFuture;

@Controller
public class MainController {

    @Autowired
    private CustomizationService customizationService;

    @GetMapping(value = {"/", "/api/**"})
    public String redirect() {
        return "forward:/index.html";
    }

    @PostMapping("/api/types")
    public CompletableFuture<ResponseEntity<Type>> createType() {

        return customizationService.createType();
    }

    @PostMapping("/api/custom-objects")
    public CompletableFuture<ResponseEntity<CustomObject>> updateCustomObject(@RequestBody CustomObjectRequest customObjectRequest) {

        return customizationService.updateCustomObject(customObjectRequest);
    }

    @GetMapping("/api/custom-objects/{container}/{key}")
    public CompletableFuture<ResponseEntity<CustomObject>> getCustomObject(@PathVariable String container,
                                                                           @PathVariable String key) {

        return customizationService.getCustomObjectWithContainerAndKey(container, key);
    }

}

