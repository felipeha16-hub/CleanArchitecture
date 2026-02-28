package com.example.user.application.useCases;

import com.example.user.application.dto.CreateUserDTO;
import com.example.user.application.dto.UserResponseDTO;
import com.example.user.domain.exceptions.BusinessException;
import com.example.user.domain.exceptions.messages.BusinessErrorMessage;
import com.example.user.domain.model.User;
import com.example.user.domain.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(MockitoExtension.class)
public class CreateUserUseCaseTest {

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    @Mock
    private IUserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private CreateUserDTO createUserDTO;
    private User userSaved;

    @BeforeEach
    void setUp() {
        createUserDTO = new CreateUserDTO("jhon", "jhon@example.com", "password123", new Long[]{1L, 2L, 3L});

        userSaved = new User(1L, "jhon", "jhon@example.com", "hashedPassword", new Long[]{1L, 2L, 3L});
    }

    @Test
    @DisplayName("Debe crear un usuario exitosamente cuando los datos son válidos")
    void testCreateUserSuccess() {
        // Arrange
        when(repository.existsByEmail(createUserDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(createUserDTO.getPassword())).thenReturn("hashedPassword");
        when(repository.save(any(User.class))).thenReturn(userSaved);

        // Act
        UserResponseDTO result = createUserUseCase.create(createUserDTO);

        // Assert - Usamos AssertJ para mayor claridad en objetos sincrónicos
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(createUserDTO.getEmail());
        assertThat(result.getPokemonsIds()).containsExactly(1L, 2L, 3L);

        // Verificamos que se llamó al repo una vez
        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe fallar cuando el email ya existe")
    void testCreateUserFailure_EmailExists() {
        // Arrange
        when(repository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> createUserUseCase.create(createUserDTO));

        assertThat(exception.getMessage()).isEqualTo(BusinessErrorMessage.USER_ALREADY_EXISTS.getMessage());

        // Verificación Senior: Si el email existe, NO se debe hashear ni guardar nada
        verify(passwordEncoder, never()).encode(any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Debe fallar cuando la contraseña es muy corta o nula")
    void testCreateUser_InvalidPassword() {
        // Arrange
        CreateUserDTO dtoShortPass = new CreateUserDTO("jhon", "jhon@example.com", "123", new Long[]{1L});
        when(repository.existsByEmail(anyString())).thenReturn(false);

        // Act & Assert
        assertThrows(BusinessException.class, () -> createUserUseCase.create(dtoShortPass));

        verify(repository, never()).save(any());
    }
}
