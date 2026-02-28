package com.example.user.application.useCases;


import com.example.user.domain.exceptions.BusinessException;
import com.example.user.domain.exceptions.messages.BusinessErrorMessage;
import com.example.user.domain.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteUserUseCaseTest {

    @Mock
    private IUserRepository repository;

    @InjectMocks
    private DeleteUserUseCase deleteUserUseCase;


    @Test
    public void testDeleteUser_Success() {
       Long userId = 1L;
       when(repository.existsById(userId)).thenReturn(true);


       deleteUserUseCase.delete(userId);

        verify(repository, times(1)).deleteById(userId);
        // Verificación de que no se llamó a nada más en el repo después de borrar
        verifyNoMoreInteractions(repository);


    }

    @Test
    public void testDeleteUser_UserNotFound() {
        Long userId = 1L;
        when(repository.existsById(userId)).thenReturn(false);


        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> deleteUserUseCase.delete(userId));

        assertThat(exception.getMessage()).isEqualTo(BusinessErrorMessage.USER_NOT_FOUND.getMessage());


        verify(repository, never()).deleteById(anyLong());

    }
}
