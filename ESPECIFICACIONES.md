# GeoRiesgoLite — Especificaciones de la API

Documento de referencia para el consumo de la API REST y el canal WebSocket desde aplicaciones móviles (iOS / Android).

- **Base URL (HTTP):** `http://<host>:<port>` (puerto por defecto `8080`)
- **Base URL (WebSocket):** `ws://<host>:<port>` (`wss://` en producción)
- **Formato:** JSON (UTF-8)
- **Autenticación:** JWT Bearer — encabezado `Authorization: Bearer <token>`
- **Swagger:** `http://localhost:8080/swagger-ui.html`

---

## 1. Resumen del proyecto

Sistema de gestión de riesgos e incidentes georreferenciados. Backend monolítico en Spring Boot 4.1 / Java 21 / MySQL. Entidades de dominio: usuarios, tipos de riesgo, riesgos, alertas y reportes ciudadanos.

## 2. Stack tecnológico

| Componente | Valor |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.0 |
| Persistencia | Spring Data JPA / MySQL |
| Seguridad | Spring Security + JWT (jjwt 0.12.6) |
| WebSocket | Spring WebSocket (canal `/ws/alertas`) |
| Docs | springdoc-openapi 2.8.8 |

## 3. Configuración (variables de entorno)

El datasource y el puerto se configuran por variables de entorno:

| Variable | Descripción |
|---|---|
| `MYSQLHOST` | Host de MySQL |
| `MYSQLPORT` | Puerto de MySQL |
| `MYSQLDATABASE` | Nombre de la base de datos |
| `MYSQLUSER` | Usuario de MySQL |
| `MYSQLPASSWORD` | Contraseña de MySQL |
| `PORT` | Puerto del servidor (por defecto `8080`) |

`spring.jpa.hibernate.ddl-auto=update` crea/actualiza las tablas automáticamente, pero la base de datos debe existir.

## 4. Autenticación

- `POST /api/auth/register` y `POST /api/auth/login` devuelven un `LoginResponse` con el `token`.
- El token expira a las **8 horas** (`jwt.expiration=28800000` ms).
- Para llamadas protegidas, enviar: `Authorization: Bearer <token>`.

---

## 5. Endpoints REST

Leyenda: **Público** = sin token, **Auth** = requiere token.

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/auth/register` | Público | Registro de usuario |
| POST | `/api/auth/login` | Público | Login (devuelve JWT) |
| GET | `/api/usuarios/me` | Auth | Perfil del usuario autenticado |
| PUT | `/api/usuarios/me` | Auth | Actualizar nombre del usuario |
| GET | `/api/tipos-riesgo` | Público | Listar tipos de riesgo |
| GET | `/api/tipos-riesgo/{id}` | Público | Detalle de tipo de riesgo |
| GET | `/api/riesgos` | Público | Listar riesgos (`?tipo=&nivel=`) |
| GET | `/api/riesgos/{id}` | Público | Detalle de riesgo |
| POST | `/api/reportes` | Público | Crear reporte ciudadano (multipart) |
| GET | `/api/reportes/{id}` | Auth | Detalle de reporte |
| GET | `/api/alertas` | Público | Listar alertas activas |
| POST | `/api/alertas` | Auth | Crear alerta (notifica por WebSocket) |
| GET | `/api/alertas/{id}` | Auth | Detalle de alerta |

### 5.1 Auth

**POST `/api/auth/register`**

```json
{
  "nombre": "Juan Pérez",
  "email": "juan@correo.com",
  "password": "secreto123"
}
```

**POST `/api/auth/login`**

```json
{
  "email": "juan@correo.com",
  "password": "secreto123"
}
```

**Respuesta (ambos):** `LoginResponse`

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "usuario": {
    "id": 1,
    "nombre": "Juan Pérez",
    "email": "juan@correo.com"
  }
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
  "tipoRiesgoId": 1,
  "descripcion": "Incendio en la zona norte",
  "latitud": -12.0464,
  "longitud": -77.0428
}
```

### 5.4 Alertas

**POST `/api/alertas`** (Auth) — crea una alerta y la notifica a todos los clientes conectados por WebSocket.

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

| Campo | Tipo | Notas |
|---|---|---|
| `id` | Long | |
| `nombre` | String | |
| `email` | String | |

```json
{ "id": 1, "nombre": "Juan Pérez", "email": "juan@correo.com" }
```

### 7.2 `LoginResponse`

| Campo | Tipo |
|---|---|
| `token` | String |
| `usuario` | `UsuarioDto` |

### 7.3 `RegisterRequest`

| Campo | Tipo | Validación |
|---|---|---|
| `nombre` | String | obligatorio |
| `email` | String | obligatorio, formato email |
| `password` | String | obligatorio, mín. 6 caracteres |

### 7.4 `LoginRequest`

| Campo | Tipo | Validación |
|---|---|---|
| `email` | String | obligatorio, formato email |
| `password` | String | obligatorio |

### 7.5 `UpdateUsuarioRequest`

| Campo | Tipo | Validación |
|---|---|---|
| `nombre` | String | obligatorio |

### 7.6 `TipoRiesgoResponse`

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

### 7.7 `RiesgoResponse`

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

### 7.8 `ReporteRequest`

| Campo | Tipo | Validación |
|---|---|---|
| `tipoRiesgoId` | Long | obligatorio |
| `descripcion` | String | obligatorio |
| `latitud` | Double | obligatorio |
| `longitud` | Double | obligatorio |

### 7.9 `ReporteResponse`

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

### 7.10 `AlertaResponse`

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

### 7.11 `AlertaRequest` (crear alerta)

| Campo | Tipo | Validación |
|---|---|---|
| `titulo` | String | obligatorio |
| `descripcion` | String | obligatorio |
| `tipoRiesgoId` | Long | obligatorio |
| `nivel` | String | obligatorio, enum `NivelRiesgo` |
| `latitud` | Double | obligatorio |
| `longitud` | Double | obligatorio |
| `riesgoId` | Long | opcional |

### 7.12 `AlertaEvent` (mensaje WebSocket)

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
| 400 | Validación fallida / credenciales inválidas / correo ya registrado |
| 401 | Token ausente o inválido |
| 404 | Recurso no encontrado |
| 500 | Error interno del servidor |
