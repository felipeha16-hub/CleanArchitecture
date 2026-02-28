package com.example.user.domain.exceptions;


import com.example.user.domain.exceptions.messages.BusinessErrorMessage;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final BusinessErrorMessage businessErrorMessage;
    public BusinessException(BusinessErrorMessage businessErrorMessage) {
        super(businessErrorMessage.getMessage());
        this.businessErrorMessage = businessErrorMessage;
    }
}
