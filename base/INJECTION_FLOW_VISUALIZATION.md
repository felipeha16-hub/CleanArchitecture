# 🎨 Visualización: Flujo de Inyección en tu Código

## 1️⃣ Cuando Llamas: `GET /api/users/1`

```
HTTP Request
    ↓
┌────────────────────────────────────┐
│  UsersController                   │
│  .getUserById(1)                   │
└────────────────────────────────────┘
    ↓
```

---

## 2️⃣ El Controlador Llama al Use Case

```
┌────────────────────────────────────┐
│  UsersController                   │
│                                    │
│  public ResponseEntity<...>        │
│  getUserById(@PathVariable Long id) {
│    return getUserByIdUseCase       │
│           .getUserById(id)         │  ← Delegación
│  }                                 │
└────────────────────────────────────┘
    ↓
```

---

## 3️⃣ El Use Case Obtiene el Usuario

```
┌────────────────────────────────────┐
│  GetUserIdUseCase                  │
│                                    │
│  public Optional<UserWhit...>      │
│  getUserById(Long id) {            │
│    return repository               │
│      .findById(id)  ←──────┐       │
│      .map(...)              │       │
│  }                          │       │
└────────────────────────────┼───────┘
    ↓                         │
    │                    ┌────▼─────────────┐
    │                    │  IUserRepository │
    │                    │  (Persistencia)  │
    │                    │                  │
    └───────────────────→│  JpaUserRepository
                         │  SELECT * FROM users WHERE id=1
                         │  ↓ (Database)
                         │  User {
                         │    id: 1,
                         │    username: "juan",
                         │    email: "juan@example.com",
                         │    pokemonsIds: [1, 4, 7]
                         │  }
                         └──────────────────┘
    ↓
User object cargado ✓
```

---

## 4️⃣ El Use Case Mapea el Resultado

```
┌──────────────────────────────────────┐
│  GetUserIdUseCase                    │
│                                      │
│  public Optional<UserWhit...>        │
│  getUserById(Long id) {              │
│    return repository                 │
│      .findById(id)                   │
│      .map(userMapper::               │
│            toUserWithPokemonDTO)  ←─┐│
│  }                                  ││
└──────────────────────────────────────┘
                                       │
    ┌──────────────────────────────────┘
    │
    ▼
┌──────────────────────────────────────────┐
│  UserMapper                              │
│  (tiene IPokemonClient inyectado)        │
│                                          │
│  public UserWhitPokemonDTO               │
│  toUserWithPokemonDTO(User user) {      │
│    List<String> names =                 │
│      pokemonClient                   ←──┼─ ESTA ES LA INYECCIÓN
│      .getPokemonNamesByIds(...)      ││
│  }                                   ││
└──────────────────────────────────────┼───┘
    ↓                                  │
```

---

## 5️⃣ La Inyección en Acción

```
┌──────────────────────────────────────────┐
│  UserMapper                              │
│                                          │
│  private final IPokemonClient            │  ← DEPENDENCIA INYECTADA
│  pokemonClient;                          │
│                                          │
│  public UserWhitPokemonDTO               │
│  toUserWithPokemonDTO(User user) {      │
│                                          │
│    List<String> names =                 │
│    pokemonClient.getPokemonNamesByIds(   │
│      Arrays.asList(user.getPokemonsIds())│
│    );                                    │
│                                          │
│    // names = ["bulbasaur", ...]        │
│  }                                      │
└──────────────────────────────────────────┘
    ↓
    │ AQUÍ NECESITA LLAMAR A LA API
    │
┌───▼────────────────────────────────────────────┐
│  IPokemonClient (INTERFAZ)                     │
│  - Define qué se puede hacer                   │
│  - NO sabe cómo hacerlo                        │
└───┬────────────────────────────────────────────┘
    │
    │ ¿Cómo implementado? Por...
    │
    ▼
┌──────────────────────────────────────────────────┐
│  PokemonHttpAdapter                              │
│  implements IPokemonClient                       │
│                                                  │
│  private final RestTemplate restTemplate;    ←──┼─ TAMBIÉN INYECTADO
│  private final ObjectMapper objectMapper;    ←──┼─ TAMBIÉN INYECTADO
│                                                  │
│  @Override                                      │
│  public String getPokemonById(Long id) {       │
│    String url = BASE_URL + id;                 │
│    String response = restTemplate               │
│      .getForObject(url, String.class);         │
│    JsonNode json = objectMapper                │
│      .readTree(response);                      │
│    return json.get("name").asText();  ←─┐      │
│  }                                      │      │
└──────────────────────────────────────────┼──────┘
                                          │
                    ┌─────────────────────┘
                    │
                    ▼
        ┌──────────────────────┐
        │  PokéAPI (Remote)    │
        │  GET /pokemon/1      │
        │  ↓ JSON Response     │
        │  {                   │
        │    "name": "bulbasaur",
        │    "height": 7,      │
        │    "weight": 69,     │
        │    ...               │
        │  }                   │
        └──────────────────────┘
```

---

