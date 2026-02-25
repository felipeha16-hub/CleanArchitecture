# Ejemplos de Prueba - Integración Pokémon API

## 1. Crear un Usuario con Pokémons

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan",
    "email": "juan@example.com",
    "password": "securepass123",
    "pokemonsIds": [1, 4, 7, 25, 39]
  }'
```

**Respuesta esperada:**
```json
{
  "id": 1,
  "username": "juan",
  "email": "juan@example.com",
  "pokemonsIds": [1, 4, 7, 25, 39]
}
```

---

## 2. Obtener Usuario SIN Pokémons Enriquecidos

```bash
curl -X GET http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json"
```

**Respuesta:**
```json
{
  "id": 1,
  "username": "juan",
  "email": "juan@example.com",
  "pokemonsIds": [1, 4, 7, 25, 39]
}
```

---

## 3. Obtener Usuario CON Pokémons Enriquecidos ⭐

```bash
curl -X GET http://localhost:8080/api/users/1/with-pokemons \
  -H "Content-Type: application/json"
```

**Respuesta esperada:**
```json
{
  "id": 1,
  "username": "juan",
  "email": "juan@example.com",
  "pokemons": [
    {
      "id": 1,
      "name": "bulbasaur"
    },
    {
      "id": 4,
      "name": "charmander"
    },
    {
      "id": 7,
      "name": "squirtle"
    },
    {
      "id": 25,
      "name": "pikachu"
    },
    {
      "id": 39,
      "name": "jigglypuff"
    }
  ]
}
```

---

## 4. Obtener Todos los Usuarios

```bash
curl -X GET http://localhost:8080/api/users \
  -H "Content-Type: application/json"
```

---

## 5. Actualizar Usuario (PATCH)

```bash
curl -X PATCH http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan_actualizado",
    "email": "juan_nuevo@example.com"
  }'
```

---

## 6. Eliminar Usuario

```bash
curl -X DELETE http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json"
```

---

## 🧪 Probando con Postman

### Importar Colección

1. Abre Postman
2. Click en "Import"
3. Pega el siguiente JSON:

```json
{
  "info": {
    "name": "Pokemon User API",
    "description": "API de usuarios con integración de Pokémon",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Crear Usuario",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"username\": \"juan\",\n  \"email\": \"juan@example.com\",\n  \"password\": \"securepass123\",\n  \"pokemonsIds\": [1, 4, 7, 25, 39]\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/users",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "users"]
        }
      }
    },
    {
      "name": "Obtener Usuario (sin pokémons)",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/users/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "users", "1"]
        }
      }
    },
    {
      "name": "Obtener Usuario (con pokémons) ⭐",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/users/1/with-pokemons",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "users", "1", "with-pokemons"]
        }
      }
    },
    {
      "name": "Obtener Todos los Usuarios",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/users",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "users"]
        }
      }
    },
    {
      "name": "Actualizar Usuario",
      "request": {
        "method": "PATCH",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"username\": \"juan_actualizado\",\n  \"email\": \"juan_nuevo@example.com\"\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/users/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "users", "1"]
        }
      }
    },
    {
      "name": "Eliminar Usuario",
      "request": {
        "method": "DELETE",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/users/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "users", "1"]
        }
      }
    }
  ]
}
```

---

## 📊 IDs de Pokémons Populares para Probar

| ID | Nombre |
|----|--------|
| 1 | Bulbasaur |
| 4 | Charmander |
| 7 | Squirtle |
| 25 | Pikachu |
| 39 | Jigglypuff |
| 54 | Psyduck |
| 58 | Growlithe |
| 63 | Abra |
| 66 | Machop |
| 74 | Geodude |
| 90 | Shellder |
| 109 | Koffing |
| 133 | Eevee |
| 147 | Dratini |
| 152 | Chikorita |

---

## 🔍 Verificar Logs

Para ver los logs de las llamadas a PokéAPI:

```bash
# En la consola de la aplicación verás algo como:
# [main] INFO  PokemonHttpAdapter - Fetching pokemon from: https://pokeapi.co/api/v2/pokemon/1
# [main] INFO  PokemonHttpAdapter - Retrieved pokemon with id: 1 and name: bulbasaur
```

---

## ⚡ Performance Notes

- **Primera llamada:** ~5-15 segundos (depende de PokéAPI)
- **Llamadas posteriores:** Si implementas caché, será instantáneo
- **Timeout configurable:** Edita `PokemonClientConfig.java` si es necesario

---

## 🚀 Siguientes Pasos

1. **Implementar Caché:** Usar Redis o Caffeine para no llamar a PokéAPI cada vez
2. **Agregar validación:** Validar que los IDs de pokémons existan
3. **Manejo de errores mejorado:** Devolver errores amigables si PokéAPI falla
4. **Tests unitarios:** Mockear PokemonHttpAdapter en tests
5. **Documentación Swagger:** Swagger ya muestra los nuevos endpoints automáticamente


