# AGENTS.md

## Project

Single-module Spring Boot 4.1.0 / Java 21 / MySQL risk management system (GeoRiesgoLite). Demo backend for the iOS app — **no authentication**. Monolithic, no microservices.

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

MySQL must be running before starting the app or running tests. The datasource is configured via environment variables: `DB_URL` (full JDBC URL), `DB_USERNAME`, `DB_PASSWORD` (see `src/main/resources/application.properties`). `server.port` defaults to 8080 via `${PORT:8080}`.

`spring.jpa.hibernate.ddl-auto=update` auto-creates/updates tables, but the target database itself must already exist. Without MySQL, both `./mvnw spring-boot:run` and `mvn test` fail with a connection error.

## Authentication

There is **no** authentication or authorization — all endpoints are public. Firebase Authentication is handled entirely client-side (iOS); the backend neither validates tokens nor stores a Firebase UID. Do NOT re-introduce Spring Security, JWT, `PasswordEncoder`, `AuthenticationManager`, or a `firebase_uid` column.

The client identifies the user explicitly via a `usuarioId` (body or query param), never via a token/principal.

## Testing

One test: `contextLoads()` in `GeoRiesgoLiteApplicationTests.java`. Requires a working MySQL connection.

No linter, formatter, or typecheck command configured.

## Seed data

`DataInitializer` auto-seeds 5 `TipoRiesgo` entries (INCENDIO, INUNDACION, DESBORDE, DESLIZAMIENTO, SISMO) on first startup when the table is empty.

## API endpoints (Swagger: http://localhost:8080/swagger-ui.html)

All public. See `ESPECIFICACIONES.md` for full request/response JSON specs (intended for iOS/Android clients).

| Method | Path | Description |
|---|---|---|
| GET | `/api/usuarios/me?usuarioId=` | Get user by id |
| PUT | `/api/usuarios/me` | Update user name (body: `usuarioId` + `nombre`) |
| GET | `/api/tipos-riesgo` | List risk types |
| GET | `/api/tipos-riesgo/{id}` | Get risk type by ID |
| GET | `/api/riesgos` | List risks (filters: `?tipo=&nivel=`) |
| GET | `/api/riesgos/{id}` | Get risk detail |
| POST | `/api/reportes` | Create report (multipart: `datos` + optional `foto`, 10MB max) |
| GET | `/api/reportes/mis-reportes?usuarioId=` | List reports for a user |
| GET | `/api/reportes/{id}` | Get report by ID |
| GET | `/api/alertas` | List active alerts |
| POST | `/api/alertas` | Create alert (broadcasts via WebSocket) |
| GET | `/api/alertas/{id}` | Get alert detail |
| WS | `/ws/alertas` | WebSocket: real-time alert notifications |

## Package structure

| Package | Purpose |
|---|---|
| `entity` | JPA entities: Usuario, Riesgo, TipoRiesgo, Reporte, Alerta |
| `enums` | NivelRiesgo, EstadoReporte |
| `repository` | Spring Data JPA repositories |
| `dto` | Request/response DTOs (separated from entities) |
| `service` | Business logic |
| `controller` | REST controllers |
| `exception` | GlobalExceptionHandler, ResourceNotFoundException, BadRequestException |
| `config` | OpenApiConfig, DataInitializer, WebSocketConfig |
| `websocket` | AlertaWebSocketHandler (session registry + broadcast) |

**Important conventions**: DTOs are preferred over exposing entities directly in API responses. `ReporteRequest` and `UpdateUsuarioRequest` carry an explicit `usuarioId` (there is no auth principal). `IncidenteController` is an empty stub — no `/api/incidentes` endpoints. Lombok is used throughout (builders, `@RequiredArgsConstructor`, `@Data`). Do not add a custom `ObjectMapper` bean — Spring Boot's auto-configured one (with `JavaTimeModule`) is required to serialize `LocalDateTime`.
