package local;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.Reference;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@SpringBootApplication
public class CREATE_CUSTOMOBJECTS implements CommandLineRunner {

    @Autowired
    private ProjectApiRoot apiRoot;

    public static void main(String[] args) {
        SpringApplication.run(CREATE_CUSTOMOBJECTS.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            Map<String, Object> jsonObject = new HashMap<>();
            jsonObject.put("crossSell", Arrays.asList(
                    (Reference.productBuilder().id("product-id").build()),
                    Reference.productBuilder().id("product-id").build()));
            jsonObject.put("upSell", Arrays.asList(
                    Reference.productBuilder().id("product-id").build(),
                    Reference.productBuilder().id("product-id").build()));

            apiRoot
                    .customObjects()
                    .post(
                            customObjectDraftBuilder -> customObjectDraftBuilder
                                    .container("cross-sell-upsell-info")
                                    .key("rustic-bowl")
                                    .value(jsonObject)
                    ).execute()
                    .thenApply(ApiHttpResponse::getBody)
                    .thenAccept(customObject -> {
                                System.out.println("Custom Object ID: " + customObject.getId());
                            }
                    )
                    .exceptionally(throwable -> {
                        System.out.println("Exception:" + throwable.getMessage());
                        return null;
                    }).join();
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }

        System.exit(0);
    }
}
