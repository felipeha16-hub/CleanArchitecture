package com.example.user.infrastructure.error_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuestas de error estandarizadas
 *
 * Ejemplo de respuesta:
 * {
 *   "timestamp": 1771947757845,
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Usuario no encontrado.",
 *   "path": "/api/users/99"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDTO {

    private long timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    /**
     * Constructor simplificado para crear respuestas rápidamente
     */
    public ErrorResponseDTO(int status, String error, String message) {
        this.timestamp = System.currentTimeMillis();
        this.status = status;
        this.error = error;
        this.message = message;
    }
}

