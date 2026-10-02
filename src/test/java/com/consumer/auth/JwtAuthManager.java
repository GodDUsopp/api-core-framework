package com.consumer.auth;

import com.enterprise.api.auth.AuthManager;
import io.restassured.specification.RequestSpecification;

public class JwtAuthManager implements AuthManager {
    private final String token;

    public JwtAuthManager(String token) {
        this.token = token;
    }

    @Override
    public RequestSpecification applyAuthentication(RequestSpecification requestSpec) {
        // Appends the JWT token to the request header automatically
        return requestSpec.header("Authorization", "Bearer " + token);
    }
}