# AGENTS.md

## Project

Single-module Spring Boot 4.1.0 / Java 21 / MySQL risk management system (GeoRiesgoLite). Monolithic — no microservices.

- GroupId: `com.cibertec.edu`
- Entrypoint: `src/main/java/com/cibertec/edu/app/GeoRiesgoLiteApplication.java`

## Build & run

```bash
./mvnw clean package      # (Linux/macOS)
mvnw.cmd clean package    # (Windows)
```

The Spring Boot Maven plugin repackages into a fat jar. No special profiles or extra flags needed.

If JDK 21 is not installed, download and set JAVA_HOME:

```bash
curl -sL "https://api.adoptium.net/v3/binary/latest/21/ga/linux/x64/jdk/hotspot/normal/eclipse?project=jdk" -o /tmp/jdk21.tar.gz
mkdir -p /tmp/jdk21 && tar -xzf /tmp/jdk21.tar.gz -C /tmp/jdk21 --strip-components=1
JAVA_HOME=/tmp/jdk21 ./mvnw clean compile
```

## Runtime prerequisites

MySQL must be running before starting the app or running tests. The datasource is configured via environment variables, not hardcoded: `MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, `MYSQLPASSWORD` (see `src/main/resources/application.properties`). `server.port` defaults to 8080 via `${PORT:8080}`. Firebase is optional at runtime via `FIREBASE_CREDENTIALS_PATH` (path to a service-account JSON, git-ignored).

`spring.jpa.hibernate.ddl-auto=update` auto-creates/updates tables, but the target database itself must already exist. Without MySQL, both `./mvnw spring-boot:run` and `mvn test` fail with a connection error.

## Authentication (Firebase)

- Auth is Firebase-based: clients send a **Firebase ID Token** in `Authorization: Bearer <token>`. `FirebaseAuthenticationFilter` verifies it via Firebase Admin SDK (`FirebaseService.verifyIdToken`), resolves the `Usuario` by `firebaseUid` (or migrates by `email`), and puts it in the `SecurityContext` with authority `ROLE_<rol>`.
- If the token's user doesn't exist in MySQL, the filter auto-provisions a `Usuario` with `rol=VECINO` (auto-registration). `rol` is always backend-controlled (`VECINO`/`ADMIN`); never trust a client-sent role.
- Firebase is initialized in `config/FirebaseConfig` from `firebase.credentials.path` (env `FIREBASE_CREDENTIALS_PATH`). If unset, auth is disabled (filter returns no authentication) and the app still starts.
- `requestMatchers` use **method-specific** matchers for non-wildcard paths. Public: `/api/auth/**`, `/api/tipos-riesgo/**`, `/api/riesgos/**`, `GET /api/alertas`, `/ws/**`, swagger paths. `POST /api/alertas` requires `ADMIN`. Everything else (incl. `POST /api/reportes` and the `/{id}` subpaths) requires any authenticated user.
- Legacy: `JwtService`/`JwtAuthenticationFilter`/`/api/auth/register|login` still exist for migration but are NOT wired into the security chain. They generate a self-signed JWT that the backend no longer accepts.

## Testing

One test: `contextLoads()` in `GeoRiesgoLiteApplicationTests.java`. Requires a working MySQL connection.

No linter, formatter, or typecheck command configured.

## Seed data

`DataInitializer` auto-seeds 5 `TipoRiesgo` entries (INCENDIO, INUNDACION, DESBORDE, DESLIZAMIENTO, SISMO) on first startup when the table is empty.

## API endpoints (Swagger: http://localhost:8080/swagger-ui.html)

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | No | Register user |
| POST | `/api/auth/login` | No | Login, returns JWT |
| GET | `/api/usuarios/me` | Yes | Get authenticated user profile |
| PUT | `/api/usuarios/me` | Yes | Update authenticated user profile |
| GET | `/api/tipos-riesgo` | No | List risk types |
| GET | `/api/tipos-riesgo/{id}` | No | Get risk type by ID |
| GET | `/api/riesgos` | No | List risks (filters: `?tipo=&nivel=`) |
| GET | `/api/riesgos/{id}` | No | Get risk detail |
| POST | `/api/reportes` | Yes | Create citizen report (multipart: `datos` + optional `foto`, 10MB max) |
| GET | `/api/reportes/{id}` | Yes | Get report by ID |
| GET | `/api/alertas` | No | List active alerts |
| POST | `/api/alertas` | ADMIN | Create alert (broadcasts via WebSocket) |
| GET | `/api/alertas/{id}` | Yes | Get alert detail |
| WS | `/ws/alertas` | No | WebSocket: real-time alert notifications |

See `ESPECIFICACIONES.md` for full API/DTO specs (intended for iOS/Android clients).

## Package structure

| Package | Purpose |
|---|---|
| `entity` | JPA entities: Usuario, Riesgo, TipoRiesgo, Reporte, Alerta |
| `enums` | NivelRiesgo, EstadoReporte, Rol |
| `repository` | Spring Data JPA repositories |
| `dto` | Request/response DTOs (separated from entities) |
| `service` | Business logic |
| `controller` | REST controllers |
| `security` | FirebaseAuthenticationFilter, FirebaseService, JwtService, JwtAuthenticationFilter (legacy), SecurityConfig |
| `exception` | GlobalExceptionHandler, ResourceNotFoundException, BadRequestException |
| `config` | OpenApiConfig, DataInitializer, WebSocketConfig, FirebaseConfig |
| `websocket` | AlertaWebSocketHandler (session registry + broadcast) |

**Important convention**: DTOs are preferred over exposing entities directly in API responses. The `@AuthenticationPrincipal Usuario` pattern extracts the user from the Firebase token via `SecurityContext`.

`IncidenteController` is an empty stub — there are no `/api/incidentes` endpoints yet. Lombok is used throughout (builders, `@RequiredArgsConstructor`, `@Data`).
