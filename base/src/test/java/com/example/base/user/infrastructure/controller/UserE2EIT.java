package com.example.base.user.infrastructure.controller;

import com.example.base.user.application.dto.CreateUserDTO;
import com.example.base.user.application.dto.UpdateUserDTO;
import com.example.base.user.application.dto.UserResponseDTO;
import com.example.base.user.application.dto.UserWhitPokemonDTO;
import com.example.base.user.infrastructure.persistence.JpaUserRepository;
import com.example.base.user.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserE2EIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @BeforeEach
    void setUp() {
        jpaUserRepository.deleteAll();
    }

    @Test
    @DisplayName("E2E: Escenario completo de Usuario")
    void userFullLifecycleE2E() {
        // --- 1. POST: Crear Usuario ---
        CreateUserDTO createRequest = new CreateUserDTO("Brock", "brock@pewter.com", "password", new Long[]{1L, 4L});

        ResponseEntity<UserResponseDTO> createResponse = restTemplate.postForEntity(
                "/api/users",
                createRequest,
                UserResponseDTO.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long userId = createResponse.getBody().getId();
        assertThat(userId).isNotNull();

        // --- 2. GET: Obtener por ID (Flujo Reactivo en el Controller) ---

        ResponseEntity<UserWhitPokemonDTO> getResponse = restTemplate.getForEntity(
                "/api/users/" + userId,
                UserWhitPokemonDTO.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getUsername()).isEqualTo("Brock");
        assertThat(getResponse.getBody().getEmail()).isEqualTo("brock@pewter.com");
        assertThat(getResponse.getBody().getPokemons()).extracting("id").containsExactly(1L,4L);
        assertThat(getResponse.getBody().getPokemons()).extracting("name").containsExactly("bulbasaur", "charmander");


        // --- 3. DELETE: Eliminar ---
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/users/" + userId,
                org.springframework.http.HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // --- 4. VERIFICACIÓN: ¿Realmente se borró? ---
        ResponseEntity<String> getAfterDelete = restTemplate.getForEntity(
                "/api/users/" + userId,
                String.class
        );

        //debería devolver 404
        assertThat(getAfterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getAfterDelete.getBody()).contains("Usuario no encontrado");
    }

    @Test
    @DisplayName("E2E: GET All - Debe retornar lista de usuarios")
    void shouldReturnListOfUsers() {
        // 1. Arrange: Insertar 2 usuarios directamente vía repositorio o UseCase
        CreateUserDTO createRequest = new CreateUserDTO("Brock", "brock@pewter.com", "password123", new Long[]{1L, 4L});
        CreateUserDTO createRequest2 = new CreateUserDTO("Brock2", "brock2@pewter.com", "password123", new Long[]{2L, 3L});



        ResponseEntity<UserResponseDTO> createResponse = restTemplate.postForEntity("/api/users", createRequest, UserResponseDTO.class);
        ResponseEntity<UserResponseDTO> createResponse2 = restTemplate.postForEntity("/api/users", createRequest2, UserResponseDTO.class);



        // 2. Act
        ResponseEntity<List> response = restTemplate.getForEntity("/api/users", List.class);

        // 3. Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2); // Depende de cuántos creaste
    }

    @Test
    @DisplayName("E2E: POST 400 - Error al crear email duplicado")
    void shouldReturn400WhenEmailExists() {
        // 1. Arrange: Crear un usuario previo
        CreateUserDTO user = new CreateUserDTO("Original", "duplicate@mail.com", "password123", new Long[]{1L});
        restTemplate.postForEntity("/api/users", user, String.class);

        // 2. Act: Intentar crear otro con el mismo email
        ResponseEntity<String> response = restTemplate.postForEntity("/api/users", user, String.class);

        // 3. Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("El usuario ya existe");
    }

    @Test
    @DisplayName("E2E: Patch 200 - Actualizar parcialmente un usuario")
    void shouldPartiallyUpdateUser() {
        // 1. Arrange: Crear un usuario previo
        CreateUserDTO createRequest = new CreateUserDTO("Original", "original@mail.com", "password123", new Long[]{1L});
        ResponseEntity<UserResponseDTO> createResponse = restTemplate.postForEntity("/api/users", createRequest, UserResponseDTO.class);
        Long userId = createResponse.getBody().getId();

        // DTO de actualización parcial (solo el nombre)
        UpdateUserDTO updateUser = new UpdateUserDTO("OriginalUpdated", null, null);

        // 2. Act: Enviar PATCH
        ResponseEntity<UserResponseDTO> patchResponse = restTemplate.exchange(
                "/api/users/" + userId,
                org.springframework.http.HttpMethod.PATCH,
                new org.springframework.http.HttpEntity<>(updateUser, org.springframework.http.HttpHeaders.EMPTY),
                UserResponseDTO.class
        );

        // 3. Assert
        assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(patchResponse.getBody().getUsername()).isEqualTo("OriginalUpdated");
        assertThat(patchResponse.getBody().getEmail()).isEqualTo("original@mail.com"); // El email no debería cambiar


    }

    @Test
    @DisplayName("E2E: POST 400 - Error de validación por datos inválidos")
    void shouldReturn400WhenDataIsInvalid() {
        // 1. Arrange: Enviamos un email con formato incorrecto y un username vacío
        // (Ajusta los campos según las anotaciones @NotBlank, @Email, etc., que tengas en tu DTO)
        CreateUserDTO invalidRequest = new CreateUserDTO("", "esto-no-es-un-email", "123", new Long[]{});

        // 2. Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/users",
                invalidRequest,
                String.class
        );

        // 3. Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }
}