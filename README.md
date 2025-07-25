# OPQ Library

A modern, container-ready Spring Boot application for managing your book library.

---

## Features
- **Modern UI**: Responsive Bootstrap 5 interface with dark mode, book management, and user authentication.
- **RESTful API**: Endpoints for all book operations (CRUD, search, progress tracking).
- **User Management**: Registration, login, roles (admin/user), password reset via token.
- **Security**: Spring Security with form login, role-based access, CSRF protection, and password hashing.
- **Testing**: JUnit 5, Mockito, AssertJ, Spring Boot Test, and mutation testing with PIT.
- **Code Quality & Security**: Jacoco for code coverage, OWASP Dependency Check for vulnerabilities.
- **Database**: In-memory H2 database for development/demo, with web console enabled.
- **Documentation**: Swagger/OpenAPI UI for API exploration.
- **Docker-ready**: Easily build and run in containers.

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker (optional, for containerization)

### Build & Run

#### Maven
```sh
mvn clean verify
java -jar target/opq-library-1.0.0.jar
```

#### Docker
```sh
docker build -t opqlibrary .
docker run -p 8080:8080 opqlibrary
```

---

## Configuration
- **Port**: Default is `8080` (see `src/main/resources/application.properties`)
- **Database**: H2 in-memory, web console at `/h2-console`
- **Swagger UI**: Available at `/swagger-ui.html`

---

## REST API
- `GET /api/books` — List all books
- `POST /api/books` — Add a book
- `DELETE /api/books/{index}` — Delete a book
- `POST /api/books/markRead/{index}` — Mark as read
- `POST /api/books/markProgress/{index}` — Mark as in progress
- `GET /api/books/search?title=...&author=...` — Search books
- `POST /api/users/register` — Register a new user
- `POST /api/password-reset/request` — Request password reset
- `POST /api/password-reset/confirm` — Confirm password reset

---

## UI Features
- **Book List**: View, search, filter, add, edit, and delete books.
- **Progress Tracking**: Mark books as read or in progress.
- **User Auth**: Register, login, logout, password reset.
- **Role-based Access**: Admin and user roles.
- **Responsive Design**: Works on desktop and mobile.

---

## Authentication & User Management
- **Registration**: Via `/register` page or API.
- **Login**: Form-based, with "Remember Me" option.
- **Password Reset**: Request and set new password via email/token flow.
- **Roles**: `ROLE_ADMIN` and `ROLE_USER` (default users seeded: `admin`/`admin123`, `user`/`user123`).

---

## Testing
- **Unit Tests**: JUnit 5, Mockito, AssertJ.
- **Web Layer Tests**: Spring Boot Test, MockMvc.
- **Mutation Testing**: PIT (run with `mvn org.pitest:pitest-maven:mutationCoverage`)
- **Coverage**: Jacoco (run with `mvn test` for report)
- **Security**: OWASP Dependency Check (`mvn org.owasp:dependency-check-maven:check`)

---

## Development Notes
- **Tech Stack**: Spring Boot, Spring Security, Spring Data JPA, Thymeleaf, H2, Swagger/OpenAPI, Bootstrap 5.
- **Code Quality**: Follows modern Java and Spring best practices.
- **Extensible**: Microservice-ready architecture.

---