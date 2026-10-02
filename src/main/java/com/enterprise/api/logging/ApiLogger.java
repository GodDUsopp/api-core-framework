package com.enterprise.api.logging;

import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiLogger {
    private static final Logger log = LoggerFactory.getLogger(ApiLogger.class);
    private static final Slf4jPrintStream slf4jPrintStream = new Slf4jPrintStream(log);

    // General logging methods for teams
    public static void logInfo(String message) { log.info(message); }
    public static void logError(String message, Throwable t) { log.error(message, t); }
    public static void logWarn(String message) { log.warn(message); }

    /**
     * Attaches SLF4J logging to the Request. Only the requests passed here will be logged.
     */
    public static RequestSpecification logRequest(RequestSpecification requestSpec) {
        return requestSpec.config(RestAssuredConfig.config()
                        .logConfig(LogConfig.logConfig().defaultStream(slf4jPrintStream)))
                .log().all();
    }

    /**
     * Manually logs the details of the Response payload and headers on demand.
     */
    public static void logResponse(Response response) {
        log.info("========== API RESPONSE ==========");
        log.info("Status: {} - {}", response.getStatusCode(), response.getStatusLine());
        log.info("Headers: \n{}", response.getHeaders());
        if (response.getBody() != null) {
            log.info("Body: \n{}", response.getBody().asPrettyString());
        }
        log.info("==================================");
    }
}