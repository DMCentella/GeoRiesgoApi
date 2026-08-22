# GeoRiesgoLite — Especificaciones de la API

Documento de referencia para el consumo de la API REST y el canal WebSocket desde la aplicación iOS (y Android).

- **Base URL (HTTP):** `http://<host>:<port>` (puerto por defecto `8080`)
- **Base URL (WebSocket):** `ws://<host>:<port>` (`wss://` en producción)
- **Formato:** JSON (UTF-8)
- **Autenticación:** ninguna — todos los endpoints son públicos
- **Swagger:** `http://localhost:8080/swagger-ui.html`

---

## 1. Resumen del proyecto

Sistema de gestión de riesgos e incidentes georreferenciados. Backend monolítico demo en Spring Boot 4.1 / Java 21 / MySQL. El registro/login se hace en **Firebase Authentication dentro de la app iOS**; el backend no valida tokens ni guarda ningún UID de Firebase.

## 2. Stack tecnológico

| Componente | Valor |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.0 |
| Persistencia | Spring Data JPA / MySQL |
| WebSocket | Spring WebSocket (canal `/ws/alertas`) |
| Docs | springdoc-openapi 2.8.8 |

## 3. Configuración (variables de entorno)

| Variable | Descripción |
|---|---|
| `DB_URL` | URL JDBC completa de MySQL |
| `DB_USERNAME` | Usuario de MySQL |
| `DB_PASSWORD` | Contraseña de MySQL |
| `PORT` | Puerto del servidor (por defecto `8080`) |

`spring.jpa.hibernate.ddl-auto=update` crea/actualiza las tablas automáticamente, pero la base de datos debe existir.

## 4. Identificación del usuario

No hay autenticación. El backend identifica al usuario mediante un `usuarioId` explícito que la app envía en el body o como query param. Ejemplos:

- `GET /api/usuarios/me?usuarioId=1`
- `GET /api/reportes/mis-reportes?usuarioId=1`
- `POST /api/reportes` → `datos.usuarioId`

---

## 5. Endpoints REST

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/usuarios/me?usuarioId=` | Perfil de un usuario por id |
| PUT | `/api/usuarios/me` | Actualizar nombre (body: `usuarioId` + `nombre`) |
| GET | `/api/tipos-riesgo` | Listar tipos de riesgo |
| GET | `/api/tipos-riesgo/{id}` | Detalle de tipo de riesgo |
| GET | `/api/riesgos` | Listar riesgos (`?tipo=&nivel=`) |
| GET | `/api/riesgos/{id}` | Detalle de riesgo |
| POST | `/api/reportes` | Crear reporte ciudadano (multipart) |
| GET | `/api/reportes/mis-reportes?usuarioId=` | Listar reportes de un usuario |
| GET | `/api/reportes/{id}` | Detalle de reporte |
| GET | `/api/alertas` | Listar alertas activas |
| POST | `/api/alertas` | Crear alerta (notifica por WebSocket) |
| GET | `/api/alertas/{id}` | Detalle de alerta |
| WS | `/ws/alertas` | WebSocket: notificaciones en tiempo real |

### 5.1 Usuarios

**GET `/api/usuarios/me?usuarioId=1`**

Respuesta (`UsuarioDto`):

```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "email": "juan@correo.com"
}
```

**PUT `/api/usuarios/me`**

```json
{
  "usuarioId": 1,
  "nombre": "Juan Actualizado"
}
```

### 5.2 Riesgos

**GET `/api/riesgos?tipo=1&nivel=ALTO`**

Filtros opcionales: `tipo` (id de tipo de riesgo) y `nivel` (BAJO, MODERADO, ALTO, CRITICO).

### 5.3 Reportes (multipart)

**POST `/api/reportes`** — `Content-Type: multipart/form-data`

| Part | Tipo | Requerido |
|---|---|---|
| `datos` | JSON (`ReporteRequest`) | Sí |
| `foto` | archivo (imagen) | No (máx. 10 MB) |

`datos` (JSON):

```json
{
  "usuarioId": 1,
  "tipoRiesgoId": 1,
  "descripcion": "Incendio en la zona norte",
  "latitud": -12.0464,
  "longitud": -77.0428
}
```

**GET `/api/reportes/mis-reportes?usuarioId=1`**

Devuelve una lista de `ReporteResponse`.

### 5.4 Alertas

**POST `/api/alertas`** — crea una alerta y la notifica a todos los clientes conectados por WebSocket.

```json
{
  "titulo": "Incendio forestal en cerro",
  "descripcion": "Foco de incendio activo cerca de viviendas",
  "tipoRiesgoId": 1,
  "nivel": "CRITICO",
  "latitud": -12.0464,
  "longitud": -77.0428,
  "riesgoId": null
}
```

---

## 6. WebSocket — Alertas en tiempo real

- **Endpoint:** `ws://<host>:<port>/ws/alertas`
- **Acceso:** público (sin token).
- El servidor envía un mensaje JSON a todos los clientes conectados cada vez que se crea una alerta vía `POST /api/alertas`.
- No requiere SockJS/STOMP: es un WebSocket estándar compatible con `URLSessionWebSocketTask` (iOS) y `OkHttp` (Android).

### 6.1 Formato del mensaje

