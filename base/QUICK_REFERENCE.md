# 🔍 REFERENCIA RÁPIDA - Inyección en UserMapper

## TU PREGUNTA
> "¿Por qué se hace una instancia de UserMapper de IPokemonClient?"

## RESPUESTA DE UNA LÍNEA
**Porque UserMapper necesita la funcionalidad de obtener pokémons, y es mejor inyectarla (recibir de Spring) que crearla internamente.**

---

## CÓDIGO CORRECTO (Copia y Pega)

```java
@Component                      // ← Necesario 1
@AllArgsConstructor             // ← Necesario 2
public class UserMapper {

    private final IPokemonClient pokemonClient;  // ← Inyección
    
    public UserWhitPokemonDTO toUserWithPokemonDTO(User user) {
        List<String> names = pokemonClient.getPokemonNamesByIds(
            Arrays.asList(user.getPokemonsIds())
        );
        List<UserWhitPokemonDTO.PokemonResponseDTO> pokemons = 
            IntStream.range(0, user.getPokemonsIds().length)
                .mapToObj(i -> new UserWhitPokemonDTO.PokemonResponseDTO(
                    user.getPokemonsIds()[i],
                    names.get(i)
                ))
                .toList();
        return new UserWhitPokemonDTO(user.getId(), user.getUsername(), 
                                      user.getEmail(), pokemons);
    }
}
```

---

## LAS 3 COSAS IMPORTANTES

### 1. `@Component` 
```java
@Component                   // ← Spring crea bean automáticamente
public class UserMapper { }
```

**¿Qué hace?** Spring gestiona este objeto. Lo crea, mantiene en memoria, lo reutiliza.

### 2. `@AllArgsConstructor`
```java
@AllArgsConstructor          // ← Lombok genera constructor
public class UserMapper {
    // Genera: public UserMapper(IPokemonClient pokemonClient) { }
}
```

**¿Qué hace?** Crea automáticamente constructor con los parámetros de las variables `final`.

### 3. `private final IPokemonClient`
```java
@Component
@AllArgsConstructor
public class UserMapper {
    private final IPokemonClient pokemonClient;  // ← Esto es la inyección
}
```

**¿Qué hace?** Spring ve que necesita `IPokemonClient`, busca quién la implementa, y la inyecta automáticamente.

---

## FLUJO EN 5 PASOS

```
1. Spring ve: "Necesito UserMapper"
   ↓
2. Spring ve: "UserMapper necesita IPokemonClient"
   ↓
3. Spring busca: "¿Quién implementa IPokemonClient?"
   Encuentra: "PokemonHttpAdapter"
   ↓
4. Spring crea: PokemonHttpAdapter (si lo necesita)
   ↓
5. Spring inyecta: En el constructor de UserMapper
   ↓
6. ✅ UserMapper listo con pokemonClient inicializado
```

---

## ¿POR QUÉ FUNCIONA?

```
Sin inyección ❌
    UserMapper crea: new PokemonHttpAdapter()
    → Acoplado a PokemonHttpAdapter
    → Imposible cambiar
    → Imposible testear

Con inyección ✅
    UserMapper recibe: IPokemonClient
    → Solo necesita la interfaz
    → Fácil cambiar
    → Fácil testear con mocks
```

---

## CÓMO VERIFICAR QUE FUNCIONA

### Opción 1: Ver en Swagger
```
1. Iniciar: ./gradlew bootRun
2. Abrir: http://localhost:8080/swagger-ui.html
3. Probar: GET /api/users/{id}
4. Ver respuesta con pokémons enriquecidos
```

### Opción 2: Ver en Logs
```
INFO  PokemonHttpAdapter - Fetching pokemon from: https://pokeapi.co/api/v2/pokemon/1
INFO  PokemonHttpAdapter - Retrieved pokemon with id: 1 and name: bulbasaur
```

### Opción 3: Ver en Test
```java
@Test
public void test() {
    IPokemonClient mock = Mockito.mock(IPokemonClient.class);
    UserMapper mapper = new UserMapper(mock);  // ← Inyecta mock
    // Testea...
}
```

---

## LAS 3 RESPONSABILIDADES

| Clase | Responsabilidad | Ubicación |
|-------|-----------------|-----------|
| **UserMapper** | Mapear datos de User a UserWhitPokemonDTO | `application/mapper/` |
| **IPokemonClient** | Definir qué se puede hacer con pokémons | `infrastructure/clients/` |
| **PokemonHttpAdapter** | Implementar cómo obtener pokémons (HTTP) | `infrastructure/clients/adapter/` |

