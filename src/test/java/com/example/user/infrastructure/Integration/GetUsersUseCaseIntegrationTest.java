package com.example.user.infrastructure.Integration;

import com.example.user.application.dto.CreateUserDTO;
import com.example.user.application.useCases.CreateUserUseCase;
import com.example.user.application.useCases.GetUsersUseCase;
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
class GetUsersUseCaseIntegrationTest {

    @Autowired
    private GetUsersUseCase getUsersUseCase;

    @Autowired
    private CreateUserUseCase createUserUseCase;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @BeforeEach
    void setUp() {
        jpaUserRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration: Should retrieve all users as DTOs")
    void shouldGetAllUsers() {
        // 1. Arrange: Creamos un par de usuarios
        createUserUseCase.create(new CreateUserDTO("User1", "u1@mail.com", "password123", new Long[]{1L}));
        createUserUseCase.create(new CreateUserDTO("User2", "u2@mail.com", "password123", new Long[]{2L}));

        // 2. Act
        var result = getUsersUseCase.getAllUsers();

        // 3. Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting("username").containsExactlyInAnyOrder("User1", "User2");
    }
}