package com.example.user.infrastructure.Integration;


import com.example.user.application.dto.CreateUserDTO;
import com.example.user.application.dto.UserResponseDTO;
import com.example.user.application.useCases.CreateUserUseCase;
import com.example.user.infrastructure.persistence.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class CreateUserUseCaseIntegrationTest {

    @Autowired
    private CreateUserUseCase createUserUseCase;

    @Autowired
    private JpaUserRepository jpaUserRepository;


    @BeforeEach
    void setUp() {
        jpaUserRepository.deleteAll();
    }

    @Test
    @DisplayName("integration succes:  UseCase -> Adapter -> DB")
    void shouldCreateUser() {

        CreateUserDTO userRequest = new CreateUserDTO( "Red", "red@pallet.com", "password123", new Long[]{1L, 2L});

        UserResponseDTO savedUser = createUserUseCase.create(userRequest);

        // Validamos la respuesta del dominio
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("Red");

        // Verificación de Infraestructura: ¿Está realmente en PostgreSQL?
        var userInDb = jpaUserRepository.findById(savedUser.getId());
        assertThat(userInDb).isPresent();
        assertThat(userInDb.get().getPokemonsIds()).containsExactly(1L, 2L);
        assertThat(userInDb.get().getEmail()).isEqualTo("red@pallet.com");




    }




}
