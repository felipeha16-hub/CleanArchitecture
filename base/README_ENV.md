# Base API - Gestión de Usuarios con Pokémons

## 🚀 Configuración de Variables de Entorno

Este proyecto utiliza variables de entorno para la configuración sensible (credenciales BD, puertos, etc.)

### Opción 1: Usar archivo `.env` (Recomendado para desarrollo local)

1. **Copiar el archivo de ejemplo:**
   ```bash
   cp .env.example .env
   ```

2. **Editar `.env` con tus valores:**
   ```env
   DB_URL=jdbc:postgresql://localhost:5432/mi_base_db
   DB_USERNAME=tu_usuario
   DB_PASSWORD=tu_contraseña
   SERVER_PORT=8080
   ```

3. **El archivo `.env` está protegido en `.gitignore`:**
   - ✅ Nunca se subira a Git
   - ✅ Es solo local en tu máquina
   - ✅ Cada desarrollador tiene su propio `.env`

### Opción 2: Variables de entorno del sistema

Si no existe `.env`, Spring Boot usa las variables de entorno del sistema:

**Windows (PowerShell):**
```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/mi_base_db"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "root"
```

**Linux/Mac (Bash):**
```bash
export DB_URL="jdbc:postgresql://localhost:5432/mi_base_db"
export DB_USERNAME="root"
export DB_PASSWORD="root"
```

### Opción 3: Variables de entorno en producción

En producción (Docker, Kubernetes, Cloud), establece las variables en el entorno:

**Docker:**
```dockerfile
ENV DB_URL=jdbc:postgresql://db:5432/mi_base_db
ENV DB_USERNAME=prod_user
ENV DB_PASSWORD=${SECURE_DB_PASSWORD}
```

**Docker Compose:**
```yaml
environment:
  DB_URL: jdbc:postgresql://postgres:5432/mi_base_db
  DB_USERNAME: ${DB_USER}
  DB_PASSWORD: ${DB_PASS}
```

---

## 📋 Variables de Entorno Disponibles

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `DB_URL` | URL de conexión PostgreSQL | `jdbc:postgresql://localhost:5432/mi_base_db` |
| `DB_USERNAME` | Usuario de BD | `root` |
| `DB_PASSWORD` | Contraseña de BD | `root` |
| `DB_DRIVER` | Driver JDBC | `org.postgresql.Driver` |
| `SERVER_PORT` | Puerto del servidor | `8080` |
| `SERVER_SERVLET_CONTEXT_PATH` | Path base de la API | `/api` |
| `JPA_SHOW_SQL` | Mostrar SQL en logs | `true` |
| `JPA_GENERATE_DDL` | Generar DDL automáticamente | `true` |
| `JPA_HIBERNATE_DDL_AUTO` | Estrategia Hibernate | `update` |
| `LOGGING_LEVEL_ROOT` | Nivel de log global | `INFO` |
| `LOGGING_LEVEL_COM_EXAMPLE` | Nivel de log de la app | `DEBUG` |
| `POKEMON_API_TIMEOUT_SECONDS` | Timeout PokéAPI | `10` |
| `POKEMON_API_MAX_CONNECTIONS` | Max conexiones PokéAPI | `50` |
| `APP_NAME` | Nombre de la aplicación | `Base API` |

---

## 🔐 Seguridad

### ✅ Buenas prácticas implementadas:

1. **`.env` en `.gitignore`** - No se commitea credenciales
2. **`.env.example`** - Documentación de variables necesarias
3. **Valores por defecto** - Si falta variable, usa default seguro
4. **System properties** - Las variables se inyectan como properties de Spring

### ⚠️ Nunca:
- Commitear `.env` con credenciales reales
- Usar la misma contraseña en desarrollo y producción
- Loguear valores sensibles (ya está protegido con `@Value` y `@ConfigurationProperties`)

---

## 🔧 Uso en Código

**Spring Boot automáticamente inyecta las variables:**

```java
@Value("${DB_USERNAME}")
private String dbUsername;

@Value("${SERVER_PORT}")
private int serverPort;
```

**Con valores por defecto:**

```java
@Value("${POKEMON_API_TIMEOUT_SECONDS:10}")
private int timeout; // Usa 10 si no está definido
```

---

## 📦 Iniciar la aplicación

```bash
./gradlew bootRun
```

Spring Boot automáticamente:
1. Carga el `.env` (si existe)
2. Inyecta las variables en `application.properties`
3. Usa los valores en la configuración

¡Listo! 🎉

