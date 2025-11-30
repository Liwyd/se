# University Library Management System

A production-ready REST API for managing a university library system, built with Spring Boot 3.2.0.

## Features

### User Roles
- **Guest**: Browse books and view statistics
- **Student**: Register, search books, request borrows, view own requests
- **Employee**: Manage books, review borrow requests, manage students
- **Admin**: Manage employees, view comprehensive statistics

### Technical Features
- RESTful API with Spring Boot
- JWT-based authentication and authorization
- JPA/Hibernate for database operations
- H2 in-memory database (dev) / PostgreSQL (production)
- Swagger/OpenAPI documentation
- Global exception handling
- Input validation
- Spring Security
- Actuator for monitoring
- Comprehensive logging

## Tech Stack

- **Framework**: Spring Boot 3.2.0
- **Java**: 17
- **Database**: H2 (dev), PostgreSQL (production)
- **Security**: Spring Security + JWT
- **Documentation**: Swagger/OpenAPI 3
- **Build Tool**: Maven
- **Libraries**: Lombok, MapStruct

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL (for production)

## Getting Started

### Development Setup

1. Clone the repository:
```bash
git clone <repository-url>
cd library-management
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Default Admin Credentials

- **Username**: `admin`
- **Password**: `admin123`

⚠️ **Important**: Change the default admin password in production!

## API Documentation

Once the application is running, access the Swagger UI at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new student
- `POST /api/auth/login` - Login (Student/Employee/Admin)

### Books (Public)
- `GET /api/books/public` - Get all books
- `GET /api/books/public/search` - Search books
- `GET /api/books/public/available` - Get available books

### Books (Protected)
- `GET /api/books/{id}` - Get book by ID
- `POST /api/books` - Add book (Employee/Admin)
- `PUT /api/books/{id}` - Update book (Employee/Admin)
- `PATCH /api/books/{id}/availability` - Update availability (Employee/Admin)

### Borrows
- `POST /api/borrows` - Create borrow request (Student)
- `GET /api/borrows/my-requests` - Get my requests (Student)
- `GET /api/borrows/pending` - Get pending requests (Employee/Admin)
- `POST /api/borrows/{id}/approve` - Approve request (Employee/Admin)
- `POST /api/borrows/{id}/reject` - Reject request (Employee/Admin)
- `POST /api/borrows/{id}/borrow` - Mark as borrowed (Employee/Admin)
- `POST /api/borrows/{id}/return` - Return book (Employee/Admin)

### Students
- `GET /api/students/public/count` - Get student count (Public)
- `GET /api/students` - Get all students (Employee/Admin)
- `GET /api/students/{id}` - Get student by ID (Employee/Admin)
- `GET /api/students/{id}/report` - Get student report (Employee/Admin)
- `PATCH /api/students/{id}/toggle-status` - Toggle status (Employee/Admin)

### Statistics
- `GET /api/public/statistics` - Get library statistics (Public)

### Admin
- `POST /api/admin/employees` - Add employee (Admin)
- `GET /api/admin/students` - Get all students (Admin)

## Authentication

All protected endpoints require JWT authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## Configuration

### Application Properties

The application uses `application.yml` for configuration. Key settings:

- **Server Port**: 8080 (configurable)
- **Database**: H2 in-memory (dev) or PostgreSQL (production)
- **JWT Secret**: Configure in `application.yml` or via `JWT_SECRET` environment variable
- **JWT Expiration**: 24 hours (configurable)

### Production Configuration

For production, use `application-prod.yml`:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

Set environment variables:
- `DATABASE_URL`: PostgreSQL connection URL
- `DATABASE_USERNAME`: Database username
- `DATABASE_PASSWORD`: Database password
- `JWT_SECRET`: Secure random string (256-bit recommended)

## Database

### Development (H2)
- Access H2 Console: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:librarydb`
- Username: `sa`
- Password: (empty)

### Production (PostgreSQL)
Add PostgreSQL dependency to `pom.xml` and configure connection in `application-prod.yml`.

## Testing

Run all tests:
```bash
mvn test
```

Run with coverage:
```bash
mvn test jacoco:report
```

## Building for Production

1. Build JAR:
```bash
mvn clean package -DskipTests
```

2. Run JAR:
```bash
java -jar target/library-management-1.0.0.jar --spring.profiles.active=prod
```

## Monitoring

Spring Boot Actuator endpoints:
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics
- Info: http://localhost:8080/actuator/info

## Project Structure

```
src/
├── main/
│   ├── java/com/university/library/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/       # REST controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # JPA entities
│   │   ├── exception/       # Exception handling
│   │   ├── repository/      # JPA repositories
│   │   ├── security/        # Security configuration
│   │   └── service/         # Business logic
│   └── resources/
│       ├── application.yml      # Main configuration
│       └── application-prod.yml # Production configuration
└── test/                    # Unit and integration tests
```

## Security

- JWT-based authentication
- Role-based access control (RBAC)
- Password encryption with BCrypt
- CORS configuration
- Input validation
- SQL injection protection (JPA)

## Logging

Logs are written to:
- Console (development)
- `logs/library-management.log` (file)

Log levels are configurable in `application.yml`.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

Apache License 2.0

## Support

For issues and questions, please open an issue on GitHub.
