# 📚 Guía: Instancias Inyectadas en UserMapper y Clean Architecture

## 🎯 Tu Pregunta

> "¿Por qué se hace una instancia de UserMapper de IPokemonClient?"
> "¿Si se puede hacer y por qué se hace?"
> "¿Esto cumple con Clean Architecture?"

---

## ✅ Respuesta Corta

**SÍ, es correcto y cumple perfectamente con Clean Architecture.**

UserMapper **inyecta IPokemonClient** porque:
1. Necesita usar la funcionalidad para obtener nombres de pokémons
2. Debería recibir la dependencia del exterior (inyección), no crearla
3. Esto permite usar diferentes implementaciones sin cambiar el código

---

## 🔍 Explicación Detallada

### 1. ¿Qué es Inyección de Dependencias?

**Inyección** = Dar a un objeto sus dependencias desde el exterior, no que las cree él mismo.

#### ❌ SIN Inyección (Anti-patrón)
```java
public class UserMapper {
    // UserMapper crea su propia dependencia ❌ ACOPLADO
    private final PokemonHttpAdapter pokemonClient = new PokemonHttpAdapter();
    
    public void mapPokemon() {
        pokemonClient.getPokemonById(1);
    }
}
```

**Problemas:**
- UserMapper está **acoplado** a `PokemonHttpAdapter`
- Si quiero cambiar a `PokemonWebClientAdapter`, debo modificar UserMapper
- Imposible testear sin hacer llamadas HTTP reales
- Viola principio de responsabilidad única

#### ✅ CON Inyección (Correcto)
```java
@Component
@AllArgsConstructor
public class UserMapper {
    // Spring inyecta la interfaz ✅ DESACOPLADO
    private final IPokemonClient pokemonClient;
    
    public void mapPokemon() {
        pokemonClient.getPokemonById(1);
    }
}
```

**Beneficios:**
- UserMapper depende de **interfaz**, no de implementación
- Puedo cambiar la implementación sin tocar UserMapper
- Fácil testear con mocks
- Cumple inversión de control

---

### 2. Cómo Funciona la Inyección en tu Código

#### Paso 1: Declarar la Dependencia

```java
@Component                    // ← Spring gestiona este bean
@AllArgsConstructor           // ← Crea constructor automáticamente
public class UserMapper {
    
    private final IPokemonClient pokemonClient;  // ← Declarar dependencia
    
    public UserWhitPokemonDTO toUserWithPokemonDTO(User user) {
        List<String> names = pokemonClient.getPokemonNamesByIds(...);
        //                     ↑ Aquí se usa la inyección
    }
}
```

#### Paso 2: Spring Crea el Constructor

Automáticamente Lombok + Spring generan:

```java
public UserMapper(IPokemonClient pokemonClient) {
    this.pokemonClient = pokemonClient;
}
```

#### Paso 3: Spring Busca la Implementación

Cuando necesita crear un `UserMapper`:

```
Spring dice:
1. "Necesito un UserMapper"
2. "UserMapper necesita un IPokemonClient"
3. "¿Quién implementa IPokemonClient?"
4. "¡PokemonHttpAdapter!"
5. "¿PokemonHttpAdapter necesita algo?"
6. "RestTemplate, ObjectMapper"
7. "Los tengo en PokemonClientConfig"
8. "Creo PokemonHttpAdapter → se lo paso a UserMapper"
```

#### Paso 4: Todo Conectado

```java
// En GetUserIdUseCase
@Component
@AllArgsConstructor
public class GetUserIdUseCase {
    private final UserMapper userMapper;  // ← También inyectado
    
    public Optional<UserWhitPokemonDTO> getUserById(Long id) {
        return repository.findById(id)
                .map(userMapper::toUserWithPokemonDTO);  // ← Usa la instancia
    }
}
```

---

### 3. Cadena de Inyecciones (Dependency Chain)

```
GetUserIdUseCase
    ↓ inyecta
    UserMapper
        ↓ inyecta
        IPokemonClient (implementado por →)
            ↓ inyecta
            RestTemplate (desde PokemonClientConfig)
            ObjectMapper (desde PokemonClientConfig)
```

---

## 🏗️ ¿Cumple con Clean Architecture?

### Arquitectura Hexagonal (Puertos y Adaptadores)

```
CAPA DE APLICACIÓN (application/)
├── UserMapper        ← Orquesta transformaciones
│   ├── Depende de → IPokemonClient (PUERTO)
│   └── ✅ Inyecta esta dependencia

CAPA DE DOMINIO (domain/)
├── User             ← Modelo puro sin dependencias

CAPA DE INFRAESTRUCTURA (infrastructure/)
├── PokemonHttpAdapter  ← Implementa IPokemonClient
├── PokemonClientConfig ← Configura beans
└── ✅ Spring las inyecta automáticamente
```

### Principios de Clean Architecture Cumplidos

| Principio | ¿Cumplido? | Por qué |
|-----------|-----------|--------|
| **Separación de capas** | ✅ | UserMapper está en application, implementación en infrastructure |
| **Inversión de dependencias** | ✅ | Depende de IPokemonClient (interfaz), no de adaptador concreto |
| **Inyección de dependencias** | ✅ | Spring maneja el ciclo de vida |
| **Baja acoplación** | ✅ | UserMapper NO depende de PokemonHttpAdapter |
| **Alta cohesión** | ✅ | Cada clase tiene una responsabilidad clara |
| **Testeable** | ✅ | Puedo hacer mocks de IPokemonClient en tests |
| **Flexible** | ✅ | Cambiar implementación sin tocar UserMapper |

