package com.example.user.infrastructure.error_management;

import com.example.user.domain.exceptions.BusinessException;
import com.example.user.infrastructure.error_management.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * GlobalExceptionHandler: Maneja excepciones de negocio a nivel global
 *
 * Esto evita que tengas que manejar excepciones en cada controlador
 * y estandariza las respuestas de error.
 *
 * Se activa mediante la propiedad: app.exception-handler.enabled=true
 */

@RestControllerAdvice
@ConditionalOnProperty(name = "app.exception-handler.enabled", havingValue = "true")
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Manejador para excepciones de negocio (BusinessException)
     *
     * Esto intercepta cualquier BusinessException lanzada en:
     * - Use Cases
     * - Services
     * - Controladores
     * - Flujos reactivos (Mono/Flux)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessException(
            BusinessException ex,
            WebRequest request) {

        var errorMessage = ex.getBusinessErrorMessage();
        var status = errorMessage.getHttpStatus();

        // Extraer el path del request
        String path = extractPath(request);

        log.warn("BusinessException capturada: {} - Path: {}", errorMessage.getMessage(), path);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(System.currentTimeMillis())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(errorMessage.getMessage())
                .path(path)
                .build();

        // Retornar respuesta con el status HTTP correcto
        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    /**
     * Manejador para errores de validación (@Valid, @Validated)
     *
     * Captura errores cuando las validaciones del DTO fallan:
     * - @Size, @NotNull, @NotBlank, @Email, @Min, @Max, etc.
     *
     * Retorna 400 Bad Request con el mensaje de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        String path = extractPath(request);

        // Get the first validation error
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation error");

        log.warn("Validation error: {} - Path: {}", errorMessage, path);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(System.currentTimeMillis())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(errorMessage)
                .path(path)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    /**
     * Manejador genérico para excepciones no controladas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception ex,
            WebRequest request) {

        String path = extractPath(request);

        log.error("Excepción no manejada: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .timestamp(System.currentTimeMillis())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Error interno del servidor")
                .path(path)
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    /**
     * Método auxiliar para extraer el path del request
     */
    private String extractPath(WebRequest request) {
        if (request instanceof ServletWebRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getRequest();
            return httpRequest.getRequestURI();
        }
        return "unknown";
    }
}


