# Activación de perfiles en Spring Boot

Spring Boot permite gestionar múltiples entornos (dev, test, prod) usando perfiles y archivos de configuración específicos:

## Ubicación de los archivos
- `src/main/resources/`: para archivos de configuración de la aplicación (dev, prod, default).
- `src/test/resources/`: para archivos de configuración específicos de tests (`application-test.properties`).

## Formas de activar un perfil

### 1. Variable de entorno
```bash
export SPRING_PROFILES_ACTIVE=dev
export SPRING_PROFILES_ACTIVE=prod
export SPRING_PROFILES_ACTIVE=test
```

### 2. Argumento JVM
```bash
java -jar app.jar --spring.profiles.active=dev
```

### 3. En application.properties (no recomendado para prod)
```properties
spring.profiles.active=dev
```

## En los tests
- Usa la anotación `@ActiveProfiles("test")` en las clases de test para forzar el uso de `application-test.properties`.

---

**Recomendación:**
- Usa siempre variables de entorno o argumentos JVM para mayor flexibilidad y seguridad.
- Mantén los archivos de test en `src/test/resources/` y los de la app en `src/main/resources/`.
