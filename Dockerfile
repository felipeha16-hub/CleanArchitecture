FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copiar configuración de Gradle y wrapper
COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle settings.gradle ./

# Copiar código fuente
COPY src/ src/

# Compilar la aplicación
RUN chmod +x ./gradlew && ./gradlew bootJar --no-daemon

# Exponer puerto
EXPOSE 8080

# Ejecutar la aplicación
#CMD ["java", "-jar", "build/libs/base-0.0.1-SNAPSHOT.jar"]



