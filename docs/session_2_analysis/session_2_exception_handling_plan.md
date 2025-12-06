# 🛡️ Implementación de Manejo de Excepciones - Backend

**Documento:** Exception Handling Strategy  
**Fecha:** 27 Noviembre 2025  
**Status:** Plan de implementación para Session 3

---

## 📋 Resumen Ejecutivo

Implementar un sistema robusto de manejo de excepciones en el backend Java/Spring Boot que:
- Valide todos los inputs
- Maneje errores de forma controlada
- Retorne respuestas HTTP consistentes
- Registre errors en logs
- No exponga información sensible

---

## 🏗️ Arquitectura de Excepciones

### 1. Custom Exception Classes

Crear excepciones personalizadas en `backend/src/main/java/io/sunbit/app/exception/`:

```java
// RuntimeException personalizadas

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}

public class InternalServerException extends RuntimeException {
    public InternalServerException(String message) {
        super(message);
    }
}
```

### 2. Global Exception Handler

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ErrorResponse.builder()
            .status(404)
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(BadRequestException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ErrorResponse.builder()
            .status(400)
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorized(UnauthorizedException ex) {
        log.warn("Unauthorized access: {}", ex.getMessage());
        return ErrorResponse.builder()
            .status(401)
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ErrorResponse.builder()
            .status(500)
            .message("An unexpected error occurred")
            .timestamp(LocalDateTime.now())
            .build();
    }
}
```

### 3. Error Response DTO

```java
@Data
@Builder
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors;  // Para múltiples errores
}
```

---

## ✅ Validaciones por Nivel

### 1. Controller Layer (Input Validation)

```java
@RestController
@RequestMapping("/api/v1/position")
@Validated
public class PositionControllerImpl implements IPositionController {

    @PostMapping
    public ResponseEntity<PositionDto> createPosition(
            @Valid @RequestBody PositionDto positionDto) {
        
        if (positionDto.getName() == null || positionDto.getName().trim().isEmpty()) {
            throw new BadRequestException("Position name cannot be empty");
        }
        
        if (positionDto.getName().length() > 100) {
            throw new BadRequestException("Position name must be less than 100 characters");
        }
        
        return ResponseEntity.ok(positionService.save(positionDto));
    }
}
```

### 2. Service Layer (Business Logic Validation)

```java
@Service
@Slf4j
public class PositionServiceImpl implements IPositionService {

    @Autowired
    private IPositionDao positionDao;

    @Override
    public PositionDto findById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid position ID");
        }

        return positionDao.findById(id)
            .map(this::convertToDto)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Position with ID " + id + " not found"
            ));
    }

    @Override
    public List<PositionDto> findAll() {
        try {
            return positionDao.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching positions", e);
            throw new InternalServerException("Failed to retrieve positions");
        }
    }
}
```

### 3. DAO Layer (Data Access Validation)

```java
@Repository
public class PositionDaoImpl implements IPositionDao {

    @Autowired
    private PositionRepository positionRepository;

    @Override
    public void save(Position position) {
        try {
            if (position == null) {
                throw new BadRequestException("Position object cannot be null");
            }
            positionRepository.save(position);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation: {}", e.getMessage());
            throw new BadRequestException("Invalid position data");
        } catch (Exception e) {
            log.error("Error saving position", e);
            throw new InternalServerException("Failed to save position");
        }
    }
}
```

---

## 🎯 Flujo de Validación

```
User Request
    ↓
┌─────────────────────────────────────────┐
│ CONTROLLER VALIDATION                   │
│ - Check null inputs                     │
│ - Validate string length                │
│ - Validate numeric ranges               │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│ SERVICE LAYER VALIDATION                │
│ - Business rule validation              │
│ - ID existence checks                   │
│ - Authority/permission checks           │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│ DAO/DATABASE OPERATIONS                 │
│ - Execute query/insert/update           │
│ - Catch database exceptions             │
└─────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────┐
│ EXCEPTION HANDLER (GlobalExceptionHandler)
│ - Catch any thrown exception            │
│ - Log error details                     │
│ - Return HTTP response                  │
└─────────────────────────────────────────┘
    ↓
HTTP Response (JSON with status code)
```

---

## 📝 Implementación para Cada Entidad

### Employee Controller Mejorado

```java
@RestController
@RequestMapping("/api/v1/employee")
@Slf4j
public class EmployeeControllerImpl implements IEmployeeController {

