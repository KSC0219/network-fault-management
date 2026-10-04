# Network Fault Management API

A RESTful backend built with **Java 17, Spring Boot 3 and MySQL** for monitoring and managing network faults: creating them, tracking them, updating them and resolving them.

Built as the capstone project for the Wipro NGA program.

## Features

- CRUD REST endpoints for network faults (device, IP address, severity, assignee)
- A fault lifecycle with validated transitions: `OPEN → IN_PROGRESS → RESOLVED → CLOSED`, and a resolved fault can be re-opened
- Filtering by status, severity and device
- A summary endpoint with counts by status and active faults by severity
- Request validation (required fields, IPv4 format) and consistent JSON error responses
- A layered architecture: Controller → Service → Repository (Spring Data JPA)
- Integration tests with MockMvc and an in-memory H2 database
- A Postman collection with test scripts

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3 (Web, Data JPA, Validation) |
| Database | MySQL 8 (H2 for tests and local demo) |
| Build | Maven |
| Testing | JUnit 5, MockMvc, Postman |

## Project structure

```
src/main/java/com/saicharan/faultmanagement
├── controller/   FaultController: the REST endpoints
├── service/      FaultService + FaultServiceImpl: business rules and status transitions
├── repository/   FaultRepository: Spring Data JPA queries
├── model/        Fault entity, Severity and FaultStatus enums
├── dto/          Request and response records
└── exception/    Custom exceptions and the global error handler
```

## Running locally

**Prerequisites:** JDK 17+ and Maven 3.9+.

### Option A: with MySQL

```sql
CREATE DATABASE fault_db;
```

```bash
export DB_USERNAME=root
export DB_PASSWORD=your_password
mvn spring-boot:run
```

### Option B: no database setup (in-memory H2)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

The API runs at `http://localhost:8080`.

### Run the tests

```bash
mvn test
```

## API endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/faults` | Create a fault (status starts as `OPEN`) |
| GET | `/api/faults` | List faults. Optional filters: `?status=`, `?severity=`, `?device=` |
| GET | `/api/faults/{id}` | Get one fault |
| PUT | `/api/faults/{id}` | Update a fault's details |
| PATCH | `/api/faults/{id}/status` | Move a fault to a new status |
| DELETE | `/api/faults/{id}` | Delete a fault |
| GET | `/api/faults/summary` | Counts by status and by severity |

### Example: create a fault

```bash
curl -X POST http://localhost:8080/api/faults \
  -H "Content-Type: application/json" \
  -d '{
        "title": "Link down on uplink port",
        "deviceName": "router-blr-01",
        "ipAddress": "10.0.0.1",
        "severity": "HIGH",
        "assignedTo": "noc-team"
      }'
```

### Example: resolve a fault

```bash
curl -X PATCH http://localhost:8080/api/faults/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "RESOLVED", "resolutionNotes": "Replaced faulty SFP module"}'
```

### Error format

```json
{
  "timestamp": "2026-01-10T10:15:30",
  "status": 409,
  "error": "Conflict",
  "message": "Cannot change fault status from OPEN to CLOSED",
  "details": []
}
```

| Status code | When |
|---|---|
| 400 | Validation failed or the request is malformed |
| 404 | No fault exists with that id |
| 409 | The status transition is not allowed |

## Testing with Postman

Import `postman/Network-Fault-Management.postman_collection.json` into Postman and run the collection. The "Create fault" request saves the new id, and the later requests use it.
