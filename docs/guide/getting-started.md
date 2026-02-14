# Empezando con Bizflow ERP

Bizflow ERP es un sistema de gestión empresarial moderno que te permite administrar gastos, empleados y nóminas de manera eficiente.

## Prerrequisitos

Antes de comenzar, asegúrate de tener instalado:

- **Docker Desktop** (v20+) o Docker Engine + Docker Compose
- **Git** para clonar el repositorio
- **Make** (opcional, para comandos simplificados)

## Instalación Rápida

```bash
# Clonar el repositorio
git clone https://github.com/jdomdev/bizflow-erp-springboot-react-docker.git
cd bizflow-erp-springboot-react-docker

# Iniciar en modo desarrollo
make dev
```

Si no tienes Make instalado:

```bash
docker compose --profile dev up -d
```

## Acceso a la Aplicación

Una vez iniciados los servicios:

| Servicio | URL | Descripción |
|----------|-----|-------------|
| Frontend | http://localhost:3000 | Aplicación React |
| Backend API | http://localhost:8080/api/v1 | API REST |
| pgAdmin | http://localhost:5050 | Administrador de BD |

## Usuarios de Prueba

El sistema viene con datos de prueba pre-cargados:

| Usuario | Rol | Email |
|---------|-----|-------|
| Ada Lovelace | ADMIN | `ada.lovelace@bizflowerp.com` |
| Nikola Tesla | MANAGER | `nikola.tesla@bizflowerp.com` |
| Ken Thompson | USER | `ken.thompson@bizflowerp.com` |

::: tip Contraseña por defecto
Todos los usuarios de prueba usan la contraseña: `password123`
:::

## Siguiente Paso

Explora las guías según tu rol:

- [Guía de Administrador](/guide/roles/admin) - CRUD completo y gestión de usuarios
- [Guía de Manager](/guide/roles/manager) - Supervisión y reportes
- [Guía de Usuario](/guide/roles/user) - Gestión de gastos propios

O profundiza en la configuración:

- [Instalación detallada](/guide/installation)
- [Arquitectura del sistema](/guide/architecture)
