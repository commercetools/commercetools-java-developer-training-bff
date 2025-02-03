package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.custom_object.CustomObject;
import com.commercetools.api.models.type.Type;
import com.training.handson.dto.CustomObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class CustomizationService {

    @Autowired
    private ProjectApiRoot apiRoot;

    public CompletableFuture<ResponseEntity<Type>> createType() {
        // Define labels for the fields
        Map<String, String> labelsForFieldInstructions = new HashMap<String, String>() {{
            put("de-DE", "Instructions");
            put("en-US", "Instructions");
        }};
        Map<String, String> labelsForFieldTime = new HashMap<String, String>() {{
            put("de-DE", "Preferred Time");
            put("en-US", "Preferred Time");
        }};

        // TODO: Define Custom Fields

        // TODO: Create a new Custom Type with custom fields
        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(Type.of())
        );
    }


    public CompletableFuture<Boolean> existsCustomObjectWithContainerAndKey(
            final String container,
            final String key) {

        return apiRoot
                .customObjects()
                .head()
                .withWhere("container=\""+container+"\"")
                .addWhere("key=\""+key+"\"")
                .execute()
                .thenApply(response -> {
                    return response.getBody() != null;
                })
                .exceptionally(ex -> {
                    return false;
                });
    }

    public CompletableFuture<ResponseEntity<CustomObject>> createCustomObject(
            final CustomObjectRequest customObjectRequest) {

        // TODO: Create a new Custom Object with container and key values from the request
        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(CustomObject.of()));
    }

    public CompletableFuture<ResponseEntity<CustomObject>> getCustomObjectWithContainerAndKey(
            final String container,
            final String key) {

        // TODO: Return the Custom Object with container and key values from the request
        return CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(CustomObject.of()));
    }

    public CompletableFuture<ResponseEntity<CustomObject>> appendToCustomObject(
            final String container,
            final String key,
            final Map<String, Object> jsonObject) {

        return getCustomObjectWithContainerAndKey(container, key)
                .thenCompose(customObjectResponseEntity -> {
                    Map<String, Object> currentSubscribers = (Map<String, Object>) customObjectResponseEntity.getBody().getValue();
                    currentSubscribers.putAll(jsonObject);

                    return apiRoot.customObjects()
                            .post(customObjectDraftBuilder -> customObjectDraftBuilder
                                    .container(container)
                                    .key(key)
                                    .value(currentSubscribers))
                            .execute();
                })
                .handle(ResponseHandler::handleResponse);
    }
}
