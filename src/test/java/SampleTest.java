import com.enterprise.api.config.ConfigManager;
import com.enterprise.api.context.ApiTestContext;
import com.enterprise.api.request.RequestBuilder;
import com.enterprise.api.response.ResponseValidator;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public class SampleTest {

    static void main() {
        ConfigManager.init("src/test/resources/env-qa.json");

        RequestSpecification request = new RequestBuilder()
                .setBaseUri(ConfigManager.get("baseUri"))
                .setHeaders(Map.of("Content-Type", "application/json"))
                .setBody("{\n" +
                        "  \"email\": \"student@example.com\",\n" +
                        "  \"password\": \"secret123\"\n" +
                        "}")
                .build();

        ApiTestContext.setRequest(request);
        Response rawResponse = ApiTestContext.getRequest().post(ConfigManager.get("registerUser"));
        ApiTestContext.setResponse(rawResponse);

        ResponseValidator validator = new ResponseValidator(ApiTestContext.getResponse());
        System.out.println(ApiTestContext.getResponse().asString());
        validator.assertStatusCode(201);
    }
}
