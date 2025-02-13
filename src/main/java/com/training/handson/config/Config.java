package com.training.handson.config;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.defaultconfig.ApiRootBuilder;
import com.commercetools.api.defaultconfig.ServiceRegion;
import com.commercetools.importapi.defaultconfig.ImportApiRootBuilder;
import io.vrap.rmf.base.client.http.ErrorMiddleware;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.UUID;

@Configuration
public class Config {

    @Value("${ctp.clientId}")
    private String clientId;

    @Value("${ctp.clientSecret}")
    private String clientSecret;

    @Value("${ctp.projectKey}")
    private String projectKey;

    @Value("${ctp.storeKey}")
    private String storeKey;


    @Bean
    public ProjectApiRoot projectApiRoot() {
        return ApiRootBuilder.of()
                .defaultClient(
                        ClientCredentials.of()
                                .withClientId(clientId)
                                .withClientSecret(clientSecret)
                                .build(),
                        ServiceRegion.GCP_EUROPE_WEST1
                )
                .withPolicies(policyBuilder ->
                        policyBuilder.withRetry(retryPolicyBuilder ->
                                retryPolicyBuilder.maxRetries(3).statusCodes(Arrays.asList(502, 503, 504))))
                .withErrorMiddleware(ErrorMiddleware.ExceptionMode.UNWRAP_COMPLETION_EXCEPTION)
                .addConcurrentModificationMiddleware(2)
                .addCorrelationIdProvider(() -> projectKey + "/" + UUID.randomUUID())
                .build(projectKey);
    }

    @Bean
    public com.commercetools.importapi.client.ProjectApiRoot importApiRoot() {
        return ImportApiRootBuilder.of()
                .defaultClient(
                        ClientCredentials.of()
                                .withClientId(clientId)
                                .withClientSecret(clientSecret)
                                .build(),
                        com.commercetools.importapi.defaultconfig.ServiceRegion.GCP_EUROPE_WEST1
                )
                .build(projectKey);
    }

    @Bean
    public String storeKey() {
        return storeKey;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
