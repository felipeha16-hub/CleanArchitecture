package com.example.base.user.application.mapper;

import com.example.base.user.application.dto.CreateUserDTO;
import com.example.base.user.application.dto.UpdateUserDTO;
import com.example.base.user.application.dto.UserResponseDTO;
import com.example.base.user.application.dto.UserWhitPokemonDTO;
import com.example.base.user.domain.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {




    @Test
    void toDTO_ShouldMapUserToUserResponseDTO()  {

        User user = new User(1L, "jhon", "jhon@example.com", "password123", new Long[]{1L, 2L, 3L});


        UserResponseDTO result = UserMapper.toDTO(user);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("jhon");
        assertThat(result.getEmail()).isEqualTo("jhon@example.com");
        assertThat(result.getPokemonsIds()).containsExactly(1L, 2L, 3L);
        }


    @Test
    void shouldMapUserAndPokemonNamesCorrectly(){

        final UserMapper userMapper = new UserMapper();

        User user = new User(1L, "jhon", "jhon@example.com", "password123", new Long[]{25L});
        List<String> pokemonNames = List.of("Pikachu");

        UserWhitPokemonDTO result = userMapper.toUserWithPokemonDTO(user, pokemonNames);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("jhon");
        assertThat(result.getEmail()).isEqualTo("jhon@example.com");
        assertThat(result.getPokemons()).hasSize(1);
        assertThat(result.getPokemons().get(0).getId()).isEqualTo(25L);
        assertThat(result.getPokemons().get(0).getName()).isEqualTo("Pikachu");

    }

    @Test
    void toDomain_ShouldMapCreateUserDTOToUser() {

        CreateUserDTO dto = new CreateUserDTO("jhon", "jhon@example.com", "password123", new Long[]{1L, 2L, 3L});

        User result = UserMapper.toDomain(dto);


        assertThat(result.getUsername()).isEqualTo("jhon");
        assertThat(result.getEmail()).isEqualTo("jhon@example.com");
        assertThat(result.getPassword()).isEqualTo("password123");
        assertThat(result.getPokemonsIds()).containsExactly(1L, 2L, 3L);

    }

    @Test
    void toDomain_ShouldApplyUpdatesFromUpdateUserDTO() {


        UpdateUserDTO existing = new UpdateUserDTO("jhon_update", "jhon_update@example.com", new Long[]{2L, 4L, 7L});

        User result = UserMapper.toDomain(existing,new User(1L, "jhon", "jhon@example.com", "password123", new Long[]{1L, 2L, 3L}));

        assertThat(result.getUsername()).isEqualTo("jhon_update");
        assertThat(result.getEmail()).isEqualTo("jhon_update@example.com");
        assertThat(result.getPokemonsIds()).containsExactly(2L, 4L, 7L);


        }

    @Test
    void toDomain_ShouldNotOverwriteDataWithNulls() {

        User existingUser = new User(1L, "original", "old@mail.com", "pass", new Long[]{1L});

        UpdateUserDTO updateDTO = new UpdateUserDTO("new_name", null, null);


        User result = UserMapper.toDomain(updateDTO, existingUser);


        assertThat(result.getUsername()).isEqualTo("new_name");
        assertThat(result.getEmail()).isEqualTo("old@mail.com");
        }


    }
