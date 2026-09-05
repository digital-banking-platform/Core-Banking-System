# Core Banking System

A backend-focused core banking system built with **Java and Spring Boot**, organized as multiple services for authentication, account management, transaction processing, shared security, and API gateway routing.

The project focuses on backend engineering concerns such as **authentication, role-based authorization, transaction processing, data consistency, concurrency control, and service separation**.

## Architecture

```text
                         Client
                           |
                           v
                    +--------------+
                    | API Gateway  |
                    +--------------+
                           |
             +-------------+-------------+
             |             |             |
             v             v             v
       +-----------+ +-----------+ +------------------+
       |   Auth    | |  Account  | |   Transaction    |
       |  Service  | |  Service  | |     Service      |
       +-----------+ +-----------+ +------------------+
             |             |             |
             v             v             v
       +-----------+ +-----------+ +------------------+
       | auth_db   | | account_db| |  transaction_db   |
       +-----------+ +-----------+ +------------------+

                    +----------------+
                    | Common-Security|
                    +----------------+
```

The repository currently contains:

* `Auth-Service`
* `Account-Service`
* `Transaction-services`
* `Common-Security`
* `Api-gate-way`
* `database-Schemas`
* `docker-compose.yaml`

## Services

### Auth Service

Responsible for authentication and session-related functionality.

The authentication layer uses:

* JWT access tokens
* Refresh tokens
* Refresh-token expiration
* Session-based refresh-token handling
* Role-based authorization
* Secure cookie-based token handling

### Account Service

Handles account and customer-profile functionality.

The service contains functionality related to:

* Bank account creation
* Account information
* Customer profiles
* KYC-related operations
* Account status
* Administrative account/profile operations
* Account balance handling

### Transaction Service

Handles financial transaction processing.

The transaction layer includes functionality for:

* Payment/transfer processing
* Transaction history
* Transaction status handling
* Failed, pending, and successful transaction flows
* Transaction pagination
* Transaction-related validation and exceptions

The transaction implementation was structured around separate lifecycle services to keep pending, successful, and failed transaction handling distinct.

### Common Security

Shared security functionality is maintained in a separate module so that security-related components can be reused across services.

This module contains the common authentication/authorization infrastructure used by the backend services.

### API Gateway

The API Gateway acts as the entry point for client requests and routes requests to the appropriate backend service.

It also participates in JWT-based authentication and route-level authorization.

## Key Engineering Areas

### Authentication & Authorization

The system uses JWT-based authentication with separate access and refresh token lifecycles.

Refresh-token handling includes token rotation and server-side session state.

Role-based access control is used to restrict administrative functionality.

### Transaction Consistency

Financial operations require careful handling of concurrent updates and failures.

The project uses Spring transaction management and optimistic locking for concurrent account updates.

The transaction service also separates different transaction states into dedicated lifecycle handling.

### Concurrency & Optimistic Locking
[📹 Watch demo](demo/concurrency-demo.mp4)

### Idempotent Transaction Processing

Transaction processing includes idempotency handling to prevent duplicate processing of repeated requests.

### Failure Handling

The services use validation and centralized exception-handling mechanisms to return controlled responses when operations fail.

### Service Separation

Authentication, account management, transaction processing, shared security, and gateway routing are separated into independent modules/services.

This keeps responsibilities isolated and allows each area to evolve independently.

## Technology Stack

| Category         | Technologies               |
| ---------------- | -------------------------- |
| Language         | Java                       |
| Backend          | Spring Boot                |
| Security         | Spring Security, JWT       |
| Persistence      | Spring Data JPA, Hibernate |
| Database         | PostgreSQL                 |
| API              | REST                       |
| Architecture     | Microservices              |
| Gateway          | Spring Cloud Gateway       |
| Containerization | Docker, Docker Compose     |
| Build            | Maven                      |
| Development      | IntelliJ IDEA, Postman     |

## Database Architecture

The Docker Compose configuration defines separate PostgreSQL databases for the major services:

```text
auth_db
account_db
transaction_db
```

Each service connects to its corresponding database.

This keeps authentication, account, and transaction data separated at the service level.

## Docker

The repository contains Dockerfiles for the backend services and a root `docker-compose.yaml` for running the system as a multi-container setup.

The Compose configuration includes:

* PostgreSQL containers
* Auth Service
* Account Service
* Transaction Service
* API Gateway
* PostgreSQL health checks
* Service startup dependencies

### Running with Docker Compose

Make sure Docker Desktop is running, then from the repository root:

```bash
docker compose up
```

The API Gateway is configured to expose port:

```text
8080
```

> The exact Docker image build/run workflow should match the current Dockerfiles and local environment. If images have not been built yet, build the required service images before starting the Compose stack.

## Running Individual Services

Each Spring Boot service contains its own Maven wrapper and can be run independently.

From a service directory:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

## Project Structure

```text
Core-Banking-System/
│
├── Account-Service/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
│
├── Auth-Service/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
│
├── Transaction-services/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
│
├── Common-Security/
│   ├── src/
│   └── pom.xml
│
├── Api-gate-way/
│   ├── src/
│   └── pom.xml
│
├── database-Schemas/
│
├── docker-compose.yaml
├── pom.xml
└── mvnw
```

## Engineering Decisions

### Why separate services?

Authentication, account management, and transaction processing have different responsibilities and data requirements. Separating them keeps the system modular and allows service-specific changes without putting all functionality into a single application.

### Why optimistic locking?

Account balances can be affected by concurrent operations. Optimistic locking allows the application to detect conflicting updates instead of silently overwriting changes.

### Why a dedicated transaction lifecycle?

Transaction processing can move through different states, including pending, successful, and failed outcomes. Separating lifecycle handling keeps state-specific behavior easier to reason about and maintain.

### Why shared security?

Security components that are common across services are maintained in `Common-Security` instead of duplicating the same authentication/authorization infrastructure in every service.

## Current Scope

This project is primarily a **backend engineering project** focused on implementing and exploring core banking workflows, service separation, authentication, authorization, transaction processing, and data consistency.

It is a learning and portfolio project and should **not be treated as production banking software**.

## What I Learned

The project was developed incrementally, with the architecture evolving as additional requirements were introduced.

The main areas explored through the implementation include:

* Designing Spring Boot microservices
* JWT authentication and refresh-token rotation
* Role-based access control
* Shared security components
* REST API design
* Transaction management
* Optimistic locking
* Idempotent transaction processing
* Transaction lifecycle design
* API Gateway routing
* PostgreSQL service-level data separation
* Docker-based local deployment

## Repository

[Core Banking System](https://github.com/digital-banking-platform/Core-Banking-System)
