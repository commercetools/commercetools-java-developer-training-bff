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
                        .label(LocalizedStringBuilder.of()
                                .values(labelsForFieldInstructions)
                                .build()
                        )
                        .type(CustomFieldStringType.of())
                        .build(),
                FieldDefinitionBuilder.of()
                        .name("time")
                        .required(false)
                        .label(LocalizedStringBuilder.of()
                                .values(labelsForFieldTime)
                                .build()
                        )
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
                    .name(LocalizedStringBuilder.of().values(nameForType).build())
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
            System.out.println(newSubscriber);
        return existsCustomObjectWithContainerAndKey(container, key)
                .thenCompose(exists -> {
                    if (!exists) {
                        Map<String, Object> currentSubscribers = new HashMap<>();
                        currentSubscribers.putAll(newSubscriber);
                        return postCustomObject(container, key, currentSubscribers);
                    } else {
                        return getCustomObjectWithContainerAndKey(container, key)
                                .thenCompose(customObjectResponseEntity -> {
                                    Map<String, Object> currentSubscribers;
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    currentSubscribers = (Map<String, Object>) customObjectResponseEntity.getBody().getValue();
                                    System.out.println(currentSubscribers);
                                    try {
                                        currentSubscribers.putAll(newSubscriber);
                                        System.out.println(currentSubscribers);
                                        return postCustomObject(container, key, currentSubscribers);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null));
                                    }
                                });
                    }
                });
    }
    private CompletableFuture<ResponseEntity<CustomObject>> postCustomObject(
            String container,
            String key,
            Map<String, Object> currentSubscribers) {
        return apiRoot.customObjects()
                .post(customObjectDraftBuilder -> customObjectDraftBuilder
                        .container(container)
                        .key(key)
                        .value(currentSubscribers))
                .execute()
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
