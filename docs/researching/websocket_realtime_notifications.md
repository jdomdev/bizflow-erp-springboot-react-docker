# WebSocket y Notificaciones en Tiempo Real

## Documento de Investigación - Bizflow ERP

**Fecha**: 9 de Febrero de 2026  
**Autor**: Equipo de Desarrollo  
**Versión**: 1.0

---

## Índice

1. [Introducción](#1-introducción)
2. [¿Qué es WebSocket?](#2-qué-es-websocket)
3. [Comparativa: Polling vs WebSocket](#3-comparativa-polling-vs-websocket)
4. [Arquitectura de la Implementación](#4-arquitectura-de-la-implementación)
5. [Stack Tecnológico](#5-stack-tecnológico)
6. [Diagramas de Flujo](#6-diagramas-de-flujo)
7. [Implementación en Bizflow ERP](#7-implementación-en-bizflow-erp)
8. [Ventajas y Beneficios](#8-ventajas-y-beneficios)
9. [Consideraciones de Producción](#9-consideraciones-de-producción)
10. [Conclusiones](#10-conclusiones)

---

## 1. Introducción

Este documento explica en profundidad qué son los WebSockets, por qué los implementamos en Bizflow ERP para el sistema de notificaciones, y cómo mejoran significativamente la experiencia del usuario respecto a las soluciones tradicionales basadas en polling.

### Problema Original

El sistema de notificaciones original tenía una limitación importante: **polling manual**. Los usuarios debían recargar la página o hacer clic en la campanita para ver si tenían nuevas notificaciones. Esto generaba:

- Mala experiencia de usuario (no hay inmediatez)
- Posible pérdida de notificaciones importantes
- No hay forma de saber si hay actualizaciones sin interactuar

### Solución Implementada

Implementamos **WebSocket con STOMP sobre SockJS** para lograr comunicación bidireccional en tiempo real. Ahora:

- Las notificaciones aparecen instantáneamente
- El contador se actualiza automáticamente
- No se requiere ninguna acción del usuario

---

## 2. ¿Qué es WebSocket?

### Definición

WebSocket es un **protocolo de comunicación bidireccional full-duplex** que opera sobre un único socket TCP. A diferencia de HTTP, que sigue el modelo request-response, WebSocket permite que tanto el cliente como el servidor envíen mensajes en cualquier momento.

### Características Principales

| Característica | HTTP Tradicional | WebSocket |
|---------------|------------------|-----------|
| Comunicación | Unidireccional (cliente→servidor) | Bidireccional |
| Conexión | Nueva conexión por cada request | Conexión persistente |
| Overhead | Headers HTTP completos cada vez | Mínimo tras handshake |
| Latencia | Alta (nuevo TCP handshake) | Muy baja (conexión abierta) |
| Tiempo real | No nativo | Nativo |

### Cómo Funciona

```
┌──────────────────────────────────────────────────────────────────┐
│                    ESTABLECIMIENTO DE CONEXIÓN                    │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Cliente                                    Servidor            │
│      │                                          │                │
│      │  ─────── HTTP Upgrade Request ───────►  │                │
│      │       GET /ws HTTP/1.1                  │                │
│      │       Upgrade: websocket                │                │
│      │       Connection: Upgrade               │                │
│      │                                          │                │
│      │  ◄────── HTTP 101 Switching ──────────  │                │
│      │       Protocols                         │                │
│      │                                          │                │
│      ╔══════════════════════════════════════════╗                │
│      ║   CONEXIÓN WEBSOCKET ESTABLECIDA        ║                │
│      ║   (Full-Duplex Bidireccional)           ║                │
│      ╚══════════════════════════════════════════╝                │
│      │                                          │                │
│      │  ◄═══════ Mensaje del servidor ═══════  │                │
│      │  ═══════► Mensaje del cliente ═══════►  │                │
│      │  ◄═══════ Mensaje del servidor ═══════  │                │
│      │  ◄═══════ Mensaje del servidor ═══════  │                │
│      │  ═══════► Mensaje del cliente ═══════►  │                │
│      │                                          │                │
└──────────────────────────────────────────────────────────────────┘
```

---

## 3. Comparativa: Polling vs WebSocket

### 3.1 Short Polling (Método Tradicional)

```
┌──────────────────────────────────────────────────────────────────┐
│                         SHORT POLLING                             │
│               (el cliente pregunta constantemente)                │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Cliente                                    Servidor            │
│      │                                          │                │
│  t=0 │  ───── "¿Hay notificaciones?" ───────►  │                │
│      │  ◄──────────── "No" ──────────────────  │                │
│      │                                          │                │
│  t=5s│  ───── "¿Hay notificaciones?" ───────►  │                │
│      │  ◄──────────── "No" ──────────────────  │                │
│      │                                          │                │
│ t=10s│  ───── "¿Hay notificaciones?" ───────►  │                │
│      │  ◄──────────── "No" ──────────────────  │                │
│      │                                          │                │
│ t=15s│  ───── "¿Hay notificaciones?" ───────►  │                │
│      │  ◄───────── "Sí, hay 1" ──────────────  │  ← Notificación
│      │                                          │    creada en t=12s
│      │                                          │    (3 seg de retraso)
└──────────────────────────────────────────────────────────────────┘

❌ Problemas:
   • Desperdicio de requests (la mayoría devuelven vacío)
   • Retraso de hasta N segundos (intervalo de polling)
   • Alto consumo de recursos del servidor
   • Mayor uso de ancho de banda
```

### 3.2 Long Polling

```
┌──────────────────────────────────────────────────────────────────┐
│                         LONG POLLING                              │
│               (el servidor retiene la conexión)                   │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Cliente                                    Servidor            │
│      │                                          │                │
│  t=0 │  ───── "¿Hay notificaciones?" ───────►  │                │
│      │           (servidor espera...)          │                │
│      │                    .                     │                │
│      │                    .                     │                │
│      │                    .                     │                │
│ t=12s│  ◄───────── "Sí, hay 1" ──────────────  │  ← Notificación
│      │                                          │    creada
│      │                                          │                │
│ t=12s│  ───── "¿Hay notificaciones?" ───────►  │  ← Nueva conexión
│      │           (servidor espera...)          │                │
│      │                    .                     │                │
│      │                    .                     │                │
└──────────────────────────────────────────────────────────────────┘

⚠️ Mejor que Short Polling pero:
   • Requiere reconexión después de cada mensaje
   • Timeout de conexión HTTP (30-60 seg)
   • Aún hay overhead de HTTP headers
```

### 3.3 WebSocket (Nuestra Implementación)

```
┌──────────────────────────────────────────────────────────────────┐
│                          WEBSOCKET                                │
│              (conexión persistente bidireccional)                 │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Cliente                                    Servidor            │
│      │                                          │                │
│  t=0 │  ═══════ Conexión WebSocket ═══════════  │                │
│      │         (handshake único)               │                │
│      ╔══════════════════════════════════════════╗                │
│      ║      CONEXIÓN ABIERTA PERMANENTE        ║                │
│      ╚══════════════════════════════════════════╝                │
│      │                                          │                │
│      │  ◄───────────────────────────────────── │  ← heartbeat   │
│      │  ────────────────────────────────────►  │  ← heartbeat   │
│      │                                          │                │
│ t=12s│  ◄══════ NUEVA NOTIFICACIÓN ══════════  │  ← INSTANTÁNEO │
│      │         {"type": "EXPENSE_CREATED",     │                │
│      │          "title": "Nuevo gasto"}        │                │
│      │                                          │                │
│ t=45s│  ◄══════ OTRA NOTIFICACIÓN ═══════════  │                │
│      │                                          │                │
│      │  ════► Marcar como leída ═════════════► │  ← Cliente     │
│      │                                          │    también puede
│      │                                          │    enviar       │
└──────────────────────────────────────────────────────────────────┘

✅ Ventajas:
   • Latencia casi cero (milisegundos)
   • Sin overhead de HTTP repetido
   • Bidireccional real
   • Heartbeats mantienen la conexión viva
```

### Comparativa de Recursos

```
┌────────────────────────────────────────────────────────────────────────────┐
│                    CONSUMO DE RECURSOS (100 usuarios, 1 hora)              │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│   Short Polling (cada 5 seg):                                              │
│   ═══════════════════════════════════════════════════════════════ 72,000   │
│   requests                                                                 │
│                                                                            │
│   Long Polling:                                                            │
│   ════════════════════════════════════════════ ~2,000-5,000 requests       │
│                                                                            │
│   WebSocket:                                                               │
│   ═══ 100 conexiones + heartbeats (~3,600 mensajes ligeros)               │
│                                                                            │
└────────────────────────────────────────────────────────────────────────────┘

Overhead por mensaje:
┌─────────────────┬────────────────┬──────────────────┐
│ Método          │ Headers        │ Payload útil     │
├─────────────────┼────────────────┼──────────────────┤
│ HTTP Request    │ ~800-2000 bytes│ Variable         │
│ WebSocket Frame │ 2-14 bytes     │ Variable         │
└─────────────────┴────────────────┴──────────────────┘
```

---

## 4. Arquitectura de la Implementación

### Diagrama de Arquitectura Completa

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                      ARQUITECTURA WEBSOCKET - BIZFLOW ERP                    │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                                  FRONTEND                                    │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         React Application                            │   │
│  │  ┌─────────────┐  ┌─────────────────┐  ┌───────────────────────┐   │   │
│  │  │NotificationBell│ │notificationStore │ │    websocket.js       │   │   │
│  │  │  Component   │──│   (Zustand)     │──│  - connectWebSocket() │   │   │
│  │  │ 🔔 (badge)   │  │ - notifications │  │  - disconnectWebSocket│   │   │
│  │  │              │  │ - unreadCount   │  │  - SockJS client      │   │   │
│  │  │              │  │ - isConnected   │  │  - STOMP client       │   │   │
│  │  └─────────────┘  └─────────────────┘  └───────────┬───────────┘   │   │
│  └────────────────────────────────────────────────────┼────────────────┘   │
└───────────────────────────────────────────────────────┼─────────────────────┘
                                                        │
                                                        │ WebSocket/SockJS
                                                        │ wss://host/ws
                                                        ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                                  BACKEND                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      Spring Boot Application                         │   │
│  │                                                                      │   │
│  │  ┌───────────────────┐    ┌──────────────────────────────────────┐ │   │
│  │  │  WebSocketConfig  │    │       Message Broker (In-Memory)      │ │   │
│  │  │  ---------------  │    │  ┌──────────────┐ ┌──────────────┐   │ │   │
│  │  │  /ws endpoint     │───▶│  │   /topic/*   │ │  /queue/*    │   │ │   │
│  │  │  SockJS enabled   │    │  │  (broadcast) │ │ (user-specific│   │ │   │
│  │  │  STOMP protocol   │    │  └──────────────┘ └──────────────┘   │ │   │
│  │  └───────────────────┘    └──────────────────────────────────────┘ │   │
│  │                                         │                           │   │
│  │  ┌───────────────────────────────────────────────────────────────┐ │   │
│  │  │                  NotificationServiceImpl                       │ │   │
│  │  │  ─────────────────────────────────────────────────────────────│ │   │
│  │  │  • create() → Guarda en DB + sendWebSocketNotification()      │ │   │
│  │  │  • createForRole() → Notifica a todos los de un rol           │ │   │
│  │  │  • createForRoleExcludingUser() → Excluye al creador          │ │   │
│  │  │                                                                │ │   │
│  │  │  SimpMessagingTemplate.convertAndSendToUser(                  │ │   │
│  │  │      userId,                                                   │ │   │
│  │  │      "/queue/notifications",                                   │ │   │
│  │  │      notificationDto                                           │ │   │
│  │  │  )                                                             │ │   │
│  │  └───────────────────────────────────────────────────────────────┘ │   │
│  │                                         │                           │   │
│  │  ┌───────────────────────────────────────────────────────────────┐ │   │
│  │  │               Controllers (ExpenseController, etc.)            │ │   │
│  │  │  ─────────────────────────────────────────────────────────────│ │   │
│  │  │  @PostMapping("/") saveExpense()                              │ │   │
│  │  │     └──► notificationService.createForRoleExcludingUser(...)  │ │   │
│  │  └───────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
                                        │
                                        ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                               PostgreSQL                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  TABLE notification                                                  │   │
│  │  ├── id (PK)                                                        │   │
│  │  ├── user_id (FK → expense_user)                                    │   │
│  │  ├── type (EXPENSE_CREATED, PAYROLL_CREATED, etc.)                  │   │
│  │  ├── title                                                          │   │
│  │  ├── message                                                        │   │
│  │  ├── is_read (boolean)                                              │   │
│  │  ├── created_at                                                     │   │
│  │  └── read_at                                                        │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 5. Stack Tecnológico

### Backend (Spring Boot)

| Componente | Tecnología | Propósito |
|------------|------------|-----------|
| WebSocket Server | Spring WebSocket | Gestión de conexiones WS |
| Protocolo Mensajes | STOMP | Protocolo de mensajería sobre WS |
| Message Broker | Simple Broker (in-memory) | Enrutamiento de mensajes |
| Fallback | SockJS | Compatibilidad con navegadores antiguos |

#### Configuración (WebSocketConfig.java)

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Broker en memoria para /topic (broadcast) y /queue (punto a punto)
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();  // Fallback para navegadores sin WebSocket nativo
    }
}
```

### Frontend (React)

| Componente | Tecnología | Propósito |
|------------|------------|-----------|
| WebSocket Client | @stomp/stompjs | Cliente STOMP para JavaScript |
| Fallback Client | sockjs-client | Cliente SockJS para fallback |
| Estado Global | Zustand | Gestión del estado de notificaciones |

#### Cliente WebSocket (websocket.js)

```javascript
import SockJS from 'sockjs-client/dist/sockjs';
import { Client } from '@stomp/stompjs';

stompClient = new Client({
    webSocketFactory: () => new SockJS(wsUrl),
    reconnectDelay: 5000,        // Reconexión automática
    heartbeatIncoming: 4000,     // Heartbeat del servidor
    heartbeatOutgoing: 4000,     // Heartbeat del cliente
});

stompClient.subscribe(`/user/${userId}/queue/notifications`, (message) => {
    const notification = JSON.parse(message.body);
    useNotificationStore.getState().addNotification(notification);
});
```

---

## 6. Diagramas de Flujo

### 6.1 Flujo: Usuario Crea un Gasto

```
┌─────────────────────────────────────────────────────────────────────────────┐
│           FLUJO: KEN THOMPSON CREA UN GASTO → ADA LOVELACE RECIBE           │
└─────────────────────────────────────────────────────────────────────────────┘

  Ken Thompson                    Backend                     Ada Lovelace
  (ROLE_USER)                   (Spring Boot)                 (ROLE_ADMIN)
       │                             │                              │
       │  [1] POST /api/v1/expense   │                              │
       │  ─────────────────────────► │                              │
       │     { concept: "Almuerzo",  │                              │
       │       amount: 25.50 }       │                              │
       │                             │                              │
       │                             │ [2] Guardar en PostgreSQL    │
       │                             │     INSERT INTO expense...   │
       │                             │                              │
       │                             │ [3] notificationService      │
       │                             │     .createForRole("ADMIN",  │
       │                             │       excludeUser: Ken.id)   │
       │                             │                              │
       │                             │ [4] Para cada admin ≠ Ken:   │
       │                             │     - INSERT notification    │
       │                             │     - sendWebSocketNotification
       │                             │                              │
       │  ◄────────────────────────  │                              │
       │  [5] 201 Created            │                              │
       │                             │                              │
       │                             │  [6] WebSocket PUSH ═══════► │
       │                             │      /user/2/queue/notifs   │
       │                             │      {"type":"EXPENSE_CREATED",
       │                             │       "title":"Nuevo gasto", │
       │                             │       "message":"Ken ha..."}│
       │                             │                              │
       │                             │                         ┌────┴────┐
       │                             │                         │ 🔔 +1   │
       │                             │                         │ Badge   │
       │                             │                         │ Update  │
       │                             │                         └─────────┘
       │                             │                              │
       ▼                             ▼                              ▼
```

### 6.2 Flujo: Conexión WebSocket al Login

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    FLUJO: ESTABLECIMIENTO DE CONEXIÓN                        │
└─────────────────────────────────────────────────────────────────────────────┘

     Usuario                     React App                      Backend
        │                            │                             │
        │  [1] Envía credenciales    │                             │
        │  ─────────────────────────►│                             │
        │                            │  [2] POST /api/v1/login     │
        │                            │  ────────────────────────►  │
        │                            │                             │
        │                            │  ◄────────────────────────  │
        │                            │  [3] { token, user }        │
        │                            │                             │
        │                            │  [4] authStore.setAuth()    │
        │                            │      Guarda token + user    │
        │                            │                             │
        │                            │  [5] connectWebSocket()     │
        │                            │  ════════════════════════►  │
        │                            │      SockJS → /ws           │
        │                            │                             │
        │                            │  ◄════════════════════════  │
        │                            │  [6] CONNECTED              │
        │                            │                             │
        │                            │  [7] SUBSCRIBE              │
        │                            │  /user/{userId}/queue/notifs│
        │                            │  ════════════════════════►  │
        │                            │                             │
        │                            │  [8] fetchUnreadCount()     │
        │                            │  ────────────────────────►  │
        │                            │                             │
        │                            │  ◄────────────────────────  │
        │                            │  [9] { count: 3 }           │
        │                            │                             │
        │  ◄─────────────────────── │                             │
        │  [10] Muestra Dashboard    │                             │
        │       con 🔔 3             │                             │
        │                            │                             │
        ▼                            ▼                             ▼
```

### 6.3 Flujo: Recepción de Notificación en Tiempo Real

```
┌─────────────────────────────────────────────────────────────────────────────┐
│              FLUJO: PROCESAMIENTO DE NOTIFICACIÓN WEBSOCKET                  │
└─────────────────────────────────────────────────────────────────────────────┘

   Backend                    WebSocket                    React App
      │                      Connection                        │
      │                          │                             │
      │  [1] Evento: Nuevo gasto creado                        │
      │                          │                             │
      │  [2] messagingTemplate   │                             │
      │      .convertAndSendToUser(                            │
      │        userId: "2",      │                             │
      │        "/queue/notifs",  │                             │
      │        notificationDto   │                             │
      │      )                   │                             │
      │  ════════════════════►  │                             │
      │                          │                             │
      │                          │  [3] STOMP MESSAGE          │
      │                          │  destination: /user/2/...   │
      │                          │  body: JSON notification    │
      │                          │  ════════════════════════►  │
      │                          │                             │
      │                          │                    ┌────────┴────────┐
      │                          │                    │ stompClient     │
      │                          │                    │ .onMessage()    │
      │                          │                    │                 │
      │                          │                    │ JSON.parse()    │
      │                          │                    │                 │
      │                          │                    │ notificationStore
      │                          │                    │ .addNotification()
      │                          │                    │                 │
      │                          │                    │ • Agrega a lista│
      │                          │                    │ • unreadCount++ │
      │                          │                    │ • Re-render 🔔  │
      │                          │                    └────────┬────────┘
      │                          │                             │
      │                          │        Usuario ve           │
      │                          │        instantáneamente     │
      │                          │        la notificación      │
      │                          │                             │
      ▼                          ▼                             ▼
```

---

## 7. Implementación en Bizflow ERP

### 7.1 Tipos de Notificaciones

```java
public enum NotificationType {
    EXPENSE_CREATED,      // Gasto creado
    EXPENSE_UPDATED,      // Gasto modificado
    EXPENSE_DELETED,      // Gasto eliminado
    PAYROLL_CREATED,      // Nómina creada
    PAYROLL_UPDATED,      // Nómina modificada
    USER_CREATED,         // Usuario creado
    SYSTEM_ALERT,         // Alerta del sistema
    INFO                  // Información general
}
```

### 7.2 Flujo de Código

**Backend - Crear Notificación:**
```java
// En ExpenseControllerImpl.java
@PostMapping("/")
public ResponseEntity<?> saveExpense(@RequestBody Expense expense, ...) {
    Object savedExpense = expenseService.save(expense, headerAuth);
    
    // Notificar a admins (excepto el creador)
    notificationService.createForRoleExcludingUser(
        "ADMIN",
        creatorId,
        NotificationType.EXPENSE_CREATED,
        "Nuevo gasto registrado",
        String.format("%s ha registrado un gasto de %.2f€", userName, expense.getAmount())
    );
    
    return ResponseEntity.ok(savedExpense);
}
```

**Backend - Enviar WebSocket:**
```java
// En NotificationServiceImpl.java
private void sendWebSocketNotification(Long userId, NotificationDto notification) {
    messagingTemplate.convertAndSendToUser(
        userId.toString(),               // Destino: usuario específico
        "/queue/notifications",          // Canal de la cola
        notification                     // Payload JSON
    );
}
```

**Frontend - Recibir y Mostrar:**
```javascript
// En websocket.js
stompClient.subscribe(`/user/${user.id}/queue/notifications`, (message) => {
    const notification = JSON.parse(message.body);
    useNotificationStore.getState().addNotification(notification);
});

// En notificationStore.js
addNotification: (notification) => {
    set((state) => ({
        notifications: [notification, ...state.notifications],
        unreadCount: state.unreadCount + 1,
    }));
},
```

### 7.3 Gestión del Ciclo de Vida

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    CICLO DE VIDA DE LA CONEXIÓN                              │
└─────────────────────────────────────────────────────────────────────────────┘

              ┌─────────────────────────────────────────────────────────────┐
              │                                                             │
              ▼                                                             │
   ┌──────────────────┐                                                     │
   │      LOGIN       │                                                     │
   │  authStore.login │                                                     │
   └────────┬─────────┘                                                     │
            │                                                               │
            ▼                                                               │
   ┌──────────────────┐                                                     │
   │  App.jsx useEffect│     ┌──────────────────────────────────────────┐  │
   │  "auth changed"  │────►│  if (isAuthenticated && user) {          │  │
   └──────────────────┘     │      connectWebSocket();                 │  │
                            │      fetchUnreadCount();                 │  │
                            │  } else {                                │  │
                            │      disconnectWebSocket();              │  │
                            │  }                                       │  │
                            └──────────────────────────────────────────┘  │
                                        │                                  │
                                        ▼                                  │
                            ┌──────────────────────┐                       │
                            │  CONEXIÓN ACTIVA     │                       │
                            │  isConnected: true   │◄──────────────────┐   │
                            │  Heartbeats activos  │                   │   │
                            └──────────┬───────────┘                   │   │
                                       │                               │   │
               ┌───────────────────────┼───────────────────────┐      │   │
               │                       │                       │      │   │
               ▼                       ▼                       ▼      │   │
     ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐│   │
     │ LOGOUT          │    │ Pérdida de red  │    │ Tab cerrada     ││   │
     │ authStore.logout│    │ (desconexión)   │    │ (beforeunload)  ││   │
     └────────┬────────┘    └────────┬────────┘    └────────┬────────┘│   │
              │                      │                      │         │   │
              ▼                      │                      ▼         │   │
     ┌─────────────────┐            │             ┌─────────────────┐ │   │
     │disconnectWebSocket│           │             │disconnectWebSocket│   │
     │ (manual)        │            │             │ (cleanup)       │ │   │
     └────────┬────────┘            │             └─────────────────┘ │   │
              │                      │                                │   │
              ▼                      ▼                                │   │
     ┌─────────────────┐   ┌─────────────────┐                       │   │
     │  DESCONECTADO   │   │ RECONEXIÓN AUTO │                       │   │
     │  Fin de sesión  │   │ reconnectDelay: │───────────────────────┘   │
     └─────────────────┘   │ 5000ms          │                           │
                           └─────────────────┘                           │
                                    │                                    │
                                    └────────────────────────────────────┘
```

---

## 8. Ventajas y Beneficios

### 8.1 Para el Usuario

| Beneficio | Descripción |
|-----------|-------------|
| **Inmediatez** | Notificaciones aparecen instantáneamente |
| **Sin interacción** | No necesita recargar ni hacer clic |
| **Visual feedback** | Badge contador siempre actualizado |
| **Consistencia** | Mismo estado en todas las pestañas |

### 8.2 Para el Sistema

| Beneficio | Descripción |
|-----------|-------------|
| **Eficiencia** | Menor carga en el servidor (sin polling) |
| **Escalabilidad** | Una conexión por usuario, no N requests |
| **Bajo overhead** | Frames WebSocket muy ligeros |
| **Bidireccional** | Cliente puede confirmar lectura en tiempo real |

### 8.3 Comparativa de Rendimiento

```
┌────────────────────────────────────────────────────────────────────────────┐
│                 MÉTRICAS DE RENDIMIENTO (estimadas)                        │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│  Latencia de notificación:                                                 │
│  ─────────────────────────                                                 │
│  Polling (5s):    ████████████████████████████████████████ 0-5000ms       │
│  Long Polling:    ████████████ 0-1000ms                                    │
│  WebSocket:       ██ 10-100ms                                              │
│                                                                            │
│  Requests por hora (100 usuarios, sin eventos):                            │
│  ─────────────────────────────────────────────                             │
│  Short Polling:   ████████████████████████████████████████████████ 72,000 │
│  Long Polling:    ████████ ~3,600                                          │
│  WebSocket:       ██ ~7,200 heartbeats (pero mensajes mínimos)             │
│                                                                            │
│  Datos transferidos por usuario/hora (sin eventos):                        │
│  ─────────────────────────────────────────────────                         │
│  Polling (headers): ████████████████████████████████████ ~1.4 MB          │
│  WebSocket:         ██ ~50 KB (solo heartbeats)                            │
│                                                                            │
└────────────────────────────────────────────────────────────────────────────┘
```

---

## 9. Consideraciones de Producción

### 9.1 Escalabilidad Horizontal

Para múltiples instancias del backend, necesitarías un broker externo:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    ESCENARIO: MÚLTIPLES INSTANCIAS                          │
└─────────────────────────────────────────────────────────────────────────────┘

    Actualmente (Simple Broker - OK para 1 instancia):

    ┌──────────────┐
    │   Frontend   │────►┌──────────────────────────┐
    │   (React)    │     │  Backend Instance 1      │
    └──────────────┘     │  ┌──────────────────┐   │
                         │  │ Simple Broker    │   │
                         │  │ (In-Memory)      │   │
                         │  └──────────────────┘   │
                         └──────────────────────────┘


    Escalado (requiere RabbitMQ/Redis):

    ┌──────────────┐     ┌──────────────────────────┐
    │   Frontend   │────►│  Backend Instance 1      │
    │   (React)    │     └───────────┬──────────────┘
    └──────────────┘                 │
                                     ▼
                         ┌──────────────────────────┐
                         │   RabbitMQ / Redis       │
                         │   (Message Broker)       │
                         └──────────────────────────┘
                                     ▲
    ┌──────────────┐                 │
    │   Frontend   │────►┌───────────┴──────────────┐
    │   (React)    │     │  Backend Instance 2      │
    └──────────────┘     └──────────────────────────┘
```

### 9.2 Configuración Recomendada

```yaml
# application.yml para producción
spring:
  websocket:
    # Ajustar según carga esperada
    message-broker:
      relay:
        # Para escalado horizontal, usar RabbitMQ
        host: ${RABBITMQ_HOST:localhost}
        port: ${RABBITMQ_PORT:61613}
```

### 9.3 Monitoreo

Métricas importantes a monitorear:
- Conexiones WebSocket activas
- Mensajes enviados/recibidos por segundo
- Latencia de entrega
- Reconexiones por cliente
- Memoria del broker

---

## 10. Conclusiones

### Resumen de la Implementación

| Aspecto | Implementación |
|---------|---------------|
| **Protocolo** | WebSocket + STOMP |
| **Fallback** | SockJS para compatibilidad |
| **Broker** | Simple Broker (in-memory) |
| **Frontend** | @stomp/stompjs + Zustand |
| **Backend** | Spring WebSocket |
| **Autenticación** | Por userId en el path de suscripción |

### Beneficios Logrados

1. **Experiencia de usuario mejorada**: Notificaciones instantáneas sin necesidad de recargar
2. **Eficiencia del servidor**: Eliminamos el polling que generaba requests innecesarios
3. **Escalabilidad**: Arquitectura lista para crecer
4. **Mantenibilidad**: Código bien estructurado y separado

### Próximos Pasos Potenciales

- [ ] Implementar notificaciones push en móvil (PWA)
- [ ] Agregar broker externo (RabbitMQ) para escalado horizontal
- [ ] Dashboard de monitoreo de conexiones WebSocket
- [ ] Notificaciones persistentes offline (Service Workers)

---

**Documento actualizado**: Febrero 2026
