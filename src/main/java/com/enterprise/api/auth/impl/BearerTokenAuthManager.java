package com.enterprise.api.auth.impl;

import com.enterprise.api.auth.AuthManager;
import com.enterprise.api.exceptions.AuthenticationException;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BearerTokenAuthManager implements AuthManager {

    private final String tokenUrl;
    private final String clientId;
    private final String clientSecret;
    private final String scope;
    private final String grantType;

    // Volatile for cross-thread visibility
    private volatile String currentToken;
    private volatile long tokenExpiryTime;

    /**
     * Constructor for standard OAuth2 Client Credentials flow.
     */
    public BearerTokenAuthManager(String tokenUrl, String clientId, String clientSecret, String scope) {
        this.tokenUrl = tokenUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
        this.grantType = "client_credentials"; // Default for machine-to-machine API testing
    }

    @Override
    public RequestSpecification applyAuthentication(RequestSpecification requestSpec) {
        refreshTokenIfNeeded();
        return requestSpec.header("Authorization", "Bearer " + currentToken);
    }

    /**
     * Synchronized to ensure that if multiple parallel tests hit an expired token,
     * only ONE thread makes the HTTP call to the auth server, while others wait
     * and then use the newly generated token.
     */
    public synchronized void refreshTokenIfNeeded() {
        // We add a 60-second buffer. If the token expires in less than 60 seconds,
        // we refresh it to prevent in-flight request authorization failures.
        long bufferTimeMillis = 60000;

        if (currentToken == null || System.currentTimeMillis() >= (tokenExpiryTime - bufferTimeMillis)) {
            fetchNewToken();
        }
    }

    private void fetchNewToken() {
        // Executing token request without Allure logging to keep test reports clean of auth clutter,
        // unless you specifically want auth calls logged.
        Response response = RestAssured.given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", this.grantType)
                .formParam("client_id", this.clientId)
                .formParam("client_secret", this.clientSecret)
                .formParam("scope", this.scope != null ? this.scope : "")
                .post(this.tokenUrl);

        if (response.statusCode() != 200) {
            throw new AuthenticationException("Failed to generate auth token. Auth Server responded with: "
                    + response.statusCode() + " - " + response.getBody().asString());
        }

        this.currentToken = response.jsonPath().getString("access_token");

        // Extract expiry time (usually in seconds) and convert to epoch milliseconds
        int expiresInSeconds = response.jsonPath().getInt("expires_in");
        this.tokenExpiryTime = System.currentTimeMillis() + (expiresInSeconds * 1000L);
    }
}
