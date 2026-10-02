package com.enterprise.api.payload;

import com.enterprise.api.exceptions.PayloadException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class PayloadManager {
    /**
     * Converts a String payload to a temporary JSON file at runtime.
     * Useful for bypassing memory constraints on massive payloads.
     */
    public static File createTempJsonFileFromString(String jsonPayload) {
        try {
            Path tempFile = Files.createTempFile("api-payload-", ".json");
            Files.writeString(tempFile, jsonPayload);
            tempFile.toFile().deleteOnExit();
            return tempFile.toFile();
        } catch (Exception e) {
            throw new PayloadException("Failed to create temporary JSON file", e);
        }
    }
}