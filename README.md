# Employee Management REST API

A full-fledged Spring Boot 3 REST API with MySQL, JPA, validation, pagination, search, Swagger UI, and Docker support.

---

## Tech Stack

| Layer        | Technology                        |
|-------------|-----------------------------------|
| Framework    | Spring Boot 3.2.0                |
| Language     | Java 17                          |
| Database     | MySQL 8.0                        |
| ORM          | Spring Data JPA / Hibernate      |
| Validation   | Jakarta Bean Validation          |
| API Docs     | Springdoc OpenAPI 2 (Swagger UI) |
| Build Tool   | Maven                            |
| Mapping      | ModelMapper                      |
| Boilerplate  | Lombok                           |
| Container    | Docker + Docker Compose          |

---

## Project Structure

```
src/main/java/com/example/employeeapi/
├── EmployeeApiApplication.java       # Main entry point
├── controller/
│   └── EmployeeController.java       # REST endpoints
├── service/
│   └── EmployeeService.java          # Service interface
├── serviceimpl/
│   └── EmployeeServiceImpl.java      # Business logic
├── repository/
│   └── EmployeeRepository.java       # JPA repository + custom queries
├── entity/
│   └── Employee.java                 # JPA entity
├── dto/
│   ├── EmployeeRequestDto.java       # Input DTO (with validation)
│   ├── EmployeeResponseDto.java      # Output DTO
│   ├── ApiResponse.java              # Generic response wrapper
│   └── PagedResponse.java            # Pagination wrapper
├── exception/
│   ├── ResourceNotFoundException.java
│   ├── DuplicateResourceException.java
│   └── GlobalExceptionHandler.java   # Centralized error handling
└── config/
    └── AppConfig.java                # ModelMapper + Swagger beans
```

---

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+

---

## MySQL Setup

```sql
CREATE DATABASE employee_db;
CREATE USER 'appuser'@'localhost' IDENTIFIED BY 'apppassword';
GRANT ALL PRIVILEGES ON employee_db.* TO 'appuser'@'localhost';
FLUSH PRIVILEGES;
```

---

## Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/employee_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=appuser
spring.datasource.password=apppassword
```

---

## Run Locally

```bash
# Clone and build
mvn clean install

# Run
mvn spring-boot:run
```

App starts at: http://localhost:8080

---

## Run with Docker Compose

```bash
docker-compose up --build
```

This starts both MySQL and the API together automatically.

---

## API Endpoints

| Method | Endpoint                               | Description                   |
|--------|----------------------------------------|-------------------------------|
| POST   | /api/v1/employees                      | Create employee               |
| GET    | /api/v1/employees                      | Get all (paginated + sorted)  |
| GET    | /api/v1/employees/{id}                 | Get by ID                     |
| GET    | /api/v1/employees/email/{email}        | Get by email                  |
| GET    | /api/v1/employees/department/{dept}    | Get by department             |
| GET    | /api/v1/employees/status/{status}      | Get by status                 |
| GET    | /api/v1/employees/search?keyword=      | Full-text search              |
| GET    | /api/v1/employees/departments          | List all departments          |
| GET    | /api/v1/employees/department/{d}/count | Count by department           |
| PUT    | /api/v1/employees/{id}                 | Full update                   |
| PATCH  | /api/v1/employees/{id}/status          | Update status only            |
| DELETE | /api/v1/employees/{id}                 | Delete employee               |

---

## Swagger UI

Open in browser after starting the app:

```
http://localhost:8080/swagger-ui.html
```

---

## Sample Request Body (POST /api/v1/employees)

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@company.com",
  "phone": "+919876543210",
  "department": "Engineering",
  "designation": "Software Engineer",
  "salary": 85000.00,
  "dateOfJoining": "2024-01-15",
  "status": "ACTIVE"
}
```

---

## Pagination & Sorting

```
GET /api/v1/employees?page=0&size=10&sortBy=firstName&sortDir=asc
```

---

## Run Tests

```bash
mvn test
```

Tests use H2 in-memory database — no MySQL needed for tests.

---

## Employee Status Values

- `ACTIVE`
- `INACTIVE`
- `ON_LEAVE`
