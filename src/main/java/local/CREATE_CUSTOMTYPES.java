package local;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.LocalizedStringBuilder;
import com.commercetools.api.models.type.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class CREATE_CUSTOMTYPES implements CommandLineRunner {

    @Autowired
    private ProjectApiRoot apiRoot;

    public static void main(String[] args) {
        SpringApplication.run(CREATE_CUSTOMTYPES.class, args);
    }

    @Override
    public void run(String... args) {
        try {
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
            apiRoot
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
                    .thenAccept(typeApiHttpResponse ->
                            System.out.println("Custom Type ID: " + typeApiHttpResponse.getBody().getId())
                    )
                    .exceptionally(throwable -> {
                        System.out.println("Exception: " + throwable.getMessage());
                        return null;
                    }).join();
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }

        System.exit(0);
    }
}