---

## 💡 Comparación Visual

### Antes (Acoplado ❌)
```
UserMapper
    ↓ DEPENDE DE
PokemonHttpAdapter (CONCRETO)
    ↓
Imposible cambiar a WebClient sin modificar UserMapper
```

### Después (Desacoplado ✅)
```
UserMapper
    ↓ DEPENDE DE
IPokemonClient (INTERFAZ)
    ↓
    ├→ PokemonHttpAdapter (implementación actual)
    ├→ PokemonWebClientAdapter (implementación futura)
    └→ PokemonMockAdapter (para tests)

Cambiar implementación sin tocar UserMapper ✓
```

---

## 🧪 Ejemplo: Cómo Testear con Inyección

### Sin Inyección (Imposible testear)
```java
public class UserMapper {
    private final PokemonHttpAdapter adapter = new PokemonHttpAdapter();
    // No hay forma de mockear esto ❌
}
```

### Con Inyección (Fácil de testear)
```java
@Test
public void testUserWithPokemons() {
    // Crear un mock
    IPokemonClient mockClient = Mockito.mock(IPokemonClient.class);
    Mockito.when(mockClient.getPokemonNamesByIds(Arrays.asList(1L)))
           .thenReturn(Arrays.asList("bulbasaur"));
    
    // Inyectar el mock
    UserMapper mapper = new UserMapper(mockClient);
    
    // Testear
    User user = new User(1L, "juan", "juan@example.com", "pass", new Long[]{1L});
    UserWhitPokemonDTO result = mapper.toUserWithPokemonDTO(user);
    
    // Verificar
    assertEquals("bulbasaur", result.getPokemons().get(0).getName());
}
```

---

## 📊 Diagrama de Flujo de Inyección

```
┌─────────────────────────────────────────┐
│  Spring ApplicationContext               │
│  (Contenedor de inyección)              │
└────────────┬────────────────────────────┘
             │
             │ "Necesito crear GetUserIdUseCase"
             │
             ├→ "¿GetUserIdUseCase qué necesita?"
             │   └→ Escanea @AllArgsConstructor
             │   └→ Ve: IUserRepository, UserMapper
             │
             ├→ "Creo IUserRepository"
             │   └→ Busca implementación (JpaUserRepository)
             │   └→ Crea instancia
             │
             ├→ "Creo UserMapper"
             │   └→ Ve que necesita IPokemonClient
             │   └→ Busca implementación (PokemonHttpAdapter)
             │   └→ Crea PokemonHttpAdapter (necesita RestTemplate, ObjectMapper)
             │   └→ Crea RestTemplate desde PokemonClientConfig
             │   └→ Crea ObjectMapper desde PokemonClientConfig
             │   └→ Inyecta todo en PokemonHttpAdapter
             │   └→ Inyecta PokemonHttpAdapter en UserMapper
             │
             └→ "Inyecto en GetUserIdUseCase"
                └→ GetUserIdUseCase listo para usar ✓
```

---

## 🎓 Principios Aplicados

### Dependency Inversion Principle (DIP)
> "Depende de abstracciones, no de concreciones"

```java
// ❌ Mal
private final PokemonHttpAdapter adapter;  // Depende de concreto

// ✅ Bien
private final IPokemonClient client;  // Depende de interfaz
```

### Single Responsibility Principle (SRP)
> "Cada clase debe tener una sola razón para cambiar"

```java
UserMapper:
- Solo transforma datos
- NO crea adaptadores
- NO maneja HTTP
- ✓ Una responsabilidad

PokemonHttpAdapter:
- Solo hace llamadas HTTP
- NO transforma datos
- NO maneja lógica de negocio
- ✓ Una responsabilidad
```

### Open/Closed Principle (OCP)
> "Abierto para extensión, cerrado para modificación"

```
Si quiero agregar PokemonWebClientAdapter:
- NO modifico UserMapper
- Solo creo nueva clase que implementa IPokemonClient
- Spring automáticamente usa la nueva implementación
✓ Abierto a extensión
✓ Cerrado a modificación
```

---

## 📝 Resumen

| Pregunta | Respuesta |
|----------|-----------|
| **¿Por qué inyectar?** | Para desacoplar y hacer testeable |
| **¿Se puede hacer?** | SÍ, con `@Component` + `@AllArgsConstructor` |
| **¿Cómo funciona?** | Spring crea el constructor automáticamente |
| **¿Cumple Clean Architecture?** | SÍ, perfectamente |
| **¿Alternativas?** | Setter injection o constructor manual |

---

## 🚀 Ventajas de tu Implementación Actual

✅ **Desacoplada** — UserMapper no depende de PokemonHttpAdapter  
✅ **Testeable** — Fácil mockear IPokemonClient  
✅ **Flexible** — Cambiar implementación sin código duplicado  
✅ **Mantenible** — Responsabilidades claras  
✅ **Escalable** — Fácil agregar nuevas implementaciones  
✅ **Clean Architecture** — Sigue los principios SOLID  

---

## 📚 Referencias

- Martin Fowler - Dependency Injection Pattern
- Robert C. Martin - Clean Architecture
- Spring Framework Documentation - Dependency Injection
- SOLID Principles


