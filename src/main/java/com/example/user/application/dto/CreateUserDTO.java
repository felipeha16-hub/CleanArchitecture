package com.example.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Schema(name = "CreateUserDTO", description = "DTO to create a user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {

    @NotBlank
    @Schema(example = "juan", description = "User name")
    private String username;

    @Email
    @NotBlank
    @Schema(example = "juan@example.com", description = "email")
    private String email;

    @NotBlank
    @Schema(example = "P4ssw0rd!", description = "password (minimum 8 characters)")
    private String password;

    @NotNull
    @Schema(example = "[1,2,3,4]", description = "Arrays of Pokemons")
    private Long[] pokemonsIds;

}
