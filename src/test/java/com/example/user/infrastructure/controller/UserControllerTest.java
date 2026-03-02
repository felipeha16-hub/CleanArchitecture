package com.example.user.infrastructure.controller;


import com.example.user.application.dto.*;
import com.example.user.application.useCases.*;
import com.example.user.domain.exceptions.BusinessException;
import com.example.user.domain.exceptions.messages.BusinessErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GetUserIdUseCase getUserIdUseCase;

    @MockitoBean
    private CreateUserUseCase createUserUseCase;

    @MockitoBean
    private GetUsersUseCase getUsersUseCase;

    @MockitoBean
    private DeleteUserUseCase deleteUserUseCase;

    @MockitoBean
    private PatchUserUseCase patchUserUseCase;

    @Test
    @DisplayName("GET /api/users/{id} - Success")
    void getUserById_Returns200() {
        // Arrange
        Long userId = 1L;
        List<PokemonResponseDTO> pokemons = Collections.emptyList();
        UserWhitPokemonDTO response = new UserWhitPokemonDTO(userId, "Ash", "ash@pk.com", pokemons);

        when(getUserIdUseCase.getUserById(userId)).thenReturn(Mono.just(response));

        // Act & Assert
        webTestClient.get()
                .uri("/api/users/{id}", userId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.username").isEqualTo("Ash")
                .jsonPath("$.email").isEqualTo("ash@pk.com")
                .jsonPath("$.pokemons").isArray();

    }

    @Test
    @DisplayName("POST /api/users - Success")
    void createUser_Returns201() {
        // Arrange
        CreateUserDTO request = new CreateUserDTO("Jhon", "jhon@mail.com", "password123", new Long[]{1L});
        UserResponseDTO response = new UserResponseDTO(1L, "Jhon", "jhon@mail.com", new Long[]{1L});

        when(createUserUseCase.create(any(CreateUserDTO.class))).thenReturn(response);

        // Act & Assert
        webTestClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.username").isEqualTo("Jhon")
                .jsonPath("$.password123").doesNotExist();
    }

    @Test
    @DisplayName("GET /api/users - Success")
    void getAllUsers_Returns200() {
        // Arrange
        UserResponseDTO user = new UserResponseDTO(1L, "Jhon", "jhon@mail.com", new Long[]{1L});

        when(getUsersUseCase.getAllUsers()).thenReturn(List.of(user));

        // Act & Assert
        webTestClient.get()
                .uri("/api/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].username").isEqualTo("Jhon");

    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Success")
    void deleteUser_Returns204() {
        // Arrange
        Long userId = 1L;

        // Act & Assert
        webTestClient.delete()
                .uri("/api/users/{id}", userId)
                .exchange()
                .expectStatus().isNoContent();

        verify(deleteUserUseCase).delete(userId);
    }


    @Test
    @DisplayName("PATCH /api/users/{id} - Success")
    void patchUser_Returns200() {
        // Arrange
        Long userId = 1L;
        UserResponseDTO response = new UserResponseDTO(userId, "Jhon Updated", "jhon_update@mail.com", new Long[]{1L, 2L});

        when(patchUserUseCase.update(any(Long.class), any(UpdateUserDTO.class))).thenReturn(response);


        // Act & Assert
        webTestClient.patch()
                .uri("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateUserDTO("Jhon Updated", "jhon_update@mail.com", new Long[]{1L, 2L}))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.username").isEqualTo("Jhon Updated")
                .jsonPath("$.email").isEqualTo("jhon_update@mail.com")
                .jsonPath("$.pokemonsIds").isArray()
                .jsonPath("$.pokemonsIds[0]").isEqualTo(1)
                .jsonPath("$.pokemonsIds[1]").isEqualTo(2);

        verify(patchUserUseCase).update(eq(userId), any(UpdateUserDTO.class));

    }

    @Test
    @DisplayName("GET /api/users/{id} - Not Found (404)")
    void getUserById_NotFound_Returns404() {
        Long userId = 99L;

        // Arrange
        when(getUserIdUseCase.getUserById(userId))
                .thenReturn(Mono.error(new BusinessException(BusinessErrorMessage.USER_NOT_FOUND)));

        // Act & Assert
        webTestClient.get()
                .uri("/api/users/{id}", userId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo(BusinessErrorMessage.USER_NOT_FOUND.getMessage())
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    @DisplayName("POST /api/users - Conflict - Email Exists 400")
    void createUser_EmailConflict_ReturnsError() {
        CreateUserDTO request = new CreateUserDTO("Jhon", "exists@mail.com", "password123", new Long[]{1L});


        when(createUserUseCase.create(any(CreateUserDTO.class)))
                .thenThrow(new BusinessException(BusinessErrorMessage.USER_ALREADY_EXISTS));

        webTestClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo(BusinessErrorMessage.USER_ALREADY_EXISTS.getMessage());
    }

    @Test
    @DisplayName("POST /api/users - Bad Request - Invalid Password (400)")
    void createUser_InvalidPassword_Returns400() {
        CreateUserDTO request = new CreateUserDTO("Jhon", "jhon@mail.com", "123", new Long[]{1L});

        when(createUserUseCase.create(any(CreateUserDTO.class)))
                .thenThrow(new BusinessException(BusinessErrorMessage.INVALID_PASSWORD));

        webTestClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo(BusinessErrorMessage.INVALID_PASSWORD.getMessage());
    }

}