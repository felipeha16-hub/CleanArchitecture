package com.example.base.user.application.useCases;

import com.example.base.user.domain.exceptions.BusinessException;
import com.example.base.user.domain.exceptions.messages.BusinessErrorMessage;
import com.example.base.user.domain.repository.IUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class DeleteUserUseCase {

    private final IUserRepository repository;


    public void delete(Long userId) {


        // Verificar que el usuario existe
        if (!repository.existsById(userId)) {
            throw new BusinessException(BusinessErrorMessage.USER_NOT_FOUND);
        }

        // Proceder a eliminarlo
        repository.deleteById(userId);
    }
}


