package com.enterprise.api.request;

import com.enterprise.api.auth.AuthManager;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import java.util.Map;

public class RequestBuilder {
    private RequestSpecification requestSpec;

    public RequestBuilder() {
        this.requestSpec = RestAssured.given()
                .log().ifValidationFails();
    }

    public RequestBuilder setBaseUri(String uri) {
        this.requestSpec.baseUri(uri);
        return this;
    }

    public RequestBuilder setAuth(AuthManager authManager) {
        this.requestSpec = authManager.applyAuthentication(this.requestSpec);
        return this;
    }

    public RequestBuilder setHeaders(Map<String, String> headers) {
        this.requestSpec.headers(headers);
        return this;
    }

    public RequestBuilder setQueryParams(Map<String, Object> queryParams) {
        this.requestSpec.queryParams(queryParams);
        return this;
    }

    // Payload: POJO (Flexible Mapper)
    public RequestBuilder setBody(Object pojo, boolean useGson) {
        ObjectMapperType mapper = useGson ? ObjectMapperType.GSON : ObjectMapperType.JACKSON_2;
        this.requestSpec.body(pojo, mapper);
        return this;
    }

    // Payload: String
    public RequestBuilder setBody(String jsonString) {
        this.requestSpec.body(jsonString);
        return this;
    }

    // Payload: File
    public RequestBuilder setBody(File file) {
        this.requestSpec.body(file);
        return this;
    }

    // --- Multipart / Document Upload ---
    public RequestBuilder addMultiPart(File file) {
        this.requestSpec.multiPart(file);
        return this;
    }

    public RequestBuilder addMultiPart(String controlName, File file) {
        this.requestSpec.multiPart(controlName, file);
        return this;
    }

    public RequestBuilder addMultiPart(String controlName, String content, String mimeType) {
        this.requestSpec.multiPart(controlName, content, mimeType);
        return this;
    }

    // --- Enterprise Specific Overrides ---
    /**
     * Bypasses strict SSL validation (useful for internal/QA servers with self-signed certs)
     */
    public RequestBuilder relaxHttpsValidation() {
        this.requestSpec.relaxedHTTPSValidation();
        return this;
    }

    /**
     * Overrides the default connection and socket timeouts.
     */
    public RequestBuilder setTimeout(int timeoutMillis) {
        RestAssuredConfig config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", timeoutMillis)
                        .setParam("http.socket.timeout", timeoutMillis));
        this.requestSpec.config(config);
        return this;
    }

    public RequestSpecification build() {
        return this.requestSpec;
    }
}