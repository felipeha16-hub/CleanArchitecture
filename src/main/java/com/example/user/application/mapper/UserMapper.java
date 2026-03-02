package com.example.user.application.mapper;

import com.example.user.application.dto.CreateUserDTO;
import com.example.user.application.dto.PokemonResponseDTO;
import com.example.user.application.dto.UpdateUserDTO;
import com.example.user.application.dto.UserResponseDTO;
import com.example.user.application.dto.UserWhitPokemonDTO;
import com.example.user.domain.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Component
@AllArgsConstructor
public class UserMapper {


    public static UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPokemonsIds()
        );
    }

    public UserWhitPokemonDTO toUserWithPokemonDTO(User user, List<String> pokemonNames) {
        Long[] ids = user.getPokemonsIds();

        // If there are no ids or names, we return an empty list
        if (ids == null || ids.length == 0) {
            return new UserWhitPokemonDTO(user.getId(), user.getUsername(), user.getEmail(), List.of());
        }

        List<PokemonResponseDTO> pokemons = IntStream.range(0, ids.length)
                .mapToObj(i -> new PokemonResponseDTO(
                        ids[i],
                        (i < pokemonNames.size() && pokemonNames.get(i) != null) ? pokemonNames.get(i) : "unknown"
                ))
                .toList();

        return new UserWhitPokemonDTO(user.getId(), user.getUsername(), user.getEmail(), pokemons);
    }



    public static User toDomain(CreateUserDTO dto) {
        return new User(null, dto.getUsername(), dto.getEmail(), dto.getPassword(), dto.getPokemonsIds());
    }

    public static User toDomain(UpdateUserDTO dto, User existing) {

        if (dto.getUsername() != null) existing.setUsername(dto.getUsername());
        if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
        if (dto.getPokemonsIds() != null) existing.setPokemonsIds(dto.getPokemonsIds());
        return existing;
    }
}
