package com.example.base.user.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "UpdateUserDTO", description = "DTO to partially update a user (PATCH)")
public class UpdateUserDTO {

    @Schema(example = "juan_updated", description = "user name (opcional)")
    private String username; // null = no change

    @Schema(example = "juan_new@example.com", description = "email (opcional)")
    private String email;    // null = no change
    // password update should be separate (ChangePasswordDTO)

    @Schema(example = "[1, 2]", description = "list of pokemon ids (opcional)")
    private Long[] pokemonsIds;// null = no change

}
