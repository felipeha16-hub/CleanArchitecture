# Integración con API de Pokémon

## 📋 Resumen

Se implementó la integración con la API pública de Pokémon (`pokeapi.co`) para enriquecer los datos de usuarios. Ahora cada usuario puede tener una lista de IDs de pokémons, y el sistema trae automáticamente los nombres desde la API externa.

## 🏗️ Arquitectura Implementada

### Capas y Componentes

```
┌─────────────────────────────────────────────────────┐
│          Controller (HTTP)                           │
│  UsersController → GET /api/users/{id}              │
│  UsersController → GET /api/users/{id}/with-pokemons│
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│          Use Cases (Application)                    │
│  GetUserIdUseCase → obtiene usuario con pokémons   │
└──────────────────────┬──────────────────────────────┘
                       │
        ┌──────────────┴──────────────┐
        │                             │
┌───────▼──────────────┐      ┌──────▼─────────────────┐
│  IUserRepository     │      │  IPokemonClient (PUERTO)│
│  (Persistencia)      │      │  (Cliente Externo)    │
└───────┬──────────────┘      └──────┬────────────────┘
        │                             │
┌───────▼──────────────┐      ┌──────▼─────────────────┐
│ JpaUserRepository    │      │ PokemonHttpAdapter    │
│ ↓ UserEntity         │      │ → HTTP REST a PokéAPI │
└──────────────────────┘      │ → Parsing JSON        │
                              └──────────────────────┘
                                      │
                              ┌───────▼──────────────┐
                              │  PokéAPI Remoto     │
                              │ pokeapi.co/api/v2/  │
                              └────────────────────┘
```

### 1. **Puerto (Interface) - `IPokemonClient`**
   - **Ubicación:** `infrastructure/clients/IPokemonClient.java`
   - **Responsabilidad:** Define el contrato de comunicación con servicios externos de pokémon
   - **Métodos:**
     - `String getPokemonById(Long id)` — Obtiene el nombre de un pokémon por ID
     - `List<String> getPokemonNamesByIds(List<Long> ids)` — Obtiene los nombres de múltiples pokémons

### 2. **Adaptador HTTP - `PokemonHttpAdapter`**
   - **Ubicación:** `infrastructure/clients/adapter/PokemonHttpAdapter.java`
   - **Responsabilidad:** Implementa la comunicación HTTP con la API pública de Pokémon
   - **Características:**
     - Usa `RestTemplate` para hacer peticiones HTTP
     - Parsea respuestas JSON con `ObjectMapper` (Jackson)
     - Maneja excepciones y errores de red
     - Logging de operaciones
     - Itera sobre múltiples IDs para traer pokémons

### 3. **Configuración - `PokemonClientConfig`**
   - **Ubicación:** `infrastructure/clients/configuration/PokemonClientConfig.java`
   - **Responsabilidad:** Configura beans de Spring para integración HTTP
   - **Componentes:**
     - `RestTemplate` con timeouts configurados (5s conexión, 10s lectura)
     - `ObjectMapper` para serialización/deserialización JSON
     - URL base de la API de Pokémon

### 4. **Mapper - `UserMapper`**
   - **Ubicación:** `application/mapper/UserMapper.java`
   - **Responsabilidad:** Convierte modelos de dominio a DTOs con datos enriquecidos
   - **Método nuevo:**
     - `toUserWithPokemonDTO(User user)` — Obtiene usuario + nombres de pokémons
     - Itera sobre los IDs de pokémons del usuario
     - Llama al cliente HTTP para traer nombres
     - Mapea todo en un `UserWhitPokemonDTO` con lista de pokémons

### 5. **Use Case - `GetUserIdUseCase`**
   - **Ubicación:** `application/useCases/GetUserIdUseCase.java`
   - **Cambio:** Ahora devuelve `UserWhitPokemonDTO` en lugar de `UserResponseDTO`
   - Inyecta el `UserMapper` para enriquecer datos

### 6. **DTOs**
   - **`UserWhitPokemonDTO`** — DTO de respuesta con usuario + lista de pokémons
     - Campo `pokemons`: `List<PokemonResponseDTO>`
     - Clase interna: `PokemonResponseDTO(id: Long, name: String)`
   - **`UserResponseDTO`** — DTO sin pokémons enriquecidos (para otros endpoints)

## 🔄 Flujo de Ejecución

**Cuando haces:** `GET /api/users/{id}`

1. **Controller** recibe la solicitud → llama a `GetUserIdUseCase.getUserById(id)`
2. **Use Case** obtiene `User` de la BD mediante `IUserRepository.findById(id)`
3. **Use Case** llama a `UserMapper.toUserWithPokemonDTO(user)`
4. **Mapper** extrae `pokemonsIds` del usuario [1, 4, 7]
5. **Mapper** llama a `IPokemonClient.getPokemonNamesByIds([1, 4, 7])`
6. **Adaptador HTTP** itera sobre los IDs:
   - GET `https://pokeapi.co/api/v2/pokemon/1` → JSON response
   - Extrae `.name` → "Bulbasaur"
   - GET `https://pokeapi.co/api/v2/pokemon/4` → "Charmander"
   - GET `https://pokeapi.co/api/v2/pokemon/7` → "Squirtle"
