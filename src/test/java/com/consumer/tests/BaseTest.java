package com.consumer.tests;

import com.enterprise.api.config.ConfigManager;
import com.enterprise.api.logging.ApiLogger;
import org.testng.annotations.BeforeSuite;

public class BaseTest {
    @BeforeSuite
    public void globalSetup() {
        ApiLogger.logInfo("Initializing Framework Configuration...");
        ConfigManager.init("src/test/resources/env-qa.json");
    }
}