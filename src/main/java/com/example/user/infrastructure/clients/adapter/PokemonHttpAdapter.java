package com.example.user.infrastructure.clients.adapter;

import com.example.user.infrastructure.clients.IPokemonClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class PokemonHttpAdapter implements IPokemonClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<String> getPokemonById(Long id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(String.class)
                //.doOnNext(response -> log.info("Response for pokemon {}: {}", id, response))
                .map(response -> {
                    try {
                        JsonNode json = objectMapper.readTree(response);
                        String name = json.get("name").asText();
                        //log.info("Parsed pokemon {}: name={}", id, name);
                        return name;
                    } catch (Exception e) {
                        //log.error("Error parsing pokemon {}: {}", id, e.getMessage());
                        return "unknown";
                    }
                })
                .onErrorResume(error -> {
                    //log.error("Error fetching pokemon {}: {}", id, error.getMessage());
                    return Mono.just("unknown");
                });
    }


    @Override
    public Mono<List<String>> getPokemonNamesByIds(List<Long> ids) {
        int concurrency = Math.min(ids.size(), 20);
        return Flux.fromIterable(ids)
                .flatMapSequential(this::getPokemonById, concurrency)
                .collectList();
    }

}


