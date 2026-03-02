package com.example.user.infrastructure.Integration;

import com.example.user.application.dto.CreateUserDTO;
import com.example.user.application.dto.UpdateUserDTO;
import com.example.user.application.useCases.CreateUserUseCase;
import com.example.user.application.useCases.PatchUserUseCase;
import com.example.user.domain.exceptions.BusinessException;
import com.example.user.infrastructure.persistence.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class PatchUserUseCaseIntegrationTest {

    @Autowired
    private PatchUserUseCase patchUserUseCase;

    @Autowired
    private CreateUserUseCase createUserUseCase;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @BeforeEach
    void setUp() {
        jpaUserRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration: Should update user fields partially")
    void shouldUpdateUserSuccessfully() {
        // 1. Arrange: Usuario inicial
        var created = createUserUseCase.create(new CreateUserDTO("Original", "old@mail.com", "Password123", new Long[]{1L}));

        // DTO con solo los campos a cambiar (Patch)
        UpdateUserDTO updateDTO = new UpdateUserDTO();
        updateDTO.setUsername("Updated Name");
        updateDTO.setEmail("new@mail.com");

        // 2. Act
        var response = patchUserUseCase.update(created.getId(), updateDTO);

        // 3. Assert
        assertThat(response.getUsername()).isEqualTo("Updated Name");
        assertThat(response.getEmail()).isEqualTo("new@mail.com");

        // Verify that it was really saved in the DB
        var dbUser = jpaUserRepository.findById(created.getId()).get();
        assertThat(dbUser.getUsername()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("Integration: Should throw exception when updating to an existing email")
    void shouldFailWhenEmailAlreadyExists() {
        // 1. Arrange: Dos usuarios existentes
        createUserUseCase.create(new CreateUserDTO("User1", "email1@mail.com", "Password123", new Long[]{}));
        var user2 = createUserUseCase.create(new CreateUserDTO("User2", "email2@mail.com", "PassWord123", new Long[]{}));

        // Intentar actualizar User2 con el email de User1
        UpdateUserDTO conflictDTO = new UpdateUserDTO();
        conflictDTO.setEmail("email1@mail.com");

        // 2. Act & Assert
        assertThatThrownBy(() -> patchUserUseCase.update(user2.getId(), conflictDTO))
                .isInstanceOf(BusinessException.class);
    }
}