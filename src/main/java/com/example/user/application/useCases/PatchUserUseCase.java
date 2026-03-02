package com.example.user.application.useCases;

import com.example.user.application.dto.UpdateUserDTO;
import com.example.user.application.dto.UserResponseDTO;
import com.example.user.application.mapper.UserMapper;
import com.example.user.domain.exceptions.BusinessException;
import com.example.user.domain.exceptions.messages.BusinessErrorMessage;
import com.example.user.domain.model.User;
import com.example.user.domain.repository.IUserRepository;
import org.springframework.stereotype.Component;

@Component
public class PatchUserUseCase {

    private final IUserRepository repository;

    // Constructor Injection
    public PatchUserUseCase(IUserRepository repository) {
        this.repository = repository;
    }

    public UserResponseDTO update(Long id, UpdateUserDTO dto) {

        // 1.
        User user = repository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessErrorMessage.USER_NOT_FOUND));

        // 2. Validate that the new email does not exist in another user
        // Only validate if the email changed
        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (repository.existsByEmail(dto.getEmail())) {
                throw new BusinessException(BusinessErrorMessage.USER_ALREADY_EXISTS);
            }
        }

        // 3. Aplicar cambios parciales desde dto
        User updated = UserMapper.toDomain(dto, user);

        // 4. Guardar y retornar
        User saved = repository.save(updated);
        return UserMapper.toDTO(saved);
    }
}