```json
{
  "type": "ALERTA_NUEVA",
  "timestamp": "2026-08-21T18:04:32.123456",
  "data": {
    "id": 10,
    "titulo": "Incendio forestal en cerro",
    "descripcion": "Foco de incendio activo cerca de viviendas",
    "tipoRiesgo": "INCENDIO",
    "nivel": "CRITICO",
    "latitud": -12.0464,
    "longitud": -77.0428,
    "activa": true,
    "riesgoId": null
  }
}
```

| Campo | Tipo | Descripción |
|---|---|---|
| `type` | string | Tipo de evento (actualmente `ALERTA_NUEVA`) |
| `timestamp` | string (ISO-8601) | Momento del evento |
| `data` | objeto `AlertaResponse` | Datos de la alerta |

---

## 7. DTOs (modelos para mapear en la app)

> Los tipos de fecha se serializan en ISO-8601 (`"2026-08-21T18:04:32.123456"`). En Swift usar `Date`/`DateFormatter` (ISO8601), en Kotlin `java.time.LocalDateTime` o `String`.

### 7.1 `UsuarioDto`

| Campo | Tipo |
|---|---|
| `id` | Long |
| `nombre` | String |
| `email` | String |

```json
{ "id": 1, "nombre": "Juan Pérez", "email": "juan@correo.com" }
```

### 7.2 `UpdateUsuarioRequest`

| Campo | Tipo | Validación |
|---|---|---|
| `usuarioId` | Long | obligatorio |
| `nombre` | String | obligatorio |

### 7.3 `TipoRiesgoResponse`

| Campo | Tipo |
|---|---|
| `id` | Long |
| `nombre` | String |
| `descripcion` | String |
| `icono` | String |
| `activo` | Boolean |

```json
{
  "id": 1,
  "nombre": "INCENDIO",
  "descripcion": "Incendio forestal o urbano",
  "icono": "flame",
  "activo": true
}
```

### 7.4 `RiesgoResponse`

| Campo | Tipo | Notas |
|---|---|---|
| `id` | Long | |
| `tipo` | String | nombre del tipo de riesgo |
| `titulo` | String | |
| `descripcion` | String | |
| `nivel` | String | enum `NivelRiesgo` |
| `latitud` | Double | |
| `longitud` | Double | |
| `fechaRegistro` | String (ISO-8601) | |
| `fechaActualizacion` | String (ISO-8601) | nullable |
| `activo` | Boolean | |

### 7.5 `ReporteRequest`

| Campo | Tipo | Validación |
|---|---|---|
| `usuarioId` | Long | obligatorio |
| `tipoRiesgoId` | Long | obligatorio |
| `descripcion` | String | obligatorio |
| `latitud` | Double | obligatorio |
| `longitud` | Double | obligatorio |

### 7.6 `ReporteResponse`

| Campo | Tipo | Notas |
|---|---|---|
| `id` | Long | |
| `usuario` | `UsuarioDto` | |
| `tipoRiesgo` | String | nombre del tipo de riesgo |
| `descripcion` | String | |
| `latitud` | Double | |
| `longitud` | Double | |
| `fotoUrl` | String | nullable |
| `fechaRegistro` | String (ISO-8601) | |
| `estado` | String | enum `EstadoReporte` |

### 7.7 `AlertaResponse`

| Campo | Tipo | Notas |
|---|---|---|
| `id` | Long | |
| `titulo` | String | |
| `descripcion` | String | |
| `tipoRiesgo` | String | nombre del tipo de riesgo |
| `nivel` | String | enum `NivelRiesgo` |
| `latitud` | Double | |
| `longitud` | Double | |
| `activa` | Boolean | |
| `riesgoId` | Long | nullable |

### 7.8 `AlertaRequest` (crear alerta)

| Campo | Tipo | Validación |
|---|---|---|
| `titulo` | String | obligatorio |
| `descripcion` | String | obligatorio |
| `tipoRiesgoId` | Long | obligatorio |
| `nivel` | String | obligatorio, enum `NivelRiesgo` |
| `latitud` | Double | obligatorio |
| `longitud` | Double | obligatorio |
| `riesgoId` | Long | opcional |

### 7.9 `AlertaEvent` (mensaje WebSocket)

| Campo | Tipo |
|---|---|
| `type` | String |
| `timestamp` | String (ISO-8601) |
| `data` | `AlertaResponse` |

---

## 8. Enums

### `NivelRiesgo`

```
BAJO, MODERADO, ALTO, CRITICO
```

### `EstadoReporte`

```
PENDIENTE, VERIFICADO, DESCARTADO
```

### Tipos de riesgo semilla

| id | nombre |
|---|---|
| 1 | INCENDIO |
| 2 | INUNDACION |
| 3 | DESBORDE |
| 4 | DESLIZAMIENTO |
| 5 | SISMO |

---

## 9. Formato de error

Todos los errores devuelven un objeto `ErrorResponse`:

```json
{
  "status": 404,
  "message": "Alerta no encontrada",
  "timestamp": "2026-08-21T18:04:32.123456"
}
```

| Código | Significado |
|---|---|
| 400 | Validación fallida |
| 404 | Recurso no encontrado |
| 500 | Error interno del servidor |
