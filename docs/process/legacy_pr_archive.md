# Archivo de Pull Requests Legado

Esta nota documenta los pull requests generados por Copilot que permanecieron en borrador y se cerraron el 2026-01-05 para evitar volver a integrar código obsoleto en el proyecto. Cada PR hacía referencia a instantáneas antiguas del repositorio que chocan con el estado actual de la rama `dev`. Se prevé reabrir temporalmente los hilos enlazados para dejar un comentario de cierre que registre el motivo.

| PR | Rama origen | Rama destino | Resolución | Notas |
|----|--------------|--------------|------------|-------|
| [#3](https://github.com/jdomdev/bizflow-erp-springboot-react-docker/pull/3) | `copilot/improve-employee-crud-module` | `main` | Cerrado | Sustituido por el trabajo de CRUD de empleados ya integrado en `dev`. |
| [#5](https://github.com/jdomdev/bizflow-erp-springboot-react-docker/pull/5) | `copilot/audit-role-permission-management` | `main` | Cerrado | Reemplazado por la implementación actual de seguridad/autenticación en `dev`. |
| [#7](https://github.com/jdomdev/bizflow-erp-springboot-react-docker/pull/7) | `copilot/add-expense-notes-persistence` | `main` | Cerrado | Entraba en conflicto con el módulo de gastos consolidado en `dev`. |
| [#9](https://github.com/jdomdev/bizflow-erp-springboot-react-docker/pull/9) | `copilot/finalize-expenses-payroll-dashboard` | `feat/session-6-dashboard-expenses-payroll` | Cerrado | Las novedades del dashboard ya están cubiertas por la línea base de `dev`. |
| [#11](https://github.com/jdomdev/bizflow-erp-springboot-react-docker/pull/11) | `copilot/optimize-ci-cd-workflows` | `main` | Cerrado | Se descartó la estructura de CI/CD heredada en favor del pipeline actual. |
| [#13](https://github.com/jdomdev/bizflow-erp-springboot-react-docker/pull/13) | `copilot/update-documentation-technical` | `main` | Cerrado | La revisión de documentación ya está cubierta por los últimos cambios en `dev`. |

## Lista de seguimiento
- [ ] Reabrir cada PR, añadir el comentario de cierre y volver a cerrarlo.
- [ ] Eliminar las ramas sin uso (`copilot/*`) una vez registrados los comentarios y con el visto bueno del equipo.
- [ ] Mantener este documento al día si se identifican más PR históricos.
