package com.example.base.user.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserResponseDTO", description = "Response DTO that is returned to the client")
public class UserResponseDTO {

    @Schema(example = "1", description = "ID User")
    private Long id;

    @Schema(example = "juan", description = "User name")
    private String username;

    @Schema(example = "juan@example.com", description = "email")
    private String email;

    @Schema(example = "[1, 2,3,4]", description = "List of Pokemon IDs associated with the user")
    private Long[] pokemonsIds;

}
