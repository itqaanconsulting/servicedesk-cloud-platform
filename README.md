# ServiceDesk Cloud Platform

[![Build](https://github.com/itqaanconsulting/servicedesk-cloud-platform/actions/workflows/build.yml/badge.svg)](https://github.com/itqaanconsulting/servicedesk-cloud-platform/actions/workflows/build.yml)

Cloud-native service desk showcase built as independently deployable Java microservices. The project focuses on service boundaries, synchronous communication, resilience, observability and Kubernetes deployment.

## Services

| Service | Port | Responsibility |
| --- | ---: | --- |
| Ticket Service | 8181 | Ticket lifecycle, priority and assignment |
| Technician Service | 8082 | Technician skills, teams and availability |
| Notification Service | 8083 | Notification delivery and audit history |

## Architecture

```mermaid
flowchart LR
    Client["Web or API client"]
    Ticket["Ticket Service"]
    Technician["Technician Service"]
    Notification["Notification Service"]

    Client --> Ticket
    Ticket --> Technician
    Ticket --> Notification
```

Each service owns its domain and database. The Ticket Service currently persists tickets in PostgreSQL through versioned Flyway migrations. Calls between services remain explicit REST contracts.

## Technology

- Java 21
- Spring Boot 3.5
- Maven multi-module build
- Spring Boot Actuator and Prometheus metrics
- Docker Compose
- GitHub Actions

Planned platform capabilities include PostgreSQL, Flyway, Resilience4j, OpenTelemetry, Prometheus, Grafana, Kubernetes and Terraform.

## Build

```powershell
mvn clean verify
```

## Run a Service

```powershell
mvn -pl services/ticket-service spring-boot:run
```

Available endpoints:

- `http://localhost:8181/api`
- `POST http://localhost:8181/api/tickets`
- `GET http://localhost:8181/api/tickets`
- `GET http://localhost:8181/api/tickets/{ticketId}`
- `PATCH http://localhost:8181/api/tickets/{ticketId}/status`
- `http://localhost:8181/actuator/health`
- `http://localhost:8181/actuator/prometheus`

The technician and notification services expose the same endpoints on ports `8082` and `8083`.

Create a ticket:

```powershell
$body = @{
    title = "VPN access unavailable"
    description = "Remote employee cannot connect to the corporate VPN."
    requesterEmail = "alex@example.com"
    priority = "HIGH"
} | ConvertTo-Json

Invoke-RestMethod `
    -Method Post `
    -Uri http://localhost:8181/api/tickets `
    -ContentType application/json `
    -Body $body
```

## Run with Docker

Build the application jars first:

```powershell
mvn clean package
docker compose up --build
```

## Delivery Roadmap

1. Implement technician persistence with a separate PostgreSQL database.
2. Add synchronous ticket assignment with timeout, retry and circuit breaker behavior.
3. Add distributed tracing and a local observability dashboard.
4. Package all services for Kubernetes with health probes and resource limits.
5. Provision a cloud environment using Terraform.

## Project Structure

```text
services/
  ticket-service/
  technician-service/
  notification-service/
compose.yml
pom.xml
```
