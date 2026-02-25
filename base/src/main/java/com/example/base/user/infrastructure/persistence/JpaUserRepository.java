package com.example.base.user.infrastructure.persistence;

import com.example.base.user.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    // puedes añadir consultas derivadas aquí si las necesitas, por ejemplo:
    // Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
