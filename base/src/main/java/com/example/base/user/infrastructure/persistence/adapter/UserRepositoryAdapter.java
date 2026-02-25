package com.example.base.user.infrastructure.persistence.adapter;

import com.example.base.user.domain.model.User;
import com.example.base.user.domain.repository.IUserRepository;
import com.example.base.user.infrastructure.persistence.JpaUserRepository;
import com.example.base.user.infrastructure.persistence.entity.UserEntity;
import com.example.base.user.infrastructure.persistence.mapper.IUserMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserRepositoryAdapter implements IUserRepository {

    private final JpaUserRepository jpaUserRepository;
    private final IUserMapper userMapper;

    public UserRepositoryAdapter(JpaUserRepository jpaUserRepository, IUserMapper userMapper) {
        this.jpaUserRepository = jpaUserRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        UserEntity saved = jpaUserRepository.save(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll().stream().map(userMapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaUserRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaUserRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }


}

