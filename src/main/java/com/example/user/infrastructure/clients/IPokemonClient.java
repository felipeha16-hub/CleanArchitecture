package com.example.user.infrastructure.clients;

import java.util.List;
import reactor.core.publisher.Mono;

public interface IPokemonClient {
    Mono<String> getPokemonById(Long id);
    Mono<List<String>> getPokemonNamesByIds(List<Long> ids);
}
