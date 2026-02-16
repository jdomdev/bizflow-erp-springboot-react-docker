# Opciones de Despliegue Cloud para Bizflow ERP

## Documento de Investigación - Hosting Docker Containers

**Fecha**: 9 de Febrero de 2026  
**Autor**: Equipo de Desarrollo  
**Versión**: 1.0

---

## Índice

1. [Introducción](#1-introducción)
2. [Requisitos de la Aplicación](#2-requisitos-de-la-aplicación)
3. [Comparativa de Proveedores](#3-comparativa-de-proveedores)
4. [Análisis Detallado por Proveedor](#4-análisis-detallado-por-proveedor)
5. [Matriz de Decisión](#5-matriz-de-decisión)
6. [Recomendación Final](#6-recomendación-final)
7. [Guía de Migración](#7-guía-de-migración)

---

## 1. Introducción

Este documento analiza las opciones de hosting cloud de pago pero económicas para desplegar los tres contenedores Docker de Bizflow ERP en producción:

- **PostgreSQL** (Base de datos)
- **Backend** (Spring Boot / Java 21)
- **Frontend** (React / Nginx)

### Criterios de Evaluación

| Criterio | Peso | Descripción |
|----------|------|-------------|
| **Precio** | 35% | Costo mensual total para los 3 servicios |
| **Facilidad** | 20% | Curva de aprendizaje y configuración |
| **Rendimiento** | 20% | Recursos disponibles (RAM, CPU, storage) |
| **Escalabilidad** | 15% | Capacidad de crecer según demanda |
| **Fiabilidad** | 10% | Uptime y redundancia |

---

## 2. Requisitos de la Aplicación

### Requisitos Mínimos de Producción

| Servicio | RAM | CPU | Storage | Red |
|----------|-----|-----|---------|-----|
| PostgreSQL | 512 MB | 0.5 vCPU | 5 GB SSD | Privada |
| Backend (Java) | 512 MB | 0.5 vCPU | 500 MB | Pública |
| Frontend (Nginx) | 128 MB | 0.25 vCPU | 100 MB | Pública/CDN |

### Requisitos Recomendados

| Servicio | RAM | CPU | Storage | Red |
|----------|-----|-----|---------|-----|
| PostgreSQL | 1 GB | 1 vCPU | 10 GB SSD | Privada |
| Backend (Java) | 1 GB | 1 vCPU | 1 GB | Pública |
| Frontend (Nginx) | 256 MB | 0.5 vCPU | 200 MB | CDN |

### Requerimientos Adicionales

- ✅ Soporte para Docker / Contenedores
- ✅ PostgreSQL 15+ o servicio gestionado
- ✅ SSL/TLS gratuito (Let's Encrypt o similar)
- ✅ Variables de entorno seguras
- ✅ CI/CD integrable (GitHub Actions compatible)
- ⚠️ WebSocket support (importante para notificaciones)

---

## 3. Comparativa de Proveedores

### Tabla Resumen de Precios

| # | Proveedor | Front | Back | DB | **Total/mes** | Free Tier |
|---|-----------|-------|------|----|--------------:|-----------|
| 1 | **Railway** | $5 | $5 | $5 | **~$15-20** | $5/mes créditos |
| 2 | **Render** | $0* | $7 | $7 | **~$14-20** | Sí (limitado) |
| 3 | **Fly.io** | $2 | $5 | $5 | **~$12-15** | $5/mes créditos |
| 4 | **DigitalOcean App Platform** | $5 | $5 | $7 | **~$17** | No |
| 5 | **Northflank** | $5 | $5 | $5 | **~$15** | $30/mes trial |
| 6 | **Koyeb** | $0* | $5 | $5 | **~$10-15** | Sí (nano) |
| 7 | **Back4App** | $5 | $5 | $5 | **~$15** | $5/mes |
| 8 | **Coolify (self-hosted)** | - | - | - | **~$5-6** | Open source |
| 9 | **Hetzner Cloud** | $4 | $4 | $4 | **~$12** | No |
| 10 | **Oracle Cloud** | $0 | $0 | $0 | **$0*** | Always Free** |

*Tier estático gratuito  
**Con limitaciones significativas

---

## 4. Análisis Detallado por Proveedor

### 4.1 Railway 🚂

**Web**: https://railway.app

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              RAILWAY                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Modelo de Precios: Pay-as-you-go (pago por uso)                           │
│                                                                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │    Frontend     │  │    Backend      │  │   PostgreSQL    │            │
│  │   (Static/Node) │  │    (Docker)     │  │   (Managed)     │            │
│  │                 │  │                 │  │                 │            │
│  │  ~$2-5/mes      │  │  ~$5-10/mes     │  │  ~$5-7/mes      │            │
│  │  (bajo tráfico) │  │  (512MB RAM)    │  │  (1GB storage)  │            │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘            │
│                                                                             │
│  Estimación Total: $12-20/mes                                              │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Ventajas:**
- ✅ Deploy desde GitHub en 1 clic
- ✅ PostgreSQL gestionado incluido
- ✅ SSL automático
- ✅ Variables de entorno fáciles
- ✅ WebSocket funciona sin configuración extra
- ✅ Logs y métricas integrados
- ✅ Preview environments automáticos

**Desventajas:**
- ❌ Sin tier completamente gratuito (solo $5 créditos trial)
- ❌ Puede escalar rápido el costo con más uso
- ❌ RAM limitada en planes básicos

**Configuración Bizflow:**
```yaml
# railway.json
{
  "services": [
    {
      "name": "frontend",
      "source": { "image": "frontend:latest" },
      "networking": { "port": 80 }
    },
    {
      "name": "backend",
      "source": { "image": "backend:latest" },
      "networking": { "port": 8080 },
      "variables": {
        "SPRING_PROFILES_ACTIVE": "prod",
        "DATABASE_URL": "${{Postgres.DATABASE_URL}}"
      }
    },
    {
      "name": "Postgres",
      "plugin": "postgresql"
    }
  ]
}
```

---

### 4.2 Render 🎨

**Web**: https://render.com

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                               RENDER                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Planes de Servicios:                                                       │
│                                                                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │  Static Site    │  │   Web Service   │  │   PostgreSQL    │            │
│  │    (Frontend)   │  │    (Backend)    │  │    (Managed)    │            │
│  │                 │  │                 │  │                 │            │
│  │  FREE ✓         │  │  Starter: $7    │  │  Starter: $7    │            │
│  │  100GB bandwidth│  │  512MB RAM      │  │  1GB storage    │            │
│  │  SSL incluido   │  │  0.5 CPU        │  │  (expire 90d*)  │            │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘            │
│                                                                             │
│  *Free tier DB se borra tras 90 días de inactividad                        │
│                                                                             │
│  Estimación Total: $14/mes (con DB de pago)                                │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Ventajas:**
- ✅ Frontend estático GRATIS (ilimitado)
- ✅ Deploy automático desde GitHub
- ✅ SSL gratuito
- ✅ Blueprint YAML para IaC
- ✅ Soporta Docker
- ✅ Buena documentación

**Desventajas:**
- ❌ Free tier del backend se "duerme" (spin-down tras 15min)
- ❌ Cold start de ~30 segundos al despertar
- ❌ PostgreSQL gratis expira en 90 días
- ⚠️ WebSocket requiere plan de pago

**Problema Crítico para Bizflow:**
```
⚠️  El tier gratuito del backend NO soporta WebSocket persistentes.
    El servicio se duerme y las conexiones WS se cierran.
    Para notificaciones en tiempo real, NECESITAS el plan de pago ($7/mes).
```

**Configuración (render.yaml):**
```yaml
services:
  - type: web
    name: bizflow-backend
    env: docker
    plan: starter  # $7/mes - NO usar free por WebSocket
    dockerfilePath: ./backend/Dockerfile
    envVars:
      - key: SPRING_PROFILES_ACTIVE
        value: prod
      - key: DATABASE_URL
        fromDatabase:
          name: bizflow-db
          property: connectionString

  - type: web
    name: bizflow-frontend
    env: static
    buildCommand: npm run build
    staticPublishPath: ./dist
    pullRequestPreviewsEnabled: true

databases:
  - name: bizflow-db
    plan: starter  # $7/mes
    postgresMajorVersion: 15
```

---

### 4.3 Fly.io 🪰

**Web**: https://fly.io

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                               FLY.IO                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Modelo: Pay-as-you-go con $5/mes créditos                                 │
│                                                                             │
│  Precios por VM:                                                            │
│  ┌──────────────────────────────────────────────────────────────────┐      │
│  │  shared-cpu-1x (256MB):  ~$2/mes                                 │      │
│  │  shared-cpu-1x (512MB):  ~$4/mes                                 │      │
│  │  shared-cpu-1x (1GB):    ~$7/mes                                 │      │
│  │  dedicated-cpu-1x (2GB): ~$29/mes                                │      │
│  └──────────────────────────────────────────────────────────────────┘      │
│                                                                             │
│  PostgreSQL (Fly Postgres):                                                 │
│  ┌──────────────────────────────────────────────────────────────────┐      │
│  │  development-single (1GB RAM, 10GB):  ~$5/mes                    │      │
│  │  production-single (2GB RAM, 40GB):   ~$15/mes                   │      │
│  └──────────────────────────────────────────────────────────────────┘      │
│                                                                             │
│  Bizflow Estimación:                                                        │
│  • Frontend (256MB): $2/mes                                                 │
│  • Backend (512MB):  $4/mes                                                 │
│  • DB (dev-single):  $5/mes                                                 │
│  ─────────────────────────────                                              │
│  Total: ~$11-15/mes                                                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Ventajas:**
- ✅ Muy barato para apps pequeñas
- ✅ Despliegue global (edge computing)
- ✅ Excelente CLI (`flyctl`)
- ✅ WebSocket nativo (no se duerme)
- ✅ Volumes persistentes
- ✅ Private networking entre servicios
- ✅ Escala a 0 (pay only when used)

**Desventajas:**
- ❌ Curva de aprendizaje más alta
- ❌ PostgreSQL no es gestionado (lo corres en VM)
- ❌ Requiere configurar `fly.toml` manualmente
- ❌ Sin UI tan pulida como Railway/Render

**Configuración (fly.toml para backend):**
```toml
app = "bizflow-backend"
primary_region = "mad"  # Madrid

[build]
  dockerfile = "backend/Dockerfile"

[env]
  SPRING_PROFILES_ACTIVE = "prod"

[http_service]
  internal_port = 8080
  force_https = true
  auto_stop_machines = false  # Importante para WebSocket
  auto_start_machines = true
  min_machines_running = 1

[[vm]]
  cpu_kind = "shared"
  cpus = 1
  memory_mb = 512
```

---

### 4.4 DigitalOcean App Platform 🌊

**Web**: https://www.digitalocean.com/products/app-platform

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        DIGITALOCEAN APP PLATFORM                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Planes:                                                                    │
│  ┌──────────────────────────────────────────────────────────────────┐      │
│  │  Basic ($5/mes)                                                  │      │
│  │  └── 512MB RAM, 1 vCPU compartida                               │      │
│  │                                                                  │      │
│  │  Professional ($12/mes)                                          │      │
│  │  └── 1GB RAM, 1 vCPU dedicada                                   │      │
│  └──────────────────────────────────────────────────────────────────┘      │
│                                                                             │
│  Managed Databases:                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐      │
│  │  PostgreSQL Basic ($7/mes)                                       │      │
│  │  └── 1GB RAM, 10GB storage, 1 vCPU                              │      │
│  └──────────────────────────────────────────────────────────────────┘      │
│                                                                             │
│  Bizflow:                                                                   │
│  • Frontend Static: GRATIS (en App Platform)                               │
│  • Backend Basic:   $5/mes                                                  │
│  • DB Managed:      $7/mes                                                  │
│  ─────────────────────────────                                              │
│  Total: ~$12-17/mes                                                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Ventajas:**
- ✅ Empresa establecida y confiable
- ✅ PostgreSQL gestionado de calidad
- ✅ UI muy intuitiva
- ✅ Buen soporte
- ✅ Créditos para startups disponibles ($200)
- ✅ WebSocket funciona bien

**Desventajas:**
- ❌ Más caro que las alternativas nuevas
- ❌ Sin free tier real (solo trial)
- ❌ Menos features que Railway/Render

---

### 4.5 Northflank ⚙️

**Web**: https://northflank.com

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                             NORTHFLANK                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Enfocado en developers, similar a Heroku moderno                          │
│                                                                             │
│  Precios:                                                                   │
│  • Compute: desde $5/mes por servicio (0.5 vCPU, 512MB)                    │
│  • Databases: desde $5/mes (PostgreSQL gestionado)                         │
│  • Trial: $30 en créditos                                                   │
│                                                                             │
│  Bizflow: ~$15-20/mes                                                       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Ventajas:**
- ✅ UI muy profesional
- ✅ CI/CD integrado
- ✅ Múltiples regiones
- ✅ GitHub/GitLab integration

**Desventajas:**
- ❌ Menos conocido
- ❌ Documentación mejorable

---

### 4.6 Koyeb 🌍

**Web**: https://koyeb.com

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                               KOYEB                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Modelo: Edge-first platform                                                │
│                                                                             │
│  Planes:                                                                    │
│  • Free (Nano): 512MB RAM, limitado                                        │
│  • Starter ($5/mes): 1GB RAM, mejor rendimiento                            │
│                                                                             │
│  ⚠️ Nota: No tienen PostgreSQL gestionado                                   │
│     Necesitas usar otro proveedor (Neon, Supabase, etc.)                   │
│                                                                             │
│  Bizflow (con Neon PostgreSQL free):                                        │
│  • Frontend: Gratis (tier nano)                                             │
│  • Backend: $5/mes                                                          │
│  • DB (Neon): Gratis hasta 3GB                                              │
│  ─────────────────────────────                                              │
│  Total: ~$5-10/mes                                                          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Ventajas:**
- ✅ Muy económico
- ✅ Deploy global automático
- ✅ Free tier usable
- ✅ Soporta Docker nativo

**Desventajas:**
- ❌ Sin PostgreSQL propio (necesitas Neon/Supabase)
- ❌ Menos maduro que competidores
- ❌ Documentación limitada

---

### 4.7 Back4App Containers 📦

**Web**: https://www.back4app.com/containers

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           BACK4APP CONTAINERS                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Precios competitivos para contenedores:                                    │
│  • Starter: $5/mes (256MB RAM)                                              │
│  • Basic: $10/mes (512MB RAM)                                               │
│  • Standard: $25/mes (1GB RAM)                                              │
│                                                                             │
│  Incluye PostgreSQL gestionado desde $5/mes                                 │
│                                                                             │
│  Bizflow: ~$15-20/mes                                                       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

### 4.8 Coolify (Self-Hosted) 🆓

**Web**: https://coolify.io

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                      COOLIFY (Self-Hosted PaaS)                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ⚡ Open source, auto-hosteable en cualquier VPS                            │
│                                                                             │
│  Cómo funciona:                                                             │
│  1. Alquilas un VPS barato (Hetzner, OVH, etc.)                            │
│  2. Instalas Coolify (1 comando)                                            │
│  3. Tienes tu propio "Heroku/Railway" privado                              │
│                                                                             │
│  Ejemplo con Hetzner Cloud:                                                 │
│  ┌──────────────────────────────────────────────────────────────────┐      │
│  │  CX22 (2 vCPU, 4GB RAM, 40GB SSD): €4.51/mes (~$5)               │      │
│  │  └── Suficiente para Front + Back + DB + Coolify                │      │
│  └──────────────────────────────────────────────────────────────────┘      │
│                                                                             │
│  Bizflow: ~$5-6/mes TODO INCLUIDO                                          │
│                                                                             │
│  ⚠️ Requiere: Conocimientos de sysadmin, backups manuales, SSL config      │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Ventajas:**
- ✅ MUY BARATO (~$5/mes total)
- ✅ Sin vendor lock-in
- ✅ Recursos dedicados, no compartidos
- ✅ UI similar a Railway/Render
- ✅ Deploy desde Git automático
- ✅ SSL con Let's Encrypt

**Desventajas:**
- ❌ Tú eres responsable de mantenimiento
- ❌ Backups manuales
- ❌ Sin soporte (solo comunidad)
- ❌ Requiere conocimientos de Linux

**Setup:**
```bash
# En tu VPS (Hetzner, OVH, etc.)
curl -fsSL https://get.coolify.io | bash
```

---

### 4.9 Hetzner Cloud 🇩🇪

**Web**: https://www.hetzner.com/cloud

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            HETZNER CLOUD                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  VPS más baratos de Europa (datacenter en Alemania/Finlandia)              │
│                                                                             │
│  Precios (CX series - shared CPU):                                          │
│  ┌──────────────────────────────────────────────────────────────────┐      │
│  │  CX11 (1 vCPU, 2GB RAM, 20GB):   €3.29/mes                       │      │
│  │  CX22 (2 vCPU, 4GB RAM, 40GB):   €4.51/mes   ← RECOMENDADO       │      │
│  │  CX32 (2 vCPU, 8GB RAM, 80GB):   €8.21/mes                       │      │
│  └──────────────────────────────────────────────────────────────────┘      │
│                                                                             │
│  Opciones de despliegue:                                                    │
│                                                                             │
│  A) Un solo VPS con Docker Compose:                                         │
│     CX22 → $5/mes (front + back + db todo junto)                           │
│                                                                             │
│  B) VPS separados:                                                          │
│     • Frontend (CX11): $3.5/mes                                             │
│     • Backend (CX11):  $3.5/mes                                             │
│     • PostgreSQL (CX11): $3.5/mes                                           │
│     ─────────────────────                                                   │
│     Total: ~$10.5/mes                                                       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Ventajas:**
- ✅ Precios imbatibles en Europa
- ✅ Excelente rendimiento
- ✅ Sin sorpresas de facturación
- ✅ Datacenters en Europa (GDPR)
- ✅ API y CLI disponibles
- ✅ Snapshots y backups baratos

**Desventajas:**
- ❌ IaaS puro, no PaaS (más trabajo de setup)
- ❌ Necesitas gestionar todo (Docker, SSL, backups)
- ❌ Sin deploy automático desde GitHub

---

### 4.10 Oracle Cloud Free Tier ☁️

**Web**: https://www.oracle.com/cloud/free/

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        ORACLE CLOUD FREE TIER                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  🎁 "Always Free" - No expira, genuinamente gratis                         │
│                                                                             │
│  Incluye:                                                                   │
│  ┌──────────────────────────────────────────────────────────────────┐      │
│  │  2x AMD VMs (1/8 OCPU, 1GB RAM cada una)                         │      │
│  │  4x Arm VMs (Ampere A1: hasta 4 OCPUs, 24GB RAM TOTAL)           │      │
│  │  200GB block storage                                             │      │
│  │  10GB object storage                                             │      │
│  │  Autonomous Database (20GB)                                       │      │
│  └──────────────────────────────────────────────────────────────────┘      │
│                                                                             │
│  Bizflow (usando Arm instances):                                            │
│  • Frontend: 1 OCPU, 6GB RAM - GRATIS                                       │
│  • Backend: 2 OCPU, 12GB RAM - GRATIS                                       │
│  • PostgreSQL: 1 OCPU, 6GB RAM - GRATIS                                     │
│  ─────────────────────────────────────────────                              │
│  Total: $0/mes 🎉                                                           │
│                                                                             │
│  ⚠️ ADVERTENCIAS:                                                           │
│  • Setup complejo (OCI no es fácil)                                         │
│  • Disponibilidad de VMs free limitada por región                          │
│  • Pueden reclamar recursos si no hay uso (idle reclamation)               │
│  • Soporte solo de comunidad                                                │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Ventajas:**
- ✅ GRATIS de verdad (no trial)
- ✅ Recursos generosos (especialmente Arm)
- ✅ No expira

**Desventajas:**
- ❌ Setup MUY complejo
- ❌ UI/UX horrible
- ❌ Documentación confusa
- ❌ Disponibilidad limitada (regiones populares llenas)
- ❌ Pueden reclamar instancias sin uso

---

## 5. Matriz de Decisión

### Puntuación (1-10)

| Proveedor | Precio | Facilidad | Rendimiento | Escalabilidad | WebSocket | **Total** |
|-----------|--------|-----------|-------------|---------------|-----------|-----------|
| Railway | 7 | 10 | 8 | 9 | ✅ 10 | **8.6** |
| Render | 8 | 9 | 7 | 8 | ⚠️ 7* | **7.8** |
| Fly.io | 9 | 6 | 9 | 10 | ✅ 10 | **8.4** |
| DigitalOcean | 6 | 8 | 8 | 8 | ✅ 9 | **7.6** |
| Northflank | 7 | 7 | 8 | 8 | ✅ 9 | **7.6** |
| Koyeb | 9 | 7 | 7 | 7 | ✅ 8 | **7.4** |
| Back4App | 7 | 7 | 7 | 7 | ⚠️ 7 | **7.0** |
| Coolify+Hetzner | 10 | 4 | 9 | 6 | ✅ 10 | **7.4** |
| Hetzner (manual) | 10 | 3 | 9 | 7 | ✅ 10 | **7.2** |
| Oracle Free | 10 | 2 | 8 | 5 | ✅ 10 | **6.2** |

*Render requiere plan de pago para WebSocket persistentes

### Visualización

```
FACILIDAD DE USO  ──────────────────────────────────────────►  PRECIO
     Alto                                                      Bajo
       │
       │  ┌─────────────┐
       │  │  RAILWAY    │  ← Mejor balance para demos/MVPs
       │  │  $15-20/mes │
       │  └─────────────┘
       │
       │  ┌─────────────┐
       │  │   RENDER    │  ← Bueno si no necesitas WS en free tier
       │  │  $14-20/mes │
       │  └─────────────┘
       │
       │                    ┌─────────────┐
       │                    │   FLY.IO    │  ← Mejor precio/rendimiento
       │                    │  $12-15/mes │
       │                    └─────────────┘
       │
       │                                      ┌──────────────────┐
       │                                      │ COOLIFY+HETZNER  │
       │                                      │    $5-6/mes      │
       │                                      └──────────────────┘
       │
       │                                                ┌──────────────┐
       │                                                │ ORACLE FREE  │
       │                                                │   $0/mes     │
       │                                                │  (complejo)  │
       │                                                └──────────────┘
       ▼
   COMPLEJIDAD
```

---

## 6. Recomendación Final

### Para Bizflow ERP, recomendamos por orden de preferencia:

#### 🥇 **1. Railway** - Mejor para demos y desarrollo rápido
```
Costo: ~$15-20/mes
Ideal para: Demos a clientes, desarrollo, staging
Pros: Setup en minutos, WebSocket funciona perfectamente
```

#### 🥈 **2. Fly.io** - Mejor relación precio/rendimiento
```
Costo: ~$12-15/mes
Ideal para: Producción ligera, startups conscientes del costo
Pros: Muy económico, buen rendimiento, WebSocket nativo
Cons: Requiere más configuración
```

#### 🥉 **3. Coolify + Hetzner** - Mejor precio absoluto
```
Costo: ~$5-6/mes
Ideal para: Proyectos personales, máximo ahorro
Pros: Precio imbatible, recursos dedicados
Cons: Requiere conocimientos de sysadmin
```

### Decisión según caso de uso:

| Situación | Recomendación | Costo |
|-----------|---------------|-------|
| Demo rápida a cliente | Railway | $15/mes |
| MVP en producción | Fly.io | $12/mes |
| Proyecto personal | Coolify + Hetzner | $5/mes |
| Enterprise / SLA | DigitalOcean | $17/mes |
| Presupuesto cero | Oracle Free | $0 |

---

## 7. Guía de Migración

### Para Railway (Recomendado)

1. **Crear cuenta**: https://railway.app
2. **Conectar GitHub**
3. **Crear nuevo proyecto**
4. **Agregar servicios**:
   - PostgreSQL (plugin)
   - Backend (desde Dockerfile)
   - Frontend (desde Dockerfile)
5. **Configurar variables de entorno**
6. **Deploy**

### Archivos necesarios en el repo:

```
bizflow-erp/
├── railway.json          # Configuración Railway
├── backend/
│   └── Dockerfile        # Ya existe
├── frontend/
│   └── Dockerfile        # Ya existe
└── docker-compose.yml    # Referencia
```

### Variables de entorno a configurar:

```bash
# Backend
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=${{Postgres.DATABASE_URL}}
JWT_SECRET=<generar-secreto-seguro>

# Frontend
VITE_API_URL=https://tu-backend.railway.app/api/v1
VITE_WS_URL=https://tu-backend.railway.app
```

---

## Anexo: Comparativa de Costos Anual

| Proveedor | Mensual | **Anual** |
|-----------|---------|-----------|
| Railway | $17 | **$204** |
| Render | $14 | **$168** |
| Fly.io | $13 | **$156** |
| DigitalOcean | $17 | **$204** |
| Coolify+Hetzner | $5 | **$60** |
| Oracle Free | $0 | **$0** |

---

**Documento actualizado**: Febrero 2026

**Referencias**:
- https://railway.app/pricing
- https://render.com/pricing
- https://fly.io/docs/about/pricing/
- https://www.digitalocean.com/pricing
- https://www.hetzner.com/cloud
- https://coolify.io
- https://www.oracle.com/cloud/free/
