package com.example.user.infrastructure.Integration;


import com.example.user.application.dto.CreateUserDTO;
import com.example.user.application.useCases.CreateUserUseCase;
import com.example.user.application.useCases.GetUserIdUseCase;
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
public class GetUserIdUseCaseIntegrationTest {

    @Autowired
    JpaUserRepository jpaUserRepository;

    @Autowired
    GetUserIdUseCase getUserIdUseCase;

    @Autowired
    CreateUserUseCase createUserUseCase;


    @BeforeEach
    void setUp() {
        jpaUserRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration Success: Get User By ID (Reactive Flow)")
    void shouldGetUserById() {
        // 1. Arrange
        var userToCreate = new CreateUserDTO("Ash", "ash@mail.com", "password123", new Long[]{1L,2L});
        var createdUser = createUserUseCase.create(userToCreate);

        // 2. Act & Assert (Usando AssertJ para mensajes de error claros)
        var retrievedUser = getUserIdUseCase.getUserById(createdUser.getId()).block();

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getUsername()).isEqualTo("Ash");
        assertThat(retrievedUser.getEmail()).isEqualTo("ash@mail.com");
        assertThat(retrievedUser.getPokemons()).extracting("id").containsExactly(1L, 2L);
    }
}
