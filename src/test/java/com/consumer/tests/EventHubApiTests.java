package com.consumer.tests;

import com.consumer.auth.JwtAuthManager;
import com.consumer.pojos.AuthPayload;
import com.consumer.pojos.EventPayload;
import com.enterprise.api.config.ConfigManager;
import com.enterprise.api.logging.ApiLogger;
import com.enterprise.api.request.RequestBuilder;
import com.enterprise.api.response.ResponseValidator;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class EventHubApiTests extends BaseTest {

    private String extractedJwtToken;
    private static final String TEST_EMAIL = "student_2026@example.com";
    private static final String TEST_PASS = "secret123";

    @Test(priority = 1)
    public void testRegisterUser() {
        ApiLogger.logInfo("--- Starting testRegisterUser ---");

        AuthPayload payload = new AuthPayload();
        payload.setEmail(TEST_EMAIL);
        payload.setPassword(TEST_PASS);

        // 1. Build Request using Jackson (false flag)
        RequestSpecification request = new RequestBuilder()
                .setBaseUri(ConfigManager.get("baseUri"))
                .setHeaders(Map.of("Content-Type", "application/json"))
                .setBody(payload, false)
                .build();

        ApiLogger.logRequest(request);

        // 2. Execute
        Response response = request.post(ConfigManager.get("registerEndpoint"));
        ApiLogger.logResponse(response);

        // 3. Validate
        ResponseValidator validator = new ResponseValidator(response);
        validator.assertStatusCode(200);

        // Assertions using JsonPath utility
        Assert.assertTrue(validator.getJsonPathObject().getBoolean("success"), "Registration failed");
        Assert.assertEquals(validator.extractString("user.email"), TEST_EMAIL);
    }

    @Test(priority = 2, dependsOnMethods = "testRegisterUser")
    public void testLoginUserAndExtractToken() {
        ApiLogger.logInfo("--- Starting testLoginUserAndExtractToken ---");

        AuthPayload payload = new AuthPayload();
        payload.setEmail(TEST_EMAIL);
        payload.setPassword(TEST_PASS);

        RequestSpecification request = new RequestBuilder()
                .setBaseUri(ConfigManager.get("baseUri"))
                .setHeaders(Map.of("Content-Type", "application/json"))
                .setBody(payload, false)
                .build();

        Response response = request.post(ConfigManager.get("loginEndpoint"));

        ResponseValidator validator = new ResponseValidator(response);
        validator.assertStatusCode(200);

        // 4. Extract token for downstream tests
        this.extractedJwtToken = validator.extractString("token");
        Assert.assertNotNull(this.extractedJwtToken, "JWT Token was null!");
    }

    @Test(priority = 3, dependsOnMethods = "testLoginUserAndExtractToken")
    public void testCreateEvent() {
        ApiLogger.logInfo("--- Starting testCreateEvent ---");

        // 5. Use the domain-specific AuthManager with the extracted token
        JwtAuthManager authManager = new JwtAuthManager(this.extractedJwtToken);

        EventPayload eventPayload = new EventPayload();
        eventPayload.setTitle("Tech Summit 2026");
        eventPayload.setDescription("A premier technology conference.");
        eventPayload.setCategory("Conference");
        eventPayload.setVenue("Bangalore International Centre");
        eventPayload.setCity("Bangalore");
        eventPayload.setEventDate("2026-06-15T09:00:00.000Z");
        eventPayload.setPrice(1500);
        eventPayload.setTotalSeats(500);
        eventPayload.setImageUrl("https://example.com/banner.jpg");

        RequestSpecification request = new RequestBuilder()
                .setBaseUri(ConfigManager.get("baseUri"))
                .setAuth(authManager) // Injects the Bearer token safely
                .setHeaders(Map.of("Content-Type", "application/json"))
                .setBody(eventPayload, false)
                .build();

        ApiLogger.logRequest(request);
        Response response = request.post(ConfigManager.get("createEventEndpoint"));
        ApiLogger.logResponse(response);

        ResponseValidator validator = new ResponseValidator(response);
        validator.assertStatusCode(200);

        // Complex Validation
        Assert.assertEquals(validator.extractString("message"), "Event created successfully");
        Assert.assertEquals(validator.extractInt("data.price"), 1500);
        Assert.assertEquals(validator.extractString("data.title"), "Tech Summit 2026");
        Assert.assertNotNull(validator.extractString("data.createdAt"), "CreatedAt timestamp missing");
    }
}