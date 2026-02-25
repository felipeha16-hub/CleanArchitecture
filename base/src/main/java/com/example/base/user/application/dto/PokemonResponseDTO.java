package com.example.base.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PokemonResponseDTO {
    @Schema(example = "1", description = "Pokemon ID")
    private Long id;

    @Schema(example = "Bulbasaur", description = "Pokemon name")
    private String name;

}