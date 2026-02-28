package com.example.user.domain.repository;

import com.example.user.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);

    boolean existsByEmail(String email);
}
