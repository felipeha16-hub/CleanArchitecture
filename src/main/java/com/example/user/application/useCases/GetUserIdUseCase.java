package com.example.user.application.useCases;

import com.example.user.application.dto.UserWhitPokemonDTO;
import com.example.user.application.mapper.UserMapper;
import com.example.user.domain.exceptions.BusinessException;
import com.example.user.domain.exceptions.messages.BusinessErrorMessage;
import com.example.user.domain.model.User;
import com.example.user.domain.repository.IUserRepository;
import com.example.user.infrastructure.clients.IPokemonClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Component
@Slf4j
public class GetUserIdUseCase {

    private final IUserRepository repository;
    private final UserMapper userMapper;
    private final IPokemonClient pokemonClient;


    public Mono<UserWhitPokemonDTO> getUserById(Long id) {
        log.info("Looking up user by id {}", id);

        return Mono.fromCallable(() -> {
            // Find user synchronously
            User user = repository.findById(id)
                    .orElseThrow(() -> new BusinessException(BusinessErrorMessage.USER_NOT_FOUND));

            log.info("User found with id {}: {}", id, user.getUsername());
            return user;
        })
        .onErrorMap(BusinessException.class, ex -> ex)
        .flatMap(user -> {

            List<Long> pokemonIds = user.getPokemonsIds() != null ?
                    Arrays.asList(user.getPokemonsIds()) : List.of();

            if (pokemonIds.isEmpty()) {
                return Mono.just(userMapper.toUserWithPokemonDTO(user, List.of()));
            }


            return pokemonClient.getPokemonNamesByIds(pokemonIds)
                    .map(names -> userMapper.toUserWithPokemonDTO(user, names));
        })
        .doOnTerminate(() -> log.info("Finished processing getUserById for id {}", id))
        .onErrorMap(ex -> {

            if (ex instanceof BusinessException) {
                return ex;
            }

            return new BusinessException(BusinessErrorMessage.USER_NOT_FOUND);
        });
    }

}
