# 📁 Estructura y Código Correcto - UserMapper y Clean Architecture

## 🎯 Vista General del Proyecto

```
src/main/java/com/example/base/user/

├── application/                           ← LÓGICA DE NEGOCIO
│   ├── dto/
│   │   ├── CreateUserDTO.java
│   │   ├── UpdateUserDTO.java
│   │   ├── UserResponseDTO.java
│   │   └── UserWhitPokemonDTO.java      ← DTO de respuesta enriquecida
│   ├── mapper/
│   │   └── UserMapper.java              ← ⭐ AQUÍ ESTÁ LA INYECCIÓN
│   └── useCases/
│       ├── CreateUserUseCase.java
│       ├── DeleteUserUseCase.java
│       ├── GetUserIdUseCase.java        ← Inyecta UserMapper
│       ├── GetUsersUseCase.java
│       └── PatchUserUseCase.java
│
├── domain/                                ← MODELOS Y PUERTOS
│   ├── model/
│   │   └── User.java                    ← Entidad de dominio
│   └── repository/
│       └── IUserRepository.java         ← Puerto de persistencia
│
├── infrastructure/                        ← IMPLEMENTACIONES
│   ├── clients/
│   │   ├── IPokemonClient.java          ← ⭐ PUERTO (interfaz)
│   │   ├── adapter/
│   │   │   └── PokemonHttpAdapter.java  ← ⭐ ADAPTADOR (implementación)
│   │   └── configuration/
│   │       └── PokemonClientConfig.java ← Configuración de beans
│   ├── controller/
│   │   └── UsersController.java
│   └── persistence/
│       ├── JpaUserRepository.java
│       ├── adapter/
│       │   └── UserRepositoryAdapter.java
│       └── entity/
│           └── UserEntity.java
│
└── configuration/
    ├── OpenApiConfig.java
    └── PasswordConfig.java
```

---

## ⭐ El Código Correcto: UserMapper

### Posición en Clean Architecture

```
Application Layer
    │
    └── UserMapper.java
        │
        ├── Depende de → IPokemonClient (application/client/)
        │               └─ NO depende de PokemonHttpAdapter
        │
        └── Inyecta en constructor →  @AllArgsConstructor
```

### Código Completo y Correcto

```java
package com.example.base.user.application.mapper;

import com.example.base.user.application.dto.CreateUserDTO;
import com.example.base.user.application.dto.UpdateUserDTO;
import com.example.base.user.application.dto.UserResponseDTO;
import com.example.base.user.application.dto.UserWhitPokemonDTO;
import com.example.base.user.domain.model.User;
import com.example.base.user.infrastructure.clients.IPokemonClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Component                      // ← Spring gestiona este bean
@AllArgsConstructor             // ← Genera constructor con parámetros
public class UserMapper {

    // ⭐ INYECCIÓN: Depende de la INTERFAZ, no de la implementación
    private final IPokemonClient pokemonClient;

    // Método estático (sin dependencias)
    public static UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPokemonsIds()
        );
    }

    // ⭐ Método de INSTANCIA que usa la dependencia inyectada
    public UserWhitPokemonDTO toUserWithPokemonDTO(User user) {
        
        // 1️⃣ Llamar al cliente para obtener nombres
        List<String> pokemonNames = pokemonClient.getPokemonNamesByIds(
                Arrays.asList(user.getPokemonsIds())
        );

        // 2️⃣ Mapear IDs con nombres
        List<UserWhitPokemonDTO.PokemonResponseDTO> pokemons = IntStream
                .range(0, user.getPokemonsIds().length)
                .mapToObj(i -> new UserWhitPokemonDTO.PokemonResponseDTO(
                        user.getPokemonsIds()[i],
                        pokemonNames.get(i)
                ))
                .toList();

        // 3️⃣ Devolver DTO enriquecido
        return new UserWhitPokemonDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                pokemons
        );
    }

    // Métodos estáticos (sin dependencias)
    public static User toDomain(CreateUserDTO dto) {
        return new User(
                null, 
                dto.getUsername(), 
                dto.getEmail(), 
                dto.getPassword(), 
                dto.getPokemonsIds()
        );
    }

    public static User toDomain(UpdateUserDTO dto, User existing) {
        if (dto.getUsername() != null) existing.setUsername(dto.getUsername());
        if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
        return existing;
    }
}
```