## 6️⃣ Spring Inyecta Automáticamente

```
┌─────────────────────────────────────────────────┐
│  Spring Application Context                     │
│  (Contenedor de Inyección)                      │
│                                                 │
│  Cuando inicia la aplicación:                   │
│                                                 │
│  1. Escanea @Component                          │
│     ├─ PokemonClientConfig ✓                    │
│     ├─ PokemonHttpAdapter ✓                     │
│     ├─ UserMapper ✓                             │
│     ├─ GetUserIdUseCase ✓                       │
│     └─ UsersController ✓                        │
│                                                 │
│  2. Resuelve dependencias                       │
│     ├─ UserMapper necesita IPokemonClient      │
│     │  └─ Encuentra PokemonHttpAdapter         │
│     │     ├─ necesita RestTemplate             │
│     │     │  └─ creado en PokemonClientConfig  │
│     │     └─ necesita ObjectMapper             │
│     │        └─ creado en PokemonClientConfig  │
│     │                                           │
│     └─ GetUserIdUseCase necesita UserMapper    │
│        └─ Ya creado ✓                          │
│                                                 │
│  3. Inyecta en constructores                    │
│     └─ Gracias a @AllArgsConstructor           │
│                                                 │
└─────────────────────────────────────────────────┘
```

---

## 7️⃣ Construcción de Beans (Paso a Paso)

```
Spring dice: "Necesito un GetUserIdUseCase"

┌─────────────────────────────────────────┐
│ 1. Crear GetUserIdUseCase               │
│    Necesita:                            │
│    - IUserRepository ✓ (JpaUserRepository)
│    - UserMapper ? (crear)               │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│ 2. Crear UserMapper                     │
│    Necesita:                            │
│    - IPokemonClient ? (crear)           │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│ 3. Crear PokemonHttpAdapter             │
│    (que implementa IPokemonClient)      │
│    Necesita:                            │
│    - RestTemplate ✓ (en config)         │
│    - ObjectMapper ✓ (en config)         │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│ 4. Spring inyecta hacia arriba:         │
│                                         │
│    RestTemplate & ObjectMapper          │
│           ↓                             │
│    PokemonHttpAdapter                   │
│           ↓                             │
│    UserMapper                           │
│           ↓                             │
│    GetUserIdUseCase                     │
│           ↓                             │
│    UsersController ← LISTO PARA USAR    │
└─────────────────────────────────────────┘
```

---

## 8️⃣ Estado Final: Todo Conectado

```
┌────────────────────────────────────┐
│  UsersController                   │
│  ├─ GetUserIdUseCase ✓ inyectado   │
│  ├─ CreateUserUseCase ✓            │
│  ├─ UpdateUserUseCase ✓            │
│  └─ DeleteUserUseCase ✓            │
└────────────────────────────────────┘
         ↑
         │ inyecta
         │
┌────────────────────────────────────┐
│  GetUserIdUseCase                  │
│  ├─ IUserRepository ✓ inyectado    │
│  └─ UserMapper ✓ inyectado         │
└────────────────────────────────────┘
         ↑
         │ inyecta
         │
┌────────────────────────────────────┐
│  UserMapper                        │
│  └─ IPokemonClient ✓ inyectado     │
└────────────────────────────────────┘
         ↑
         │ inyecta interfaz implementada por
         │
┌────────────────────────────────────────┐
│  PokemonHttpAdapter                    │
│  ├─ RestTemplate ✓ inyectado          │
│  └─ ObjectMapper ✓ inyectado          │
└────────────────────────────────────────┘
         ↑
         │ inyecta
         │
┌────────────────────────────────────────┐
│  PokemonClientConfig                   │
│  ├─ @Bean RestTemplate                 │
│  └─ @Bean ObjectMapper                 │
└────────────────────────────────────────┘
```

---

## 🎯 Resumen Visual

```
TODA LA CADENA DE INYECCIÓN:

REST Request
    ↓
Controller → GetUserIdUseCase
                   ↓
                UserMapper (inyecta IPokemonClient)
                   ↓
           PokemonHttpAdapter (implementa IPokemonClient)
                   ↓
           RestTemplate + ObjectMapper
                   ↓
           Llamada HTTP a PokéAPI
                   ↓
           Respuesta JSON parseada
                   ↓
           Nombre del Pokémon extraído
                   ↓
           UserWhitPokemonDTO con pokémons enriquecidos
                   ↓
           JSON Response al cliente
```

---

## ✅ Por Qué Esto Es Correcto

### ❌ Si NO hubiera inyección
```java
public class UserMapper {
    private final PokemonHttpAdapter adapter 
        = new PokemonHttpAdapter();  // ❌ Acoplado
}
```
- Imposible testear sin HTTP
- Imposible cambiar a otra implementación
- Código duro de mantener

### ✅ CON inyección
```java
@Component
@AllArgsConstructor
public class UserMapper {
    private final IPokemonClient pokemonClient;  // ✅ Desacoplado
}
```
- Fácil testear con mocks
- Fácil cambiar implementación
- Clean Architecture


