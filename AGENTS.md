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

MySQL must be running and a database `georiesgolite` must exist before starting the app. The datasource config is in `src/main/resources/application.properties`.

Without MySQL, both `./mvnw spring-boot:run` and `mvn test` fail with a connection error.

## Spring Security

- JWT-based auth with `SecurityConfig`, `JwtService`, and `JwtAuthenticationFilter`.
- Public endpoints (no auth): `/api/auth/**`, `/api/tipos-riesgo/**`, `/api/riesgos/**`, `/swagger-ui/**`, `/api-docs/**`
- All other endpoints require a valid JWT token (`Authorization: Bearer <token>`).
- Passwords are hashed with BCrypt.
- JWT secret/expiration are in `application.properties` (not environment variables in dev).

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
| POST | `/api/reportes` | Yes | Create citizen report (multipart: `datos` + optional `foto`) |
| GET | `/api/reportes/{id}` | Yes | Get report by ID |
| GET | `/api/alertas` | Yes | List active alerts |
| GET | `/api/alertas/{id}` | Yes | Get alert detail |

## Package structure

| Package | Purpose |
|---|---|
| `entity` | JPA entities: Usuario, Riesgo, TipoRiesgo, Reporte, Alerta |
| `enums` | NivelRiesgo, EstadoReporte |
| `repository` | Spring Data JPA repositories |
| `dto` | Request/response DTOs (separated from entities) |
| `service` | Business logic |
| `controller` | REST controllers |
| `security` | JwtService, JwtAuthenticationFilter, SecurityConfig |
| `exception` | GlobalExceptionHandler, ResourceNotFoundException, BadRequestException |
| `config` | OpenApiConfig, DataInitializer |

**Important convention**: DTOs are preferred over exposing entities directly in API responses. The `@AuthenticationPrincipal Usuario` pattern extracts the user from the JWT via `SecurityContext`.
