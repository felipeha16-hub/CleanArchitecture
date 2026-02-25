package com.example.base.user.domain.exceptions.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BusinessErrorMessage {

    // 400 - Bad Request
    USER_ALREADY_EXISTS("El usuario ya existe.", HttpStatus.BAD_REQUEST),
    INVALID_USER_DATA("Datos de usuario inválidos.", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT("Formato de email inválido.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD("La contraseña debe tener al menos 8 caracteres.", HttpStatus.BAD_REQUEST),

    // 404 - Not Found
    USER_NOT_FOUND("Usuario no encontrado.", HttpStatus.NOT_FOUND),

    // 500 - Internal Server Error
    INTERNAL_SERVER_ERROR("Error interno del servidor.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus httpStatus;
}
