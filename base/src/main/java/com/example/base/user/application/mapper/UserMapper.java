package com.example.base.user.application.mapper;

import com.example.base.user.application.dto.CreateUserDTO;
import com.example.base.user.application.dto.PokemonResponseDTO;
import com.example.base.user.application.dto.UpdateUserDTO;
import com.example.base.user.application.dto.UserResponseDTO;
import com.example.base.user.application.dto.UserWhitPokemonDTO;
import com.example.base.user.domain.model.User;
import com.example.base.user.infrastructure.clients.IPokemonClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
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

        // Si no hay ids o nombres, devolvemos lista vacía
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
        // Aplicar cambios parciales al dominio
        if (dto.getUsername() != null) existing.setUsername(dto.getUsername());
        if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
        if (dto.getPokemonsIds() != null) existing.setPokemonsIds(dto.getPokemonsIds());
        return existing;
    }
}
