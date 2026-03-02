package com.example.user.configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public io.swagger.v3.oas.models.OpenAPI springShopOpenAPI() {
        return new io.swagger.v3.oas.models.OpenAPI()
                .info(new Info().title("User API")
                        .description("Example API for user management")
                        .version("v0.0.1"))
                .externalDocs(new ExternalDocumentation()
                        .description("Base project")
                        .url("https://example.com"));
    }

    /**
     * Configuration to document only the API controllers
     * Without excluding packages, we only document those we want to expose
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("users-api")
                .packagesToScan("com.example.user.infrastructure.controller")
                .pathsToMatch("/api/**")
                .build();
    }
}