    @Autowired
    private IEmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAll() {
        try {
            List<EmployeeDto> employees = employeeService.findAll();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Error fetching employees", e);
            throw new InternalServerException("Failed to retrieve employees");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid employee ID: " + id);
        }
        return ResponseEntity.ok(employeeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> create(@Valid @RequestBody EmployeeDto employeeDto) {
        validateEmployeeDto(employeeDto);
        EmployeeDto created = employeeService.save(employeeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeDto employeeDto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid employee ID");
        }
        validateEmployeeDto(employeeDto);
        return ResponseEntity.ok(employeeService.update(id, employeeDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid employee ID");
        }
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void validateEmployeeDto(EmployeeDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BadRequestException("Employee name cannot be empty");
        }
        if (dto.getEmail() == null || !dto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new BadRequestException("Invalid email format");
        }
        if (dto.getPositionId() == null) {
            throw new BadRequestException("Position ID is required");
        }
        if (dto.getSalary() == null || dto.getSalary().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Salary must be greater than 0");
        }
    }
}
```

---

## 🔐 Excepciones Especializadas

### Expense Entity Validations

```java
@Override
public ExpenseDto save(ExpenseDto expenseDto) {
    // Validación 1: Campos obligatorios
    if (expenseDto.getDescription() == null || expenseDto.getDescription().trim().isEmpty()) {
        throw new BadRequestException("Expense description cannot be empty");
    }

    // Validación 2: Rangos numéricos
    if (expenseDto.getAmount() == null || expenseDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        throw new BadRequestException("Expense amount must be greater than 0");
    }

    // Validación 3: Referencias existentes
    if (!employeeDao.existsById(expenseDto.getEmployeeId())) {
        throw new ResourceNotFoundException(
            "Employee with ID " + expenseDto.getEmployeeId() + " not found"
        );
    }

    // Validación 4: Reglas de negocio
    if (expenseDto.getAmount().compareTo(new BigDecimal("100000")) > 0) {
        log.warn("Large expense detected: {}", expenseDto.getAmount());
        // Opcional: Requiere aprobación especial
    }

    return expenseDao.save(expenseDto);
}
```

### Payroll Entity Validations

```java
@Override
public PayrollDto save(PayrollDto payrollDto) {
    // Validación 1: Campos obligatorios
    if (payrollDto.getMonth() == null) {
        throw new BadRequestException("Payroll month cannot be null");
    }

    // Validación 2: Formato de mes (YYYY-MM)
    if (!payrollDto.getMonth().matches("^\\d{4}-\\d{2}$")) {
        throw new BadRequestException("Invalid month format. Use YYYY-MM");
    }

    // Validación 3: No permitir pagos futuros
    YearMonth requestedMonth = YearMonth.parse(payrollDto.getMonth());
    if (requestedMonth.isAfter(YearMonth.now())) {
        throw new BadRequestException("Cannot create payroll for future months");
    }

    // Validación 4: Verificar que empleado existe
    if (!employeeDao.existsById(payrollDto.getEmployeeId())) {
        throw new ResourceNotFoundException(
            "Employee not found: " + payrollDto.getEmployeeId()
        );
    }

    // Validación 5: No duplicados
    if (payrollDao.existsByMonthAndEmployeeId(payrollDto.getMonth(), payrollDto.getEmployeeId())) {
        throw new BadRequestException(
            "Payroll already exists for month " + payrollDto.getMonth()
        );
    }

    return payrollDao.save(payrollDto);
}
```

---

## 📊 HTTP Response Mapping

| Scenario | HTTP Status | Exception | Message |
|----------|------------|-----------|---------|
| Recurso no encontrado | 404 | ResourceNotFoundException | "Position with ID X not found" |
| Input inválido | 400 | BadRequestException | "Position name cannot be empty" |
| No autorizado | 401 | UnauthorizedException | "Access denied" |
| Conflicto de datos | 409 | BadRequestException | "Record already exists" |
| Error interno | 500 | InternalServerException | "An unexpected error occurred" |

---

## 📋 Checklist de Implementación

### Session 3 - Implementar Excepciones

- [ ] Crear carpeta `exception/` en backend
- [ ] Crear clases: `ResourceNotFoundException`, `BadRequestException`, `UnauthorizedException`, `InternalServerException`
- [ ] Crear clase `GlobalExceptionHandler` con `@RestControllerAdvice`
- [ ] Crear DTO `ErrorResponse`
- [ ] Actualizar `PositionControllerImpl` con validaciones
- [ ] Actualizar `EmployeeControllerImpl` con validaciones
- [ ] Actualizar `ExpenseControllerImpl` con validaciones
- [ ] Actualizar `PayrollControllerImpl` con validaciones
- [ ] Agregar validaciones en Service layer
- [ ] Agregar try-catch en DAO layer
- [ ] Crear tests para excepciones
- [ ] Documentar en README

---

## 🧪 Testing de Excepciones

```java
@SpringBootTest
class ExceptionHandlingTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCreatePositionWithoutName_ShouldReturn400() {
        PositionDto invalidPosition = new PositionDto();
        invalidPosition.setDescription("No name");

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
            "/api/v1/position",
            invalidPosition,
            ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage())
            .contains("Position name cannot be empty");
    }

    @Test
    void testGetNonexistentPosition_ShouldReturn404() {
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
            "/api/v1/position/99999",
            ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
```

---

## 📝 Logging Strategy

```java
@Slf4j
public class MyService {

    // INFO: Operaciones normales
    log.info("Position created with ID: {}", position.getId());

    // WARN: Operaciones sospechosas pero no críticas
    log.warn("Large salary detected for employee: {}", employeeId);

    // ERROR: Errores que requieren atención
    log.error("Failed to save position", exception);

    // DEBUG: Información detallada para debugging
    log.debug("Validating position with name: {}", name);
}
```

---

## 🎯 Próximos Pasos

1. **Session 3:** Implementar todas las excepciones
2. **Session 4:** Agregar autenticación/autorización
3. **Session 5:** Implementar auditoría y logging avanzado

---

**Documento:** Exception Handling Implementation Plan  
**Próxima acción:** Implementar en Session 3