7. **Mapper** combina User + nombres en `UserWhitPokemonDTO`
8. **Controller** devuelve respuesta JSON:

```json
{
  "id": 1,
  "username": "juan",
  "email": "juan@example.com",
  "pokemons": [
    { "id": 1, "name": "Bulbasaur" },
    { "id": 4, "name": "Charmander" },
    { "id": 7, "name": "Squirtle" }
  ]
}
```

## 📁 Estructura de Carpetas

```
user/
├── application/
│   ├── client/
│   │   └── IPokemonClient.java          ← Puerto (interfaz)
│   ├── dto/
│   │   ├── UserWhitPokemonDTO.java      ← DTO de respuesta con pokémons
│   │   └── UserResponseDTO.java         ← DTO simple sin pokémons
│   ├── mapper/
│   │   └── UserMapper.java              ← Enriquece datos
│   └── useCases/
│       └── GetUserIdUseCase.java        ← Usa el mapper
├── domain/
│   ├── model/
│   │   └── User.java                    ← pokemonsIds: Long[]
│   └── repository/
│       └── IUserRepository.java
└── infrastructure/
    ├── clients/
    │   ├── IPokemonClient.java          ← Puerto (implementado en adapter)
    │   ├── adapter/
    │   │   └── PokemonHttpAdapter.java  ← Implementación HTTP
    │   └── configuration/
    │       └── PokemonClientConfig.java ← Beans de RestTemplate
    ├── controller/
    │   └── UsersController.java         ← Endpoints
    └── persistence/
        ├── JpaUserRepository.java
        ├── adapter/
        └── entity/
            └── UserEntity.java
```

## 🔧 Configuración de Timeouts

Por defecto, la API de Pokémon puede ser lenta:
- **Connection Timeout:** 5 segundos
- **Read Timeout:** 10 segundos

Se pueden ajustar en `PokemonClientConfig` si es necesario.

## ⚠️ Consideraciones

### Posibles Mejoras Futuras

1. **Caching:** Implementar caché (Redis, Caffeine) para no llamar a PokéAPI en cada request
   ```java
   @Cacheable(value = "pokemons", key = "#id")
   public String getPokemonById(Long id) { ... }
   ```

2. **Batch Requests:** Si PokéAPI lo permite, traer varios pokémons en una sola llamada en lugar de iterar

3. **Circuit Breaker:** Usar Resilience4j para manejar fallos de la API externa
   ```java
   @CircuitBreaker(name = "pokemon", fallbackMethod = "fallbackGetPokemon")
   ```

4. **Async:** Usar `WebClient` de Spring en lugar de `RestTemplate` para llamadas no-bloqueantes
   ```java
   private final WebClient webClient;
   ```

5. **Validación:** Validar IDs de pokémons antes de llamar a la API

## 📝 Endpoints

| Método | URL | Respuesta |
|--------|-----|-----------|
| GET | `/api/users/{id}` | `UserResponseDTO` (sin pokémons) |
| GET | `/api/users/{id}/with-pokemons` | `UserWhitPokemonDTO` (con nombres) |
| GET | `/api/users` | `List<UserResponseDTO>` |
| POST | `/api/users` | `UserResponseDTO` |
| PATCH | `/api/users/{id}` | `UserResponseDTO` |
| DELETE | `/api/users/{id}` | `void` (204 No Content) |

## 🧪 Probando en Swagger

1. Inicia la aplicación: `.\gradlew bootRun`
2. Abre Swagger: `http://localhost:8080/swagger-ui.html`
3. Prueba:
   - Crea un usuario con `pokemonsIds: [1, 4, 7]`
   - Ejecuta `GET /api/users/{id}/with-pokemons`
   - Verás los nombres de los pokémons enriquecidos

## 📚 Referencias

- **PokéAPI Documentation:** https://pokeapi.co/
- **Spring RestTemplate:** https://spring.io/guides/gs/consuming-rest/
- **Jackson JSON Processing:** https://github.com/FasterXML/jackson
- **Clean Architecture & Ports/Adapters:** https://alistair.cockburn.us/hexagonal-architecture/

---

**Nota:** Esta implementación sigue el patrón **Puertos y Adaptadores** (Hexagonal Architecture), donde:
- **Puertos:** Son las interfaces (`IPokemonClient`, `IUserRepository`)
- **Adaptadores:** Son las implementaciones concretas (`PokemonHttpAdapter`, `JpaUserRepository`)
- **Ventaja:** Fácil cambiar la implementación (ej: cambiar HTTP por gRPC) sin tocar la lógica del dominio

