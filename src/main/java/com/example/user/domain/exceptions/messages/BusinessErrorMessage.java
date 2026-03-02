package com.example.user.domain.exceptions.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BusinessErrorMessage {

    // 400 - Bad Request
    USER_ALREADY_EXISTS("User already exists.", HttpStatus.BAD_REQUEST),
    INVALID_USER_DATA("Invalid user data.", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT("Invalid email format.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD("Password must be at least 8 characters long.", HttpStatus.BAD_REQUEST),

    // 404 - Not Found
    USER_NOT_FOUND("User not found.", HttpStatus.NOT_FOUND),

    // 500 - Internal Server Error
    INTERNAL_SERVER_ERROR("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus httpStatus;
}
