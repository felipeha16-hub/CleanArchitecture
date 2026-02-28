package com.example.user.infrastructure.persistence.adapter;

import com.example.user.domain.model.User;

import com.example.user.infrastructure.persistence.JpaUserRepository;
import com.example.user.infrastructure.persistence.entity.UserEntity;
import com.example.user.infrastructure.persistence.mapper.IUserMapperImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// AQUÍ: Añade la implementación del Mapper (la que termina en Impl)
@Import({UserRepositoryAdapter.class, IUserMapperImpl.class})
class UserRepositoryAdapterTest {

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Test
    @DisplayName("Validación total: Adapter -> DB (Entidad) -> Adapter")
    void saveUser() {
        // 1. Guardamos usando el lenguaje del DOMINIO (User)
        User newUser = new User(null, "Ash", "ash@mail.com", "password123", new Long[]{1L});
        User savedUser = userRepositoryAdapter.save(newUser);

        // 2. ASSERT DE INFRAESTRUCTURA
        // Usamos el JpaUserRepository para ver la ENTIDAD real en la DB
        UserEntity entityInDb = jpaUserRepository.findById(savedUser.getId()).orElseThrow();

        // Aquí verificamos que la TABLA tiene los datos que queremos
        assertThat(entityInDb.getPokemonsIds()).containsExactly(1L);
        assertThat(entityInDb.getUsername()).isEqualTo("Ash");

        // 3. ASSERT DE DOMINIO (Mapeo de vuelta)
        // Verificamos que el Adapter sabe leer esos datos y devolver un User
        Optional<User> userFromAdapter = userRepositoryAdapter.findById(savedUser.getId());
        assertThat(userFromAdapter.get().getPokemonsIds()).containsExactly(1L);
    }

    @Test
    @DisplayName("findAll return all users")
    void findAllUsers() {
        // 1. Arrange: Insertamos dos entidades directamente (sin usar el adapter)
        jpaUserRepository.save(new UserEntity(null, "User1", "u1@mail.com", "password123", new Long[]{1L}));
        jpaUserRepository.save(new UserEntity(null, "User2", "u2@mail.com", "password123", new Long[]{2L}));

        // 2. Act: Recuperamos usando el ADAPTER
        List<User> users = userRepositoryAdapter.findAll();

        // 3. Assert: Validamos que el Adapter supo leer y transformar ambos
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getUsername).containsExactlyInAnyOrder("User1", "User2");


    }

    @Test
    @DisplayName("GetById get the user")
    void getUserById() {
        // Arrange: Insertamos una entidad directamente (sin usar el adapter)
        UserEntity entity = jpaUserRepository.save(new UserEntity(null, "Misty", "misty@mail.com", "password123", new Long[]{3L}));


        // Act: Recuperamos usando el ADAPTER
        Optional<User> userOpt = userRepositoryAdapter.findById(entity.getId());

        // Assert: Validamos que el Adapter supo leer y transformar la entidad
        assertThat(userOpt).isPresent();
        User user = userOpt.get();
        assertThat(user.getUsername()).isEqualTo("Misty");
        assertThat(user.getPokemonsIds()).containsExactly(3L);

    }

    @Test
    @DisplayName("DeleteById deletes the user")
    void deleteUserById() {
        // Arrange: Insertamos una entidad directamente (sin usar el adapter)
        UserEntity entity = jpaUserRepository.save(new UserEntity(null, "Brock", "brock@mail.com", "password123", new Long[]{4L}));

        // Act: Eliminamos usando el ADAPTER
        userRepositoryAdapter.deleteById(entity.getId());

        // Assert: Validamos que el Adapter eliminó la entidad
        Optional<UserEntity> deletedEntity = jpaUserRepository.findById(entity.getId());
        assertThat(deletedEntity).isEmpty();

    }

    @Test
    @DisplayName("ExistsByEmail returns true if email exists")
    void existsUserByEmail() {
        // Arrange

        UserEntity entity = jpaUserRepository.save(new UserEntity(null, "Test", "test@unique.com", "123", new Long[]{}));

        // Act & Assert
        assertThat(userRepositoryAdapter.existsByEmail(entity.getEmail())).isTrue();
        assertThat(userRepositoryAdapter.existsByEmail("non-existent@mail.com")).isFalse();
    }

    @Test
    @DisplayName("ExistsByID returns true if ID exists")
    void existsUserByID() {
        // Arrange

        UserEntity entity = jpaUserRepository.save(new UserEntity(null, "Test", "test@unique.com", "123", new Long[]{}));

        // Act & Assert
        assertThat(userRepositoryAdapter.existsById(entity.getId())).isTrue();
        assertThat(userRepositoryAdapter.existsById(100L)).isFalse();
    }

    @Test
    @DisplayName("Update: return the user with new data")
    void updateUser() {
        // 1. Arrange: Creamos un usuario inicial
        UserEntity originalEntity = jpaUserRepository.save(
                new UserEntity(null, "Ash", "ash@mail.com", "123", new Long[]{1L})
        );

        // 2. Act: Creamos un objeto de dominio con el MISMO ID pero nuevos datos
        User userUpdate = new User(originalEntity.getId(), "Ash Updated", "ash@mail.com", "123", new Long[]{1L, 25L, 150L});
        userRepositoryAdapter.save(userUpdate);

        // 3. Assert: Verificamos que en la DB se actualizaron los datos
        UserEntity updatedInDb = jpaUserRepository.findById(originalEntity.getId()).orElseThrow();
        assertThat(updatedInDb.getUsername()).isEqualTo("Ash Updated");
        assertThat(updatedInDb.getPokemonsIds()).containsExactly(1L, 25L, 150L);
    }

}