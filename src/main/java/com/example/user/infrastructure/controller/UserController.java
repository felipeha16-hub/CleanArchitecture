package com.example.user.infrastructure.controller;

import com.example.user.application.dto.CreateUserDTO;
import com.example.user.application.dto.UpdateUserDTO;
import com.example.user.application.dto.UserResponseDTO;
import com.example.user.application.dto.UserWhitPokemonDTO;
import com.example.user.application.useCases.*;
import jakarta.validation.*;
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
@Tag(name = "Users", description = "User and associated Pokémon management")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUsersUseCase getUsersUseCase;
    private final GetUserIdUseCase getUserByIdUseCase;
    private final PatchUserUseCase patchUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    @GetMapping
    @Operation(
            summary = "Get all users",
            description = "Returns the complete list of users registered in the system"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of users obtained successfully"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
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
            summary = "Get user by ID",
            description = "Get data for a specific user along with their associated Pokémon"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found (ID does not exist)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "user_not_found",
                                    value = "{\"timestamp\":1771947757845,\"status\":404,\"error\":\"Not Found\",\"message\":\"User not found.\",\"path\":\"/api/users/999\"}"
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
            description = "Create a new user with email, username, password, and Pokemon IDs"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data or email already exists (duplicate user)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "duplicate_user",
                                    value = "{\"timestamp\":1771947757845,\"status\":400,\"error\":\"Bad Request\",\"message\":\"User already exists.\",\"path\":\"/api/users\"}"
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
            summary = "Update user",
            description = "Partially update user data (email, username, password, Pokemon IDs)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data or email already exists in another user",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "duplicate_user",
                                    value = "{\"timestamp\":1771947757845,\"status\":400,\"error\":\"Bad Request\",\"message\":\"User already exists.\",\"path\":\"/api/users/1\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "user_not_found",
                                    value = "{\"timestamp\":1771947757845,\"status\":404,\"error\":\"Not Found\",\"message\":\"User not found.\",\"path\":\"/api/users/999\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
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
            summary = "Delete user",
            description = "Permanently delete a user and all associated data"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully (no content)"),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "user_not_found",
                                    value = "{\"timestamp\":1771947757845,\"status\":404,\"error\":\"Not Found\",\"message\":\"User not found.\",\"path\":\"/api/users/999\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
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
