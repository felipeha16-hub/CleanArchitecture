package com.example.user.application.useCases;

import com.example.user.application.dto.PokemonResponseDTO;
import com.example.user.application.dto.UserWhitPokemonDTO;
import com.example.user.application.mapper.UserMapper;
import com.example.user.domain.exceptions.BusinessException;
import com.example.user.domain.model.User;
import com.example.user.domain.repository.IUserRepository;
import com.example.user.infrastructure.clients.IPokemonClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetUserIdUseCaseTest {

    @Mock
    private IUserRepository repository;

    @InjectMocks
    private GetUserIdUseCase getUserIdUseCase;

    @Mock
    private UserMapper userMapper;

    @Mock
    private IPokemonClient pokemonClient;

    @Test
    @DisplayName("should return a business exception when user is not found")
    void getUserById_UserNotFound() {

        Long userId = 1L;
        when(repository.findById(userId)).thenReturn(Optional.empty());

        Mono<UserWhitPokemonDTO> result = getUserIdUseCase.getUserById(userId);


        StepVerifier.create(result)
                .expectError(BusinessException.class)
                .verify();

        verifyNoInteractions(pokemonClient);


    }

    @Test
    @DisplayName("should return user with pokemon names when user is found")
    void getUserById_Success(){
        Long userId = 1L;
        User user = new User(userId, "jhon", "jhon@example.com", "pass", new Long[]{25L});
        List<String> pokemonNames = List.of("Pikachu");

        UserWhitPokemonDTO expectedDTO = new UserWhitPokemonDTO(userId, "jhon", "jhon@example.com", List.of(new PokemonResponseDTO(25L, "Pikachu")));


        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(pokemonClient.getPokemonNamesByIds(anyList())).thenReturn(Mono.just(pokemonNames));
        when(userMapper.toUserWithPokemonDTO(user, pokemonNames)).thenReturn(expectedDTO);

        Mono<UserWhitPokemonDTO> result = getUserIdUseCase.getUserById(userId);

        StepVerifier.create(result)
                .expectNext(expectedDTO)
                .verifyComplete();
    }

}
