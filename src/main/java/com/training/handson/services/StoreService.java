package com.training.handson.services;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.store.Store;
import com.commercetools.api.models.store.StorePagedQueryResponse;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class StoreService {

    @Autowired
    private ProjectApiRoot apiRoot;

    @Autowired
    private String storeKey;


    public CompletableFuture<ApiHttpResponse<Store>> getCurrentStore() {
        return
                apiRoot
                        .stores()
                        .withKey(storeKey)
                        .get()
                        .execute();
    }

    public CompletableFuture<ApiHttpResponse<Store>> getStoreById(final String storeId) {
        return
                apiRoot
                        .stores()
                        .withId(storeId)
                        .get()
                        .execute();
    }

    public CompletableFuture<ApiHttpResponse<StorePagedQueryResponse>> getStores() {
        return
                apiRoot
                        .stores()
                        .get()
                        .execute();
    }


}
