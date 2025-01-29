package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.LocalizedStringBuilder;
import com.commercetools.api.models.custom_object.CustomObject;
import com.commercetools.api.models.type.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.handson.dto.CustomObjectRequest;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

        // Define the fields
        List<FieldDefinition> definitions = Arrays.asList(
                FieldDefinitionBuilder.of()
                        .name("instructions")
                        .required(false)
                        .label(lsb -> lsb.values(labelsForFieldInstructions))
                        .type(CustomFieldStringType.of())
                        .build(),
                FieldDefinitionBuilder.of()
                        .name("time")
                        .required(false)
                        .label(lsb -> lsb.values(labelsForFieldTime))
                        .type(CustomFieldStringType.of())
                        .build()
        );

        // Define the name for the type
        Map<String, String> nameForType = new HashMap<String, String>() {{
            put("de-DE", "Delivery instructions");
            put("en-US", "Delivery instructions");
        }};

        // Create the custom type asynchronously
        return apiRoot
            .types()
            .post(
                typeDraftBuilder -> typeDraftBuilder
                    .key("delivery-instructions")
                    .name(lsb -> lsb.values(nameForType))
                    .resourceTypeIds(
                        ResourceTypeId.CUSTOMER,
                        ResourceTypeId.ORDER
                    )
                    .fieldDefinitions(definitions)
            ).execute()
            .thenApply(ApiHttpResponse::getBody)
            .handle(this::handleResponse);
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

        Map<String, Object> jsonObject = new HashMap<>();

        return apiRoot.customObjects()
                .post(customObjectDraftBuilder -> customObjectDraftBuilder
                        .container(customObjectRequest.getContainer())
                        .key(customObjectRequest.getKey())
                        .value(customObjectRequest.getJsonObject()))
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle(this::handleResponse);
    }

    public CompletableFuture<ResponseEntity<CustomObject>> getCustomObjectWithContainerAndKey(
            final String container,
            final String key) {


        return apiRoot
                .customObjects()
                .withContainerAndKey(container, key)
                .get()
                .execute()
                .thenApply(ApiHttpResponse::getBody)
                .handle(this::handleResponse);
    }

    public CompletableFuture<ResponseEntity<CustomObject>> updateCustomObject(
            final CustomObjectRequest customObjectRequest) {

        final String container = customObjectRequest.getContainer();
        final String key = customObjectRequest.getKey();
        final Map<String, Object> newSubscriber = customObjectRequest.getJsonObject();

        return getCustomObjectWithContainerAndKey(container, key)
                .thenCompose(customObjectResponseEntity -> {
                    Map<String, Object> currentSubscribers = (Map<String, Object>) customObjectResponseEntity.getBody().getValue();
                    currentSubscribers.putAll(newSubscriber);
                    return apiRoot.customObjects()
                            .post(customObjectDraftBuilder -> customObjectDraftBuilder
                                    .container(container)
                                    .key(key)
                                    .value(currentSubscribers))
                            .execute();
                })
                .thenApply(ApiHttpResponse::getBody)
                .handle(this::handleResponse);
    }

    private <T> ResponseEntity<T> handleResponse(T body, Throwable throwable) {
        if (throwable != null) {
            logError(throwable);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
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