### Análisis de Cada Parte

#### 1. Anotaciones de Spring

```java
@Component                      // ← Marca como bean para Spring
@AllArgsConstructor             // ← Genera constructor automáticamente
public class UserMapper {
```

**¿Qué hace `@AllArgsConstructor`?**

Lombok lo expande a:

```java
public UserMapper(IPokemonClient pokemonClient) {
    this.pokemonClient = pokemonClient;
}
```

Spring automáticamente:
1. Ve que UserMapper necesita IPokemonClient
2. Busca quién implementa IPokemonClient → PokemonHttpAdapter
3. Crea PokemonHttpAdapter (si lo necesita)
4. Lo inyecta en el constructor de UserMapper

#### 2. La Inyección

```java
@Component
@AllArgsConstructor
public class UserMapper {

    // ⭐ ESTO ES UNA INYECCIÓN
    private final IPokemonClient pokemonClient;
    
    // Spring proporciona: 
    // new PokemonHttpAdapter(...) → pokemonClient
}
```

**Por qué `private final`?**
- `private` → Solo UserMapper lo usa
- `final` → No puede cambiar después de inicializar
- Garant que una vez inyectado, no se modifica

#### 3. Métodos Estáticos (sin dependencias)

```java
public static UserResponseDTO toDTO(User user) {
    // ❌ No usamos pokemonClient aquí
    // ✅ Solo transforma User → UserResponseDTO
    return new UserResponseDTO(...);
}
```

Estos métodos pueden ser estáticos porque:
- No necesitan `pokemonClient`
- No tienen efectos secundarios
- Pueden llamarse sin instancia

#### 4. Métodos de Instancia (con dependencias)

```java
// ✅ NO es static
public UserWhitPokemonDTO toUserWithPokemonDTO(User user) {
    // ✅ Puede usar pokemonClient porque es método de instancia
    List<String> names = pokemonClient.getPokemonNamesByIds(...);
    // ...
}
```

Tiene que ser no-est��tico porque:
- Necesita acceder a `pokemonClient` (inyectado)
- Los métodos estáticos no pueden acceder a variables de instancia

---

## ⭐ El Código Correcto: GetUserIdUseCase

```java
package com.example.base.user.application.useCases;

import com.example.base.user.application.dto.UserWhitPokemonDTO;
import com.example.base.user.application.mapper.UserMapper;
import com.example.base.user.domain.repository.IUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class GetUserIdUseCase {

    private final IUserRepository repository;
    private final UserMapper userMapper;  // ⭐ TAMBIÉN INYECTADO

    public Optional<UserWhitPokemonDTO> getUserById(Long id) {
        return repository.findById(id)
                .map(userMapper::toUserWithPokemonDTO);  // ← Usa instancia
    }
}
```

**¿Por qué `userMapper::toUserWithPokemonDTO` y no `UserMapper::toUserWithPokemonDTO`?**

- `userMapper::` → Método de INSTANCIA
- `UserMapper::` → Método ESTÁTICO

Como `toUserWithPokemonDTO` es un método de instancia (necesita `pokemonClient`), hay que usar la instancia inyectada `userMapper`.

---

## ⭐ El Puerto: IPokemonClient

```java
package com.example.base.user.infrastructure.clients;

import java.util.List;

public interface IPokemonClient {
    String getPokemonById(Long id);
    List<String> getPokemonNamesByIds(List<Long> ids);
}
```

**Ubicación: `infrastructure/clients/`**

¿Por qué en infrastructure? Porque define el contrato de acceso a un servicio externo.

---

## ⭐ El Adaptador: PokemonHttpAdapter

