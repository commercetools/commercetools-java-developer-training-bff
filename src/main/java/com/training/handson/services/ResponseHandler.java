package com.training.handson.services;

import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {
    public static <T> ResponseEntity<T> handleResponse(ApiHttpResponse<T> apiHttpResponse, Throwable throwable) {
        if (throwable != null) {
            logError(throwable);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiHttpResponse.getBody());
        } else {
            if (apiHttpResponse.getStatusCode() == 404) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
                return ResponseEntity.status(HttpStatus.OK).body(apiHttpResponse.getBody());
        }
    }

    private static void logError(Throwable throwable) {
        System.err.println("Error occurred: " + throwable.getMessage());
    }
}
