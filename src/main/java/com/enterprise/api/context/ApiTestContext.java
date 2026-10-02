package com.enterprise.api.context;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiTestContext {
    private static final ThreadLocal<RequestSpecification> REQUEST_SPEC = new ThreadLocal<>();
    private static final ThreadLocal<Response> RESPONSE = new ThreadLocal<>();

    public static void setRequest(RequestSpecification spec) { REQUEST_SPEC.set(spec); }
    public static RequestSpecification getRequest() { return REQUEST_SPEC.get(); }

    public static void setResponse(Response res) { RESPONSE.set(res); }
    public static Response getResponse() { return RESPONSE.get(); }

    public static void cleanUp() {
        REQUEST_SPEC.remove();
        RESPONSE.remove();
    }
}