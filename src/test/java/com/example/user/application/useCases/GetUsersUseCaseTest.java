package com.example.user.application.useCases;


import com.example.user.application.dto.UserResponseDTO;
import com.example.user.domain.model.User;
import com.example.user.domain.repository.IUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetUsersUseCaseTest {

    @Mock
    private IUserRepository repository;


    @InjectMocks
    private GetUsersUseCase getUsersUseCase;

    @Test
    @DisplayName("Debe retornar una lista de UserResponseDTO cuando existen usuarios")
    public void testGetAllUsers_Success() {

        User user = new User(1L, "jhon", "jhon@example.com", "password123", new Long[]{1L, 2L, 3L});
        UserResponseDTO expectedDto = new UserResponseDTO(1L, "jhon", "jhon@example.com", new Long[]{1L, 2L, 3L});

        when(repository.findAll()).thenReturn(List.of(user));



        List<UserResponseDTO> result = getUsersUseCase.getAllUsers();


        assertThat(result).isNotEmpty().hasSize(1);

        UserResponseDTO actual = result.get(0);
        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getUsername()).isEqualTo("jhon");
        assertThat(actual.getEmail()).isEqualTo("jhon@example.com");
        assertThat(actual.getPokemonsIds()).containsExactly(1L, 2L, 3L);


        verify(repository).findAll();

    }

    @Test
    @DisplayName("Debe retornar una lista vacía cuando no hay usuarios")
    public void testGetAllUsers_Empty() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of());

        // Act
        List<UserResponseDTO> result = getUsersUseCase.getAllUsers();

        // Assert
        assertThat(result).isEmpty();
        verify(repository).findAll();
    }
}
