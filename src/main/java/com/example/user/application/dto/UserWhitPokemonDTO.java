package com.example.user.application.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Schema(name = "UserWhitPokemonDTO", description = "Response DTO that is returned to the client and list of Pokemons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWhitPokemonDTO {

    @Schema(example = "1", description = "ID User")
    private Long id;

    @Schema(example = "juan", description = "User name")
    private String username;

    @Schema(example = "juan@example.com", description = "email")
    private String email;

    @Schema(example = """
            [ { "id": 1, "name": "Bulbasaur" },
                { "id": 4, "name": "Charmander" },
                { "id": 7, "name": "Squirtle" }
              ]""", description = "Arrays of Pokemons with id and name")
    private List<PokemonResponseDTO> pokemons;
}
