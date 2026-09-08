# coc-materials

A production-style raw-material inventory application built to practice
backend and full-stack engineering patterns with Kotlin, Spring Boot,
Hibernate/JPA, PostgreSQL, and React/Next.js.

The backend currently supports raw-material CRUD operations, partial
updates, filtering, sorting, pagination, relational data, database
constraints, optimistic locking, health checks, and PostgreSQL
integration testing.

> The frontend is the next phase of the project and has not been
> implemented yet.

## Tech Stack

### Backend

-   Kotlin
-   Java 21
-   Spring Boot
-   Spring MVC
-   Spring Data JPA
-   Hibernate
-   PostgreSQL
-   Flyway
-   HikariCP
-   Spring Boot Actuator
-   Gradle

### Testing

-   JUnit 5
-   Spring Boot Test
-   Testcontainers
-   PostgreSQL container

### Planned Frontend

-   React
-   Next.js
-   TypeScript

## Domain Model

The application currently models:

-   Suppliers
-   User groups
-   Users
-   Storage areas
-   Storage locations
-   Raw materials

A raw material contains information such as:

-   product code
-   Corning part number
-   lot number
-   material type
-   material class
-   form
-   category
-   quantity and unit of measure
-   supplier
-   owner
-   storage location
-   date in / date out
-   notes
-   optimistic-lock version

## Database

PostgreSQL is used as the application database.

Schema changes are managed exclusively through Flyway migrations.
Hibernate is configured to validate the schema rather than generate it.

Current migrations:

``` text
V1__create_initial_schema.sql
V2__add_version_to_raw_materials.sql
```

Important database rules include:

-   quantity must be non-negative
-   `date_out` must be null or greater than/equal to `date_in`
-   `(supplier_id, lot_number)` must be unique
-   foreign-key constraints protect relationships between domain tables
-   indexes support relationship-based queries
-   `version` supports optimistic locking

## API

The raw-material API is exposed under:

``` text
/api/raw-materials
```

It supports:

-   create
-   get by ID
-   list
-   update
-   partial update
-   delete
-   pagination
-   sorting
-   dynamic filtering

Example:

``` http
GET /api/raw-materials?page=0&size=25&sort=dateIn,desc
```

### Pagination

Default page size:

``` text
25
```

Maximum page size:

``` text
100
```

### PATCH

PATCH supports partial updates.

Example:

``` http
PATCH /api/raw-materials/1
Content-Type: application/json

{
  "version": 0,
  "quantity": 15.500,
  "notes": "Adjusted after inventory recount"
}
```

The PATCH mapper validates:

-   supported property names
-   JSON value types
-   nullable vs non-nullable fields
-   numeric values
-   timestamps
-   relationship IDs
-   relationship existence
-   cross-field date rules

Unknown properties are rejected instead of silently ignored.

## Optimistic Locking

`RawMaterial` uses JPA's `@Version` mechanism.

A client receives a version with a raw-material response:

``` json
{
  "id": 1,
  "version": 0
}
```

The client sends that version with an update. After a successful update,
Hibernate increments the version.

A stale update is rejected with HTTP `409 Conflict` rather than silently
overwriting a newer change.

This protects against lost updates when multiple users edit the same
inventory record.

## Persistence and Querying

The persistence layer uses Spring Data JPA plus a custom raw-material
repository implementation for more complex querying.

The implementation includes concepts such as:

-   `EntityManager`
-   JPA Criteria API
-   `CriteriaBuilder`
-   `CriteriaQuery`
-   `Root`
-   predicates
-   Spring Data `Specification`
-   fetch joins
-   `Pageable`
-   offset/limit pagination
-   separate count queries
-   `PageImpl`
-   controlled sorting

Relationships are loaded deliberately to avoid accidental N+1 query
behavior.

Open Session in View is disabled:

``` yaml
spring:
  jpa:
    open-in-view: false
```

This keeps persistence-context boundaries explicit.

## Connection Pooling

The application uses HikariCP through Spring Boot's datasource
auto-configuration.

Example configuration:

``` yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      connection-timeout: 30000
```

Database connections are borrowed from the pool while needed and
returned for reuse afterward.

Pool sizing should be considered together with the number of deployed
application instances and the PostgreSQL connection budget.

## Configuration

Database credentials are provided through environment variables rather
than committed to source control.

Example:

``` text
DB_USERNAME=...
DB_PASSWORD=...
```

The datasource configuration references them:

``` yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/coc_materials
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

Verbose Hibernate SQL logging belongs in the development profile rather
than the shared production-oriented configuration.

Example `application-dev.yml`:

``` yaml
spring:
  config:
    activate:
      on-profile: dev

  jpa:
    properties:
      hibernate:
        format_sql: true

logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.orm.jdbc.bind: TRACE
```

## Health Checks

Spring Boot Actuator exposes application health information.

``` http
GET /actuator/health
```

Liveness and readiness health groups are available:

``` http
GET /actuator/health/liveness
GET /actuator/health/readiness
```

These provide a foundation for future container-orchestration health
checks.

## Error Handling

The API uses centralized exception handling to return consistent HTTP
errors.

Examples include:

-   `400 Bad Request` for invalid input
-   `404 Not Found` for missing resources
-   `409 Conflict` for uniqueness violations
-   `409 Conflict` for optimistic-lock conflicts

Database constraint violations are translated into domain/API errors
where appropriate rather than exposing raw database exceptions.

Production-facing error responses do not expose application stack
traces.

## Integration Testing

Integration tests run against a real temporary PostgreSQL instance using
Testcontainers.

Test flow:

``` text
JUnit
  ↓
Testcontainers
  ↓
Docker
  ↓
temporary PostgreSQL
  ↓
Flyway migrations
  ↓
Spring Boot / Hibernate
  ↓
integration test
```

The essential integration suite currently verifies:

1.  the Spring application and PostgreSQL schema can bootstrap
    successfully
2.  a raw material can be created through the application service
3.  duplicate `(supplier, lot number)` records are rejected
4.  stale updates are rejected by optimistic locking

Tests are isolated from the normal development database.

Docker Desktop (or another Testcontainers-compatible container runtime)
must be running to execute these tests.

## Running Locally

### Prerequisites

-   Java 21
-   PostgreSQL
-   Docker Desktop for integration tests
-   Gradle/Gradle Wrapper

### Database

Create a PostgreSQL database:

``` text
coc_materials
```

Set the database credentials in the environment:

``` bash
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
```

Start the application using the development profile.

Flyway will apply pending migrations automatically and Hibernate will
validate the resulting schema.

## Development Principles

This project intentionally follows production-oriented practices rather
than treating the application as a simple CRUD tutorial.

Current principles include:

-   schema changes through Flyway
-   database constraints as a final integrity boundary
-   DTOs rather than exposing JPA entities directly
-   explicit transaction boundaries
-   Open Session in View disabled
-   pagination for collection endpoints
-   controlled sorting
-   dynamic filtering
-   deliberate relationship fetching
-   centralized API error handling
-   environment-based credentials
-   optimistic concurrency control
-   database connection pooling
-   application health checks
-   integration tests against real PostgreSQL

## Next Phase

The next phase is the frontend.

The initial frontend work will focus on:

1.  raw-material list page
2.  pagination
3.  sorting
4.  filtering
5.  loading and error states
6.  create/edit forms
7.  supplier, owner, and storage-location selection
8.  PATCH/PUT integration
9.  optimistic-lock conflict handling

The frontend will consume the existing Spring Boot API and gradually
turn the backend functionality into a complete inventory-management
workflow.
