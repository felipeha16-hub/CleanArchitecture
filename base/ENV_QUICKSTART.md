# 🚀 Guía Rápida: Variables de Entorno

## Setup en 3 pasos:

### 1️⃣ Copiar archivo de ejemplo
```bash
cp .env.example .env
```

### 2️⃣ Editar `.env` con tus valores (opcional)
```env
# Solo edita si necesitas valores diferentes a los por defecto
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_contraseña
SERVER_PORT=8080
```

### 3️⃣ ¡Listo! Ejecuta la app
```bash
./gradlew bootRun
```

---

## 📍 Ubicación de archivos

| Archivo | Propósito |
|---------|-----------|
| `.env` | **Tu configuración local** (⚠️ NO subir a Git) |
| `.env.example` | Plantilla con todas las variables |
| `application.properties` | Lee variables de `.env` |
| `.gitignore` | Protege `.env` de ser subido |

---

## 🔄 Flujo de carga

```
.env (local)
   ↓
DotEnvConfig (carga automática)
   ↓
System Properties
   ↓
application.properties (${VAR_NAME})
   ↓
Spring Boot ✅
```

---

## ❓ Preguntas frecuentes

**P: ¿Por qué `.env` no se subir a Git?**
R: Porque contiene credenciales sensibles. Está en `.gitignore`.

**P: ¿Cómo otros desarrolladores saben qué variables usar?**
R: Mira `.env.example` - tiene todos los campos necesarios.

**P: ¿Funciona en producción?**
R: Sí. En Docker/Cloud establece variables de entorno y Spring las usa.

**P: ¿Qué pasa si falta una variable?**
R: Usa el valor por defecto definido en `application.properties` (la parte después de `:`)

---

## 📚 Más info
Ver: `README_ENV.md`

