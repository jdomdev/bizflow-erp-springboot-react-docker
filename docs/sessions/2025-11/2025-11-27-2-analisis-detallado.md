**Fecha:** 2025-11-27

# Detailed Analysis - Expense Note App

## 📋 TABLA DE CONTENIDOS
1. [Descripción General del Proyecto](#descripción-general-del-proyecto)
2. [Problemas Identificados](#problemas-identificados)
3. [Dependencias Deprecadas](#dependencias-deprecadas)
4. [Violaciones SOLID](#violaciones-solid)
5. [Problemas de Código](#problemas-de-código)
6. [Plan de Acción](#plan-de-acción)

---

## 🎯 Descripción General del Proyecto

**Nombre:** Expense Note App (Sistema de Control de Gastos Empresariales)

**Propósito:** Aplicación backend monolítica que permite a empleados gestionar sus notas de gastos, nóminas y datos personales.

**Tecnologías Actuales:**
- Java JDK 21 (configurado en pom.xml)
- Spring Boot 2.7.18 (DEPRECATED - debe migrar a 3.x)
- PostgreSQL
- JWT para autenticación
- Spring Security 5.8.x

**Arquitectura:** Monolítica con patrón DAO/DTO

---

## 🚨 PROBLEMAS IDENTIFICADOS

### 1. DEPENDENCIAS DEPRECADAS Y OBSOLETAS

#### 1.1 Spring Boot 2.7.18 → DEBE SER 3.3.x
**Problema:**
- Spring Boot 2 llegó al final de soporte el 1 de diciembre de 2023
- Los nuevos proyectos DEBEN usar Spring Boot 3
- Faltan características de seguridad modernas

**Impacto:**
- Vulnerabilidades de seguridad sin parches
- Compatibilidad con bibliotecas modernas limitada
- Falta de optimizaciones de rendimiento

---

#### 1.2 Paquetes `javax.servlet` → DEBEN SER `jakarta.servlet`
**Problema en pom.xml:**
```xml
<!-- ❌ DEPRECATED -->
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.1</version>
</dependency>

<!-- ❌ DEPRECATED -->
<dependency>
    <groupId>org.apache.tomcat</groupId>
    <artifactId>tomcat-servlet-api</artifactId>
    <version>11.0.0-M26</version>
</dependency>
```

**Archivos Afectados:**
- `JwtAuthenticationFilter.java` - línea 5: `import javax.servlet.FilterChain;`
- `JwtAuthenticationFilter.java` - línea 6: `import javax.servlet.ServletException;`
- `CustomAuthenticationEntryPoint.java` - línea 12: `import javax.servlet.ServletException;`
- `CustomAuthenticationEntryPoint.java` - línea 13: `import javax.servlet.http.HttpServletRequest;`
- `CustomAuthenticationEntryPoint.java` - línea 14: `import javax.servlet.http.HttpServletResponse;`

**Impacto:** En Spring Boot 3 se cambió a `jakarta.*`

---

#### 1.3 Spring Framework 5.3.39 vs 6.0.x
**Problema:**
- Spring Framework 5.3 está en mantenimiento
- Spring Boot 3 requiere Spring Framework 6+
- Las APIs han cambiado significativamente

**En pom.xml:**
```xml
<!-- CONFLICTIVO -->
<spring.version>5.3.39</spring.version>
```

---

#### 1.4 Problemas en JwtAuthenticationUtil.java

**Línea 78: Cast incorrecto y método deprecated**
```java
// ❌ PROBLEMA: Conversión incorrecta
((JwtParserBuilder) Jwts.builder()).setSigningKey(key).build().parseClaimsJws(token);

// Debería ser:
Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
```

**Línea 107: Cast incorrecto y método deprecated**
```java
// ❌ PROBLEMA: parseClaimsJws(token) es deprecated
return ((JwtParser) Jwts.parser()
    .setSigningKey(secretKey))
    .parseClaimsJws(token)
    .getBody();

// Debería ser:
return Jwts.parserBuilder()
    .setSigningKey(secretKey)
    .build()
    .parseClaimsJws(token)
    .getBody();
```

**Importaciones Deprecated:**
```java
// ❌ DEPRECATED en JJWT 0.12+
import io.jsonwebtoken.SignatureException;  // Cambiar a io.jsonwebtoken.security.SignatureException

// ❌ DEPRECATED
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
```

---

#### 1.5 JJWT (JWT Library) - Versión 0.12.6
**Problema:**
- Versión compatible con JJWT moderno pero hay cambios de API
- Los imports de `SignatureException` están deprecated
- Los métodos de parsing necesitan refactorización

---

#### 1.6 Hibernate Core 6.6.1
**Problema:**
```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.6.1.Final</version>
    <type>pom</type>  <!-- ❌ POM type es incorrecto -->
</dependency>
```

**Impacto:** No se incluyen las dependencias necesarias de Hibernate correctamente.

---

#### 1.7 Lombok 1.18.30
**Problema:**
- Versión antigua, debería ser 1.18.32 o superior
- Problemas de compatibilidad con Java 21

---

### 2. VIOLACIONES PRINCIPIOS SOLID

#### 2.1 **Single Responsibility Principle (SRP)** - VIOLADO

**Problema en `JwtAuthenticationFilter.java`:**
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Responsabilidad 1: Validar token
    // Responsabilidad 2: Extraer información del token
    // Responsabilidad 3: Establecer contexto de seguridad
    // Responsabilidad 4: Lógica de autorización (checks de admin)
}
```

La clase hace DEMASIADO. Debería dividirse en:
- `TokenValidator` - solo valida tokens
- `TokenExtractor` - solo extrae claims
- `SecurityContextManager` - solo establece contexto
- `JwtAuthenticationFilter` - orquesta

---

#### 2.2 **Open/Closed Principle (OCP)** - VIOLADO

**Problema:** La clase `JwtAuthenticationFilter` está cerrada para extensión:
```java
// No hay forma de agregar nuevas validaciones sin modificar la clase
private boolean hasAuthorizationHeader(HttpServletRequest request) { ... }
private String getAccessToken(HttpServletRequest request) { ... }
```

**Solución:** Crear interfaces estratégicas:
```java
public interface TokenExtractorStrategy {
    String extract(HttpServletRequest request);
}
```

---

#### 2.3 **Liskov Substitution Principle (LSP)** - POTENCIALMENTE VIOLADO

**Problema en `ExpenseUser implements UserDetails`:**
```java
@Override
public boolean isAccountNonExpired() {
    // Retorna true siempre - no hay lógica real
}
```

Subclases no pueden sustituirse correctamente.

---

#### 2.4 **Interface Segregation Principle (ISP)** - VIOLADO

**Problema:** Interfaces demasiado grandes
```java
public interface IExpenseController {
    // Probablemente tiene 10+ métodos
    // Clientes que solo necesitan 2 métodos deben implementar todo
}
```

**Solución:** Dividir en interfaces más pequeñas y específicas

---

#### 2.5 **Dependency Inversion Principle (DIP)** - PARCIALMENTE VIOLADO

**En `JwtAuthenticationFilter.java`:**
```java
@Autowired
private JwtAuthenticationUtil jwtAuthUtil;  // ✓ Inyectado (bien)

// Pero instanciación directa de objetos:
private ExpenseUser tokenUserDetails = new ExpenseUser();  // ❌ Acoplamiento directo
```

---

### 3. PROBLEMAS ESPECÍFICOS DE CÓDIGO

#### 3.1 JwtAuthenticationUtil - Métodos Deprecated
```java
// ❌ SignatureException está en io.jsonwebtoken pero DEPRECATED
import io.jsonwebtoken.SignatureException;

// Debería ser (nueva ubicación):
import io.jsonwebtoken.security.SignatureException;
```

#### 3.2 Code Smell - Debug Print Statements
```java
// ❌ Demasiados System.out.println en producción
System.out.println("Authorization header: " + header);
System.out.println("Access token(JwtAuthFilter.getAccessToken()): " + token);
System.out.println("Method Request: " + request.getMethod());
System.out.println("Request URI: " + request.getRequestURI());
```

**Impacto:** 
- Afecta rendimiento
- Contamina logs
- Información de seguridad expuesta

#### 3.3 CustomAuthenticationEntryPoint - Implementación Incompleta
```java
@Override
public void commence(...) throws IOException, ServletException {
    response.sendError(...);
    throw new UnsupportedOperationException("Unimplemented method 'commence'");  // ❌ NUNCA DEBE LANZAR
}
```

#### 3.4 ExpenseUser - Métodos Deprecated
```java
public static Object withDefaultPasswordEncoder() {
    // ❌ Este método está deprecated en Spring Security
}
```

---

### 4. ARQUITECTURA Y DISEÑO

#### 4.1 Carpeta `util/` Sin Organización
```
util/
├── EmployeeUtil.java
├── ExpenseUserIdCheckUtil.java
└── UserUtil.java
```

**Problema:** 
- Utilidades sueltas sin patrón
- Difícil de mantener
- Acoplamiento oculto

**Solución:** Usar patrón `Strategy` o reorganizar en servicios

---

#### 4.2 DAO vs Service
**Problema:** Posible duplicación de lógica entre DAOs y Services

**Falta:** Implementaciones reales de:
```
service/ (probablemente vacío)
├── IEmployeeService.java
├── EmployeeServiceImpl.java
├── IExpenseService.java
└── ExpenseServiceImpl.java
```

---

## 🔧 DEPENDENCIAS DEPRECADAS - RESUMEN

| Componente | Versión Actual | Versión Recomendada | Razón |
|-----------|-----------------|-------------------|-------|
| Spring Boot | 2.7.18 | 3.3.x | EOL December 2023 |
| Spring Framework | 5.3.39 | 6.1.x | Spring Boot 3 requiere 6+ |
| Spring Security | 5.8.x | 6.1.x+ | Cambios importantes en Boot 3 |
| javax.servlet | 4.0.1 | jakarta.servlet | Java EE → Jakarta EE |
| Lombok | 1.18.30 | 1.18.32+ | Compatibilidad Java 21 |
| Hibernate | 6.6.1 | 6.6.5+ | Parches de seguridad |
| JJWT | 0.12.6 | 0.12.6 | OK pero APIs deprecated |
| Java | 21 | 21 | OK, pero sin parches Boot 2 |

---

## 📝 PLAN DE ACCIÓN COMPLETO

### FASE 1: Actualización de Dependencias (Crítico)
1. Actualizar Spring Boot a 3.3.x
2. Cambiar javax.* a jakarta.*
3. Actualizar Spring Security a 6.1.x
4. Actualizar JJWT con nuevas APIs
5. Corregir Hibernate y Lombok

### FASE 2: Refactorización Código JWT (Importante)
1. Refactorizar JwtAuthenticationFilter
2. Corregir JwtAuthenticationUtil
3. Implementar CustomAuthenticationEntryPoint correctamente
4. Eliminar System.out.println y usar Logger

### FASE 3: Principios SOLID (Importante)
1. Dividir responsabilidades en JwtAuthenticationFilter
2. Crear estrategias para extracción de tokens
3. Implementar segregación de interfaces
4. Invertir dependencias correctamente

### FASE 4: Frontend React (Nuevo)
1. Crear proyecto React con Vite/Next.js
2. Diseño moderno con gradientes
3. Componentes reutilizables
4. Integración con API REST

### FASE 5: Testing y Documentación
1. Crear tests unitarios
2. Tests de integración
3. Documentar cambios
4. API documentation (Swagger)

---

## 📊 RESUMEN DE IMPACTOS

| Área | Severidad | Impacto |
|------|-----------|---------|
| Dependencias | CRÍTICA | Vulnerabilidades de seguridad |
| JWT Parsing | ALTA | Fallos en autenticación |
| SOLID Principles | MEDIA | Dificultad mantenimiento |
| Debug Statements | BAJA | Performance, seguridad |

---

## ✅ SIGUIENTE PASO

Los pasos inmediatos son:
1. **URGENTE**: Actualizar pom.xml con versiones correctas
2. **INMEDIATO**: Refactorizar JWT authentication
3. **IMPORTANTE**: Aplicar principios SOLID
4. **DESARROLLO**: Crear frontend React

---
