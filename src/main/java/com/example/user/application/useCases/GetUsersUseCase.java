package com.example.user.application.useCases;

import com.example.user.application.dto.UserResponseDTO;
import com.example.user.application.mapper.UserMapper;
import com.example.user.domain.repository.IUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class GetUsersUseCase {

    private final IUserRepository repository;


    public List<UserResponseDTO> getAllUsers() {
        // Lógica para obtener todos los usuarios
        return repository.findAll().stream()
                .map(UserMapper::toDTO)
                .toList();
    }
}