```java
package com.example.base.user.infrastructure.clients.adapter;

import com.example.base.user.infrastructure.clients.IPokemonClient;
import com.example.base.user.infrastructure.clients.configuration.PokemonClientConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class PokemonHttpAdapter implements IPokemonClient {

    private final RestTemplate restTemplate;      // ⭐ INYECTADO
    private final ObjectMapper objectMapper;      // ⭐ INYECTADO

    @Override
    public String getPokemonById(Long id) {
        try {
            String url = PokemonClientConfig.BASE_URL + id;
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("name").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch pokemon", e);
        }
    }

    @Override
    public List<String> getPokemonNamesByIds(List<Long> ids) {
        return ids.stream()
                .map(this::getPokemonById)
                .toList();
    }
}
```

---

## ⭐ La Configuración: PokemonClientConfig

```java
package com.example.base.user.infrastructure.clients.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PokemonClientConfig {
    
    public static final String BASE_URL = "https://pokeapi.co/api/v2/pokemon/";

    // ⭐ Bean que Spring proporciona
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);   // 5 segundos
        factory.setReadTimeout(10000);     // 10 segundos
        return new RestTemplate(factory);
    }

    // ⭐ Otro Bean que Spring proporciona
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
```

Estos beans los usa PokemonHttpAdapter, que a su vez los inyecta automáticamente.

---

## 📊 Flujo de Inyección Completo

```
Cuando Spring inicia:

1. Encuentra @Configuration PokemonClientConfig
   ├─ Crea @Bean RestTemplate
   └─ Crea @Bean ObjectMapper

2. Encuentra @Component PokemonHttpAdapter
   ├─ Necesita RestTemplate → ✓ ya existe
   └─ Necesita ObjectMapper → ✓ ya existe
   └─ Crea PokemonHttpAdapter con ambos

3. Encuentra @Component UserMapper
   ├─ Necesita IPokemonClient
   ├─ Busca implementación → PokemonHttpAdapter ✓
   └─ Crea UserMapper con PokemonHttpAdapter

4. Encuentra @Component GetUserIdUseCase
   ├─ Necesita IUserRepository → ✓ JpaUserRepository
   ├─ Necesita UserMapper → ✓ ya creado
   └─ Crea GetUserIdUseCase con ambos

5. Encuentra @RestController UsersController
   ├─ Necesita GetUserIdUseCase → ✓ ya creado
   ├─ Necesita CreateUserUseCase → ✓ creado
   ├─ Necesita UpdateUserUseCase → ✓ creado
   ├─ Necesita DeleteUserUseCase → ✓ creado
   └─ Crea UsersController con todos

✓ TODO INYECTADO Y LISTO PARA USAR
```

---

## ✅ Verificación: ¿Es Correcto?

| Aspecto | Estado | Verificación |
|---------|--------|--------------|
| UserMapper es `@Component` | ✅ | Sí |
| UserMapper tiene `@AllArgsConstructor` | ✅ | Sí |
| IPokemonClient está inyectado | ✅ | `private final IPokemonClient pokemonClient` |
| IPokemonClient es interfaz | ✅ | Sí, en `infrastructure/clients/` |
| PokemonHttpAdapter implementa IPokemonClient | ✅ | Sí, en `infrastructure/clients/adapter/` |
| GetUserIdUseCase inyecta UserMapper | ✅ | `private final UserMapper userMapper` |
| toUserWithPokemonDTO es método de instancia | ✅ | No tiene `static` |
| Sigue Clean Architecture | ✅ | Separación clara de capas |
| Cumple SOLID | ✅ | DIP, SRP, OCP |

---

## 🎯 Conclusión

Tu implementación de **inyección en UserMapper** es **correcta, profesional y sigue Clean Architecture**.

Las claves son:
1. ✅ `@Component` — Spring gestiona el bean
2. ✅ `@AllArgsConstructor` — Constructor automático
3. ✅ `private final IPokemonClient` — Dependencia inyectada
4. ✅ Métodos de instancia que usan la inyección
5. ✅ Métodos estáticos que NO la usan
6. ✅ Separación clara entre capas
7. ✅ Inversión de dependencias hacia arriba


