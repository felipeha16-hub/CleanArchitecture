package com.example.base.user.infrastructure.controller;

import com.example.base.user.application.dto.CreateUserDTO;
import com.example.base.user.application.dto.UpdateUserDTO;
import com.example.base.user.application.dto.UserResponseDTO;
import com.example.base.user.application.dto.UserWhitPokemonDTO;
import jakarta.validation.*;
import com.example.base.user.application.useCases.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Gestión de usuarios y pokémones asociados")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUsersUseCase getUsersUseCase;
    private final GetUserIdUseCase getUserByIdUseCase;
    private final PatchUserUseCase patchUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    @GetMapping
    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Retorna la lista completa de usuarios registrados en el sistema"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "error_500",
                                    value = "{\"timestamp\":1771947757845,\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Error interno del servidor.\",\"path\":\"/api/users\"}"
                            )
                    )
            )
    })
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = getUsersUseCase.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Obtiene los datos de un usuario específico junto con sus pokémones asociados"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado (ID no existe)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "usuario_no_encontrado",
                                    value = "{\"timestamp\":1771947757845,\"status\":404,\"error\":\"Not Found\",\"message\":\"Usuario no encontrado.\",\"path\":\"/api/users/999\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "error_500",
                                    value = "{\"timestamp\":1771947757845,\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Error interno del servidor.\",\"path\":\"/api/users/1\"}"
                            )
                    )
            )
    })
    public Mono<ResponseEntity<UserWhitPokemonDTO>> getUserById(@PathVariable Long id) {
        return getUserByIdUseCase.getUserById(id)
                .map(ResponseEntity::ok)
                .onErrorMap(error -> error); // Propagar el error para que GlobalExceptionHandler lo maneje
    }

    @PostMapping
    @Operation(
            summary = "Crear nuevo usuario",
            description = "Crea un nuevo usuario con email, username, contraseña e IDs de pokémones"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o email ya existe (usuario duplicado)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "usuario_duplicado",
                                    value = "{\"timestamp\":1771947757845,\"status\":400,\"error\":\"Bad Request\",\"message\":\"El usuario ya existe.\",\"path\":\"/api/users\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "error_500",
                                    value = "{\"timestamp\":1771947757845,\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Error interno del servidor.\",\"path\":\"/api/users\"}"
                            )
                    )
            )
    })
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody CreateUserDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createUserUseCase.create(dto));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza parcialmente los datos de un usuario (email, username, contraseña, IDs de pokémones)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o email ya existe en otro usuario",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "usuario_duplicado",
                                    value = "{\"timestamp\":1771947757845,\"status\":400,\"error\":\"Bad Request\",\"message\":\"El usuario ya existe.\",\"path\":\"/api/users/1\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "usuario_no_encontrado",
                                    value = "{\"timestamp\":1771947757845,\"status\":404,\"error\":\"Not Found\",\"message\":\"Usuario no encontrado.\",\"path\":\"/api/users/999\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "error_500",
                                    value = "{\"timestamp\":1771947757845,\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Error interno del servidor.\",\"path\":\"/api/users/1\"}"
                            )
                    )
            )
    })
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO dto) {
        UserResponseDTO updatedUser = patchUserUseCase.update(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario y todos sus datos asociados de forma permanente"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente (sin contenido)"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "usuario_no_encontrado",
                                    value = "{\"timestamp\":1771947757845,\"status\":404,\"error\":\"Not Found\",\"message\":\"Usuario no encontrado.\",\"path\":\"/api/users/999\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "error_500",
                                    value = "{\"timestamp\":1771947757845,\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Error interno del servidor.\",\"path\":\"/api/users/1\"}"
                            )
                    )
            )
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        deleteUserUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
