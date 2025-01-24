package com.training.handson.services;

import com.commercetools.importapi.client.ProjectApiRoot;
import com.commercetools.importapi.models.common.LocalizedString;
import com.commercetools.importapi.models.common.ProductTypeKeyReferenceBuilder;
import com.commercetools.importapi.models.importrequests.ImportResponse;
import com.commercetools.importapi.models.importrequests.ProductImportRequestBuilder;
import com.commercetools.importapi.models.importsummaries.ImportSummary;
import com.commercetools.importapi.models.products.ProductImport;
import com.commercetools.importapi.models.products.ProductImportBuilder;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ImportService {

    @Autowired
    private ProjectApiRoot apiRoot;

    public CompletableFuture<ResponseEntity<ImportResponse>> importProductsFromCsv(
            final MultipartFile csvFile) {

        return
                apiRoot
                        .products()
                        .importContainers()
                        .withImportContainerKeyValue("my-import-container")
                        .post(
                                 ProductImportRequestBuilder.of()
                                         .resources(getProductImportsFromCsv(csvFile))
                                         .build()
                        )
                        .execute()
                        .thenApply(ApiHttpResponse::getBody)
                        .handle(this::handleResponse);
    }

    private List<ProductImport> getProductImportsFromCsv(final MultipartFile csvFile) {

        List<ProductImport> productImports = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {

            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                List<String> values = Arrays.asList(line.split(","));
                if (values.size() >= 4) {
                    LocalizedString name = LocalizedString.of();
                    name.setValue("en-US", values.get(2));
                    LocalizedString slug = LocalizedString.of();
                    slug.setValue("en-US", values.get(3));
                    ProductImport productImport = ProductImportBuilder.of()
                            .key(values.get(0))
                            .productType(ProductTypeKeyReferenceBuilder.of().key(values.get(1)).build())
                            .name(name)
                            .slug(slug)
                            .build();
                    productImports.add(productImport);
                }
                else {
                    System.out.println("skipping line");
                }
            }
        }
        catch (Exception e){
            System.err.println(e);
        }
        return productImports;
    }

    public CompletableFuture<ResponseEntity<ImportSummary>> getImportContainerSummary(final String containerKey) {
        return
                apiRoot
                        .importContainers()
                        .withImportContainerKeyValue("my-import-container")
                        .importSummaries()
                        .get()
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
