package com.enterprise.api.response;

import io.restassured.mapper.ObjectMapperType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.io.File;

public class ResponseValidator {
    private final Response response;

    public ResponseValidator(Response response) {
        this.response = response;
    }

    // --- Assertions ---
    public ResponseValidator assertStatusCode(int expectedCode) {
        response.then().statusCode(expectedCode);
        return this;
    }

    public ResponseValidator validateSchema(File schemaFile) {
        response.then().body(JsonSchemaValidator.matchesJsonSchema(schemaFile));
        return this;
    }

    // --- JsonPath Extraction ---
    public String extractString(String jsonPath) {
        return response.jsonPath().getString(jsonPath);
    }

    public int extractInt(String jsonPath) {
        return response.jsonPath().getInt(jsonPath);
    }

    public JsonPath getJsonPathObject() {
        return response.jsonPath();
    }

    // --- POJO Deserialization ---
    public <T> T deserializeToPojo(Class<T> responseClass, boolean useGson) {
        ObjectMapperType mapper = useGson ? ObjectMapperType.GSON : ObjectMapperType.JACKSON_2;
        return response.as(responseClass, mapper);
    }

    public Response getRawResponse() {
        return this.response;
    }
}