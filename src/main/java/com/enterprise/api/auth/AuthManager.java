package com.enterprise.api.auth;

import io.restassured.specification.RequestSpecification;

public interface AuthManager {
    RequestSpecification applyAuthentication(RequestSpecification requestSpec);
}