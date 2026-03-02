package com.example.user.application.useCases;

import com.example.user.application.dto.CreateUserDTO;
import com.example.user.application.dto.UserResponseDTO;
import com.example.user.application.mapper.UserMapper;
import com.example.user.domain.exceptions.BusinessException;
import com.example.user.domain.exceptions.messages.BusinessErrorMessage;
import com.example.user.domain.model.User;
import com.example.user.domain.repository.IUserRepository;
import lombok.AllArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


/**
 * CreateUserUseCase: Use case to create a new user
 *
 * Validations:
 * - Email must not be duplicated (400 Bad Request)
 * - Password must be at least 8 characters (400 Bad Request)
 * - Email must be valid (delegated to @Email in DTO)
 */
@AllArgsConstructor
@Component
public class CreateUserUseCase {

    private final IUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO create(CreateUserDTO dto) {

        // 1. Convertir DTO a dominio
        User user = UserMapper.toDomain(dto);

        // 2. Validate that the email does not exist
        if (repository.existsByEmail(user.getEmail())) {
            throw new BusinessException(BusinessErrorMessage.USER_ALREADY_EXISTS);
        }

        // 3. Validate that the password is secure
        if (user.getPassword() == null || user.getPassword().length() < 8) {
            throw new BusinessException(BusinessErrorMessage.INVALID_PASSWORD);
        }

        // 4. Hash the password before persisting
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 5. Save to repository
        User savedUser = repository.save(user);

        // 6. Retornar DTO de respuesta
        return UserMapper.toDTO(savedUser);
    }
}
