package com.example.user.application.useCases;

import com.example.user.application.dto.UpdateUserDTO;
import com.example.user.application.dto.UserResponseDTO;
import com.example.user.domain.exceptions.BusinessException;
import com.example.user.domain.exceptions.messages.BusinessErrorMessage;
import com.example.user.domain.model.User;
import com.example.user.domain.repository.IUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatchUserUseCaseTest {

    @Mock
    private IUserRepository repository;

    @InjectMocks
    private PatchUserUseCase patchUserUseCase;

    @Test
    @DisplayName("Should partially update when data is valid")
    void update_Success() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User(userId, "old_name", "old@mail.com", "pass", new Long[]{1L});
        UpdateUserDTO updateDTO = new UpdateUserDTO("new_name", "new@mail.com", new Long[]{2L});

        when(repository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(repository.existsByEmail("new@mail.com")).thenReturn(false);
        // El save devuelve el usuario ya modificado por el Mapper (que es real)
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserResponseDTO result = patchUserUseCase.update(userId, updateDTO);

        // Assert
        assertThat(result.getUsername()).isEqualTo("new_name");
        assertThat(result.getEmail()).isEqualTo("new@mail.com");
        verify(repository).save(any(User.class));
    }

    @Test
    @DisplayName("Should fail if user does not exist")
    void update_UserNotFound() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class,
                () -> patchUserUseCase.update(1L, new UpdateUserDTO("name", "mail", null)));

        assertThat(ex.getMessage()).isEqualTo(BusinessErrorMessage.USER_NOT_FOUND.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should fail if the new email is already in use by another user")
    void update_EmailAlreadyExists() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User(userId, "jhon", "old@mail.com", "pass", null);
        UpdateUserDTO updateDTO = new UpdateUserDTO(null, "busy@mail.com", null);

        when(repository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(repository.existsByEmail("busy@mail.com")).thenReturn(true);

        // Act & Assert
        assertThrows(BusinessException.class, () -> patchUserUseCase.update(userId, updateDTO));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("No debe validar email si el email del DTO es igual al actual")
    void update_NoEmailChange() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User(userId, "jhon", "same@mail.com", "pass", null);
        UpdateUserDTO updateDTO = new UpdateUserDTO("new_name", "same@mail.com", null);

        when(repository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(repository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        patchUserUseCase.update(userId, updateDTO);

        // Assert
        // Verify that existsByEmail was NEVER called because the email did not change
        verify(repository, never()).existsByEmail(anyString());
        verify(repository).save(any());
    }
}