package com.example.base.user.infrastructure.persistence.adapter;

import com.example.base.user.domain.model.User;

import com.example.base.user.infrastructure.persistence.JpaUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserRepositoryAdapter.class)
class UserRepositoryAdapterTest {

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @Test
    @DisplayName("Debe persistir y recuperar un usuario con sus pokémones correctamente")
    void saveAndFindUserTest() {
        // Arrange: Creamos un usuario de dominio (ID null porque es nuevo)
        Long[] myPokemons = {1L, 25L, 150L};
        User newUser = new User(null, "Ash Ketchum", "ash@pallet.com", "pikachu123", myPokemons);

        // Act: Guardamos usando el ADAPTER
        User savedUser = userRepositoryAdapter.save(newUser);

        // Assert: Validamos que Postgres generó el ID y guardó el Array
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("Ash Ketchum");
        assertThat(savedUser.getPokemonsIds()).containsExactly(1L, 25L, 150L);

        // Verificación extra: ¿Realmente está en la DB?
        Optional<User> found = userRepositoryAdapter.findById(savedUser.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getPokemonsIds()).hasSize(3);
    }
}