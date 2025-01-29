package com.training.handson.controllers;

import com.commercetools.api.models.custom_object.CustomObject;
import com.commercetools.importapi.models.importrequests.ImportResponse;
import com.training.handson.dto.CustomObjectRequest;
import com.training.handson.services.CustomizationService;
import com.training.handson.services.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.commercetools.api.models.type.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Controller
public class MainController {

    private final CustomizationService customizationService;
    private final ImportService importService;

    public MainController(CustomizationService customizationService, ImportService importService) {
        this.customizationService = customizationService;
        this.importService = importService;
    }

    @GetMapping(value = {"/", "/api/**"})
    public String redirect() {
        return "forward:/index.html";
    }

    @PostMapping("/api/types")
    public CompletableFuture<ResponseEntity<Type>> createType() {

        return customizationService.createType();
    }

    @PostMapping("/api/custom-objects")
    public CompletableFuture<ResponseEntity<CustomObject>> createCustomObject(@RequestBody CustomObjectRequest customObjectRequest) {

        return customizationService.createCustomObject(customObjectRequest);
    }

    @PostMapping("/api/custom-objects/{container}/{key}")
    public CompletableFuture<ResponseEntity<CustomObject>> updateCustomObject(@PathVariable String container,
                                                                              @PathVariable String key,
                                                                              @RequestBody CustomObjectRequest customObjectRequest) {

        return customizationService.updateCustomObject(customObjectRequest);
    }

    @GetMapping("/api/custom-objects/{container}/{key}")
    public CompletableFuture<ResponseEntity<CustomObject>> getCustomObject(@PathVariable String container,
                                                                           @PathVariable String key) {

        return customizationService.getCustomObjectWithContainerAndKey(container, key);
    }

    @PostMapping("/api/import")
    public CompletableFuture<ResponseEntity<ImportResponse>> importProducts(@RequestParam("file") MultipartFile file) {
            return importService.importProductsFromCsv(file);
    }

}

