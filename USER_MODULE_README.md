# User Management Module

This module provides comprehensive user management functionality including registration, authentication, authorization, and user CRUD operations.

## Features

- **User Registration**: Create new user accounts with validation
- **User Authentication**: JWT-based login system
- **Password Security**: BCrypt encryption for secure password storage
- **Role-based Authorization**: Support for USER and ADMIN roles
- **User Management**: CRUD operations for user accounts
- **Database Integration**: MyBatis for database operations with H2 in-memory database

## Architecture

The module follows the standard Spring Boot layered architecture:

- **Controller Layer**: REST endpoints for user operations
- **Service Layer**: Business logic and transaction management
- **Repository Layer**: Data access using MyBatis
- **Model Layer**: Entity classes and DTOs

## API Endpoints

### Authentication Endpoints (`/api/auth`)
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - User login

### User Management Endpoints (`/api/users`)
- `GET /api/users` - Get all users (Admin only)
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user (Admin only)

## Security

- **JWT Authentication**: Stateless authentication using JSON Web Tokens
- **Password Encryption**: BCrypt with salt for secure password storage
- **Role-based Access Control**: Different access levels for USER and ADMIN roles
- **Input Validation**: Comprehensive validation using Bean Validation annotations

## Database Schema

The module uses the following tables:
- `users`: User account information
- `roles`: Available roles (USER, ADMIN)
- `user_roles`: Many-to-many relationship between users and roles

## Configuration

Key configuration properties in `application.properties`:
- Database connection settings
- JWT secret and expiration
- MyBatis mapper locations
- Security settings

## Default Data

The system creates default data on startup:
- Default roles: ROLE_USER, ROLE_ADMIN
- Default admin user: username: `admin`, password: `admin123`

## Testing

You can test the API using:
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- H2 Console: `http://localhost:8080/h2-console`

## Dependencies Added

- MyBatis Spring Boot Starter
- Spring Security
- H2 Database
- JWT (jjwt)
- Lombok (already present)

## Usage Example

1. Register a new user:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

2. Login:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

3. Use the returned JWT token for authenticated requests:
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer <your-jwt-token>"
```
