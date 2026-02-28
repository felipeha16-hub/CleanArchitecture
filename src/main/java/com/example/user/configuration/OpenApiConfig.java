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
                        .description("API de ejemplo para manejo de usuarios")
                        .version("v0.0.1"))
                .externalDocs(new ExternalDocumentation()
                        .description("Proyecto base")
                        .url("https://example.com"));
    }

    /**
     * Configuración para documentar solo los controladores de la API
     * Sin excluir paquetes, solo documentamos los que nos interesa exponer
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