**Cada uno hace una cosa → Responsabilidad Única**

---

## CAMBIAR DE IMPLEMENTACIÓN (Ejemplo)

### Si quiero cambiar de HTTP a WebClient

**SIN Inyección:**
```java
// UserMapper.java
private final PokemonWebClientAdapter pokemonClient = new PokemonWebClientAdapter();
// ❌ Tengo que editar UserMapper
```

**CON Inyección:**
```java
// PokemonClientConfig.java
@Bean
public IPokemonClient pokemonClient() {
    return new PokemonWebClientAdapter();  // ← Cambio aquí
    // return new PokemonHttpAdapter();  // ← Comentado
}
// ✅ UserMapper no se modifica
```

---

## PRINCIPIOS CUMPLIDOS

| Principio | ¿Cumplido? | Por qué |
|-----------|-----------|--------|
| **DIP** (Dependency Inversion) | ✅ | Depende de IPokemonClient, no de PokemonHttpAdapter |
| **SRP** (Single Responsibility) | ✅ | UserMapper solo mapea, no gestiona HTTP |
| **OCP** (Open/Closed) | ✅ | Abierto a agregar nuevas implementaciones |
| **Clean Architecture** | ✅ | Separación clara de capas |

---

## TEST UNITARIO EJEMPLO

```java
@Test
public void testToUserWithPokemonDTO_shouldMapCorrectly() {
    // 1. ARRANGE: Crear un mock
    IPokemonClient mockClient = Mockito.mock(IPokemonClient.class);
    Mockito.when(mockClient.getPokemonNamesByIds(Arrays.asList(1L)))
           .thenReturn(Arrays.asList("bulbasaur"));
    
    // 2. INJECT: Inyectar el mock
    UserMapper mapper = new UserMapper(mockClient);
    
    // 3. ACT: Llamar el método
    User user = new User(1L, "juan", "test@example.com", "pass", new Long[]{1L});
    UserWhitPokemonDTO result = mapper.toUserWithPokemonDTO(user);
    
    // 4. ASSERT: Verificar
    assertEquals("bulbasaur", result.getPokemons().get(0).getName());
    
    // 5. VERIFY: Confirmar que se llamó
    Mockito.verify(mockClient).getPokemonNamesByIds(Arrays.asList(1L));
}
```

**Con inyección, esto es POSIBLE ✅**
**Sin inyección, esto es IMPOSIBLE ❌**

---

## COMANDOS ÚTILES

```bash
# Compilar
./gradlew clean build

# Ver errores
./gradlew compileJava

# Ejecutar tests
./gradlew test

# Iniciar aplicación
./gradlew bootRun

# Ver swagger
http://localhost:8080/swagger-ui.html
```

---

## CHEATSHEET DE ANOTACIONES

```java
@Component              // Marca como bean (Spring la gestiona)
@AllArgsConstructor     // Crea constructor con todos los @final
@Autowired              // Inyecta automáticamente (alternativa)
@Bean                   // Define bean en @Configuration
@Configuration          // Clase de configuración
@Service                // Marca como servicio (@Component especializado)
@Repository             // Marca como repositorio (@Component especializado)
```

---

## RESUMEN EN 3 FRASES

1. **UserMapper inyecta `IPokemonClient`** porque necesita la funcionalidad de obtener pokémons.

2. **Spring lo hace automáticamente** gracias a `@Component` y `@AllArgsConstructor`.

3. **Esto cumple Clean Architecture** porque `IPokemonClient` es una interfaz (puerto) que abstrae la implementación.

---

## ESTADO ACTUAL

✅ **BUILD SUCCESSFUL**
✅ **Código correcto**
✅ **Cumple Clean Architecture**
✅ **Cumple SOLID**
✅ **No necesita cambios**

---

## LECTURA RECOMENDADA

| Documento | Para qué |
|-----------|----------|
| **INDEX.md** | Navegar toda la documentación |
| **ANSWER_TO_YOUR_QUESTION.md** | Respuesta completa |
| **WITH_VS_WITHOUT_INJECTION.md** | Ver diferencia visual |
| **CORRECT_CODE_STRUCTURE.md** | Ver el código completo |

---

## ¿PREGUNTAS?

Si tienes dudas sobre:
- **Cómo funciona la inyección** → Ver INDEX.md → DEPENDENCY_INJECTION_GUIDE.md
- **El código específico** → Ver INDEX.md → CORRECT_CODE_STRUCTURE.md
- **El flujo completo** → Ver INDEX.md → INJECTION_FLOW_VISUALIZATION.md

**Tu implementación es excelente. Sigue así.** 👍


