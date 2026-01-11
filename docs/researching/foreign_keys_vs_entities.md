# Evaluación de claves foráneas frente a entidades embebidas en tablas relacionales

## Introducción
En la modelación de bases de datos relacionales, existe un debate recurrente entre almacenar únicamente identificadores (claves foráneas) que referencian a entidades relacionadas o persistir, dentro de la misma fila, la información de dichas entidades. Este informe examina las implicaciones de cada enfoque aplicadas al caso de payroll, donde se debe decidir si la tabla `payroll` almacena solo `employee_id` y `expense_user_id` o si incorpora datos completos de empleado y usuario de gastos.

## Contexto del problema
El sistema BizFlow ERP utiliza PostgreSQL con un modelo orientado a dominios, donde `Payroll` mantiene relaciones `Many-to-One` tanto con `Employee` como con `ExpenseUser`. Actualmente, la tabla `payroll` almacena únicamente los identificadores (`employee_id`, `expense_user_id`) y la capa de servicio se encarga de completar las entidades. El usuario plantea si sería más profesional persistir las entidades completas en la misma tabla.

## Alternativa A: Uso de claves foráneas (identificadores)
### Ventajas
- **Integridad referencial**: Las claves foráneas garantizan, mediante restricciones nativas del motor, que cualquier nómina apunte a un empleado y usuario válidos, evitando registros huérfanos.
- **Normalización**: Mantiene el modelo en 3FN o BCNF, reduciendo redundancia y asegurando que cada atributo resida en la tabla donde tiene su dependencia funcional primaria.
- **Actualizaciones eficientes**: Cambios en la información del empleado o del usuario se realizan una sola vez en sus tablas respectivas, propagándose automáticamente a todas las nóminas relacionadas sin operaciones masivas.
- **Indices compactos**: Los índices sobre identificadores son más pequeños y rápidos, mejorando el rendimiento de `JOIN`, filtros y planes de ejecución.
- **Consistencia histórica controlada**: Es posible combinar el almacenamiento de IDs con tablas de auditoría/versionado si se requiere congelar ciertos atributos, sin sacrificar la integridad global.

### Desventajas
- **Necesidad de JOIN frecuentes**: Consultas que requieran datos combinados deben realizar `JOIN`, lo que incrementa complejidad y, en escenarios de reportes masivos, puede añadir carga al planificador.
- **Dependencia del diseño de relaciones**: Requiere un esquema bien normalizado y alineado con la capa ORM para evitar inconsistencias lógicas o duplicaciones accidentales.
- **Mayor complejidad en caché**: Al trabajar con IDs, las capas intermedias (ORM, cachés de aplicación) deben resolver relaciones, pudiendo generar consultas adicionales si no se optimizan `fetch` o `entity graphs`.

## Alternativa B: Almacenamiento de entidades completas
### Ventajas
- **Lecturas auto-contenidas**: Una sola consulta a `payroll` devuelve toda la información sin `JOIN`, útil para reporting directo o exportaciones densas.
- **Desacoplo temporal**: Permite congelar instantáneamente datos históricos (por ejemplo, nombre y cargo del empleado en la fecha de la nómina) sin depender de mecanismos adicionales.
- **Simplicidad en sistemas legados**: Algunos sistemas heredados o integraciones planas prefieren tablas desnormalizadas para reducir el número de consultas.

### Desventajas
- **Redundancia y riesgo de inconsistencia**: La misma información se duplicaría en todas las nóminas. Cualquier cambio en el empleado (apellido, posición) requeriría actualizar miles de filas o aceptar datos desalineados.
- **Incremento de almacenamiento**: Almacenar entidades completas multiplica el espacio ocupado y puede afectar tanto almacenamiento como rendimiento de cachés y backups.
- **Violación de normalización**: Se rompe la dependencia funcional, dificultando mantenibilidad, integridad y migraciones futuras.
- **Complejidad en escrituras**: Insertar o actualizar una nómina exigiría copiar múltiples columnas del empleado y del usuario, aumentando la lógica de negocio y el riesgo de errores humanos o de sincronización.
- **Índices más pesados**: Columnas duplicadas dificultan el diseño de índices compuestos y pueden degradar el rendimiento de escrituras (`INSERT/UPDATE`).

## Requerimientos complementarios si se opta por entidades embebidas
- **Triggers o jobs de sincronización**: Serían necesarios para mantener consistencia entre tablas, añadiendo latencia y complejidad operativa.
- **Estrategia de versionado**: Habría que definir qué atributos se congelan y cuáles siguen siendo referencias, lo que conlleva separar columnas y acuerdos con el equipo de datos.
- **Revisión de ORM**: Frameworks como JPA están orientados a referencias; almacenar datos replicados exigiría DTO específicos y bypass de entidades administradas.

## Consideraciones de rendimiento y mantenimiento
- **Lecturas**: Las consultas OLTP típicas se benefician de tablas compactas con claves foráneas. La desnormalización suele reservarse a data warehouses o vistas materializadas.
- **Escrituras**: El uso de IDs mantiene operaciones `INSERT` y `UPDATE` ágiles. Embebido puede triplicar la cantidad de columnas afectadas y generar más bloqueos.
- **Mantenibilidad**: Un modelo normalizado es más fácil de evolucionar, versionar y documentar. La desnormalización requiere disciplina extra para evitar “datos zombies”.
- **Auditoría**: Si se necesita capturar el estado histórico del empleado al generar la nómina, es preferible usar una tabla de historial (`employee_history`) o campos específicos (por ejemplo, `snapshot_position`), manteniendo los IDs como referencia primaria.

## Recomendaciones
1. **Mantener claves foráneas en payroll** para `employee_id` y `expense_user_id`, asegurando integridad y evitando duplicaciones.
2. **Introducir snapshots selectivos** solo para atributos que deban congelarse (p.ej., nombre en el recibo) mediante campos derivados o tablas de auditoría.
3. **Optimizar consultas** con vistas o proyecciones (`DTO`) desde la capa de aplicación, permitiendo exponer tanto IDs como datos enriquecidos sin alterar el modelo relacional.
4. **Documentar contratos de servicio** para que los clientes de la API conozcan que deben enviar IDs y, si la entidad es nula, el servicio realizará la reasignación, tal como ocurre con `ExpenseUser`.

## Conclusión
Guardar únicamente las claves foráneas en la tabla `payroll` es la aproximación profesional y alineada con las buenas prácticas relacionales. Embebiendo entidades completas se sacrifica normalización, se eleva el coste de mantenimiento y se introduce riesgo de inconsistencias. Cuando se necesite contexto histórico, conviene complementarlo con mecanismos de snapshot o auditoría, manteniendo la referencia mediante IDs como fuente de verdad.
