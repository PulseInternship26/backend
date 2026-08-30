# Bookstore API

Spring Boot REST API for a small bookstore. It provides JWT authentication,
role-based access control, book management, and administrator management.

## Features

- Public user registration and login
- Stateless JWT authentication with BCrypt password hashing
- `USER` access to the book catalogue and book details
- `ADMIN` access to book CRUD and administrator management
- Database-backed JWT validation so deleted accounts lose access immediately
- Bean Validation with consistent JSON error responses
- OpenAPI documentation and Swagger UI
- PostgreSQL persistence and isolated H2 integration tests
- Optional development accounts seeded through the `dev` profile

## Technology

- Java 21
- Spring Boot 4
- Spring Security and JJWT
- Spring Data JPA and PostgreSQL
- Springdoc OpenAPI
- Maven Wrapper
- JUnit, MockMvc, and H2

## Requirements

- Java 21
- Docker with Docker Compose, or a local PostgreSQL instance
- A Base64-encoded JWT secret that decodes to at least 32 bytes

## Configuration

The application reads configuration from environment variables. The root
`.env.example` is a reference file; export its values in your shell or provide
them through your IDE or deployment environment.

| Variable | Required | Default | Description |
| --- | --- | --- | --- |
| `DB_PASSWORD` | Yes | - | PostgreSQL password |
| `JWT_SECRET` | Yes | - | Base64-encoded JWT signing key |
| `DATASOURCE_URL` | No | `jdbc:postgresql://127.0.0.1:5432/bookstore` | JDBC connection URL |
| `DB_USERNAME` | No | `postgres` | PostgreSQL username |
| `SERVER_PORT` | No | `8081` | HTTP port |
| `JWT_TTL` | No | `3600` | Token lifetime in seconds |
| `SPRING_PROFILES_ACTIVE` | No | - | Set to `dev` to seed development users |
| `SEED_ADMIN_PASSWORD` | With `dev` | - | Seeded administrator password |
| `SEED_USER_PASSWORD` | With `dev` | - | Seeded user password |

Generate a suitable signing key with:

```bash
openssl rand -base64 32
```

## Local Setup

1. Start PostgreSQL:

```bash
docker compose up -d
```

2. Export the application settings. Ensure `DB_PASSWORD` matches the
   `POSTGRES_PASSWORD` used by Docker Compose.

```bash
export DB_PASSWORD=0000
export JWT_SECRET="$(openssl rand -base64 32)"
export SPRING_PROFILES_ACTIVE=dev
export SEED_ADMIN_PASSWORD=Admin123
export SEED_USER_PASSWORD=User123
```

3. Run the API:

```bash
./mvnw spring-boot:run
```

The API is available at `http://localhost:8081` by default.

## Development Accounts

When the `dev` profile is active, `DevDataSeeder` creates or updates these
accounts on every startup:

| Email | Role | Password |
| --- | --- | --- |
| `admin@bookstore.local` | `ADMIN` | Value of `SEED_ADMIN_PASSWORD` |
| `user@bookstore.local` | `USER` | Value of `SEED_USER_PASSWORD` |

The seeder resets matching accounts to the configured password, phone, and
role. Deleted seeded users are recreated on the next development startup. It
does not seed books and is not active outside the `dev` profile.

## Authentication

Login returns a JWT. Send it on protected requests using:

```http
Authorization: Bearer <token>
```

Public registration always creates a `USER`. Only an authenticated `ADMIN`
can create another administrator through `/api/admins`.

## API Summary

| Method | Endpoint | Access | Description |
| --- | --- | --- | --- |
| `POST` | `/api/auth/register` | Public | Register a user |
| `POST` | `/api/auth/login` | Public | Authenticate and receive a JWT |
| `GET` | `/api/users/me` | Authenticated | Return the current account |
| `GET` | `/api/books` | Authenticated | List book summaries |
| `GET` | `/api/books/{id}` | Authenticated | Get full book details |
| `POST` | `/api/books` | `ADMIN` | Create a book |
| `PUT` | `/api/books/{id}` | `ADMIN` | Replace a book |
| `DELETE` | `/api/books/{id}` | `ADMIN` | Delete a book |
| `GET` | `/api/admins` | `ADMIN` | List administrators |
| `POST` | `/api/admins` | `ADMIN` | Create an administrator |
| `DELETE` | `/api/admins/{id}` | `ADMIN` | Delete an administrator |

Book list responses omit `description`; the detail and mutation responses
include all book fields. List endpoints are currently unpaged.

## Validation and Errors

- Account passwords must contain 8 to 50 characters.
- Phone numbers may start with `+` and must contain 10 to 15 digits.
- Book text fields are required; description is limited to 1000 characters.
- Book price must be greater than zero.
- Duplicate account emails return `409 Conflict`.

Handled errors use a consistent shape:

```json
{
  "status": 400,
  "message": "email: must be a well-formed email address",
  "timestamp": "2026-08-30T12:00:00"
}
```

## API Documentation

With the application running:

- Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`
- OpenAPI YAML: `http://localhost:8081/v3/api-docs.yaml`

## Postman

Import the collections from `postman/`:

- `Bookstore-Books.postman_collection.json`
- `Bookstore-Admins.postman_collection.json`

Each collection contains its own variables, scripts, and assertions; a
separate Postman environment is not required. The default administrator and
user credentials match `.env.example`. Update the collection variables if you
use different seed passwords. Run folders and requests in their numbered order
because login and create requests save IDs and tokens for later checks.

## Tests and Build

Run the integration test suite:

```bash
./mvnw clean verify
```

Tests use the `test` profile and an in-memory H2 database, so PostgreSQL and
development seed credentials are not required. Build output is written to
`target/`.

## Project Structure

```text
src/main/java/com/pulseinternship/bookstore/
|- auth/        JWT filter, token service, and security error handlers
|- config/      security, OpenAPI, and development seed configuration
|- controller/  REST endpoints
|- dto/         request and response records
|- entity/      JPA entities and roles
|- exception/   application exceptions and global error mapping
|- repository/  Spring Data repositories
`- service/     authentication and domain operations
```

## Frontend Integration

The companion Angular application sends relative `/api` requests. During local
development its proxy forwards those requests to `http://localhost:8081`. A
production deployment should serve both applications behind the same origin or
configure a reverse proxy that routes `/api` to this service.
