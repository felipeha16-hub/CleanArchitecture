package com.example.user.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to load environment variables from the .env file
 * Runs automatically when the application starts
 */
@Configuration
public class DotEnvConfig {

    public DotEnvConfig() {
        // Load variables from the .env file
        // If .env doesn't exist, just use system environment variables
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing() // Doesn't fail if .env doesn't exist
                .load();

        // Copy all variables from .env to system environment variables
        // This allows Spring Boot to access them via ${VAR_NAME}
        dotenv.entries().forEach(entry -> {
            if (System.getenv(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        });
    }
}

