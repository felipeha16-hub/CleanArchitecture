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
 * CreateUserUseCase: Caso de uso para crear un nuevo usuario
 *
 * Validaciones:
 * - El email no debe estar duplicado (400 Bad Request)
 * - La contraseña debe tener al menos 8 caracteres (400 Bad Request)
 * - El email debe ser válido (delegado a @Email en DTO)
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
