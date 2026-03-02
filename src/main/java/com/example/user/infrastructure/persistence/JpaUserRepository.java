package com.example.user.infrastructure.persistence;

import com.example.user.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    // Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
