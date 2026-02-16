---
layout: home

hero:
  name: Bizflow ERP
  text: Sistema de Gestión Empresarial
  tagline: Gestiona gastos, empleados y nóminas con una plataforma moderna y segura
  image:
    src: /logo.svg
    alt: Bizflow ERP
  actions:
    - theme: brand
      text: Empezar
      link: /guide/getting-started
    - theme: alt
      text: Ver en GitHub
      link: https://github.com/jdomdev/bizflow-erp-springboot-react-docker

features:
  - icon: 🔐
    title: Seguridad Robusta
    details: Autenticación JWT con control de acceso basado en roles (ADMIN, MANAGER, USER)
  - icon: 📊
    title: Dashboard Intuitivo
    details: Visualiza estadísticas y métricas clave de tu empresa en tiempo real
  - icon: 💰
    title: Gestión de Gastos
    details: CRUD completo con paginación del servidor, filtros y exportación
  - icon: 👥
    title: Empleados y Nóminas
    details: Administra tu equipo con vinculación automática entre usuarios y empleados
  - icon: 🌙
    title: Modo Oscuro
    details: Interfaz adaptable con persistencia de preferencias de usuario
  - icon: 🔔
    title: Notificaciones Real-time
    details: WebSocket para alertas instantáneas de nuevos gastos y nóminas
---

## Stack Tecnológico

<div class="tech-stack">

| Backend | Frontend | Infraestructura |
|---------|----------|-----------------|
| Spring Boot 3.3.4 | React 18 | Docker Compose |
| Spring Security + JWT | Vite 5 | PostgreSQL 16 |
| JPA/Hibernate | Tailwind CSS | Nginx |
| Maven | Zustand | pgAdmin |

</div>

## Quick Start

```bash
# Clonar el repositorio
git clone https://github.com/jdomdev/bizflow-erp-springboot-react-docker.git
cd bizflow-erp-springboot-react-docker

# Iniciar en modo desarrollo
make dev

# Acceder a la aplicación
# Frontend: http://localhost:3000
# API: http://localhost:8080/api/v1
```
