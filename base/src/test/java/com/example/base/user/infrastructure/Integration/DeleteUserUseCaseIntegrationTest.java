package com.example.base.user.infrastructure.Integration;


import com.example.base.user.application.dto.CreateUserDTO;
import com.example.base.user.application.dto.UserResponseDTO;
import com.example.base.user.application.useCases.CreateUserUseCase;
import com.example.base.user.application.useCases.DeleteUserUseCase;
import com.example.base.user.infrastructure.persistence.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class DeleteUserUseCaseIntegrationTest {

    @Autowired
    private CreateUserUseCase createUserUseCase;

    @Autowired
    private DeleteUserUseCase deleteUserUseCase;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    private Long userIdToDelete;

    @BeforeEach
    void setUp() {
        jpaUserRepository.deleteAll();
    }

    @Test
    @DisplayName("integration succes:  UseCase -> Adapter -> DB")
    void  IntegrationdeleteUser() {
        // Crear un usuario para luego eliminarlo
        CreateUserDTO createDTO = new CreateUserDTO("TestUser", "test@mail.com", "password123", new Long[]{1L});
        UserResponseDTO createdUser = createUserUseCase.create(createDTO);
        userIdToDelete = createdUser.getId();

        deleteUserUseCase.delete(userIdToDelete);

        // Verificar que el usuario ya no existe en la base de datos
        boolean exists = jpaUserRepository.existsById(userIdToDelete);
        assertThat(exists).isFalse();


    }

}
