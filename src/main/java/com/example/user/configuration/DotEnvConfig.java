package com.example.user.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para cargar variables de entorno desde el archivo .env
 * Se ejecuta automáticamente al iniciar la aplicación
 */
@Configuration
public class DotEnvConfig {

    public DotEnvConfig() {
        // Cargar variables del archivo .env
        // Si no existe .env, simplemente usa las variables de entorno del sistema
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing() // No falla si .env no existe
                .load();

        // Copiar todas las variables de .env a las variables de entorno del sistema
        // Esto permite que Spring Boot las acceda via ${VAR_NAME}
        dotenv.entries().forEach(entry -> {
            if (System.getenv(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        });
    }
}

