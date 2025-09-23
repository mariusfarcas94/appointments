# User Module Structure

The user management functionality has been organized into a separate module within the project for easy extraction and reuse.

## Module Structure

```
src/main/java/com/motioncare/appointments/user/
├── controller/
│   ├── AuthController.java          # Authentication endpoints (/api/auth)
│   └── UserController.java          # User management endpoints (/api/users)
├── service/
│   └── UserService.java             # Business logic for user operations
├── repository/
│   └── UserRepository.java          # MyBatis repository interface
├── model/
│   ├── User.java                    # User entity
│   └── Role.java                    # Role entity
├── dto/
│   ├── UserRegistrationRequest.java # Registration request DTO
│   ├── UserLoginRequest.java        # Login request DTO
│   ├── UserResponse.java            # User response DTO
│   ├── AuthResponse.java            # Authentication response DTO
│   └── UserUpdateRequest.java       # User update request DTO
├── security/
│   ├── JwtUtil.java                 # JWT utility class
│   ├── JwtAuthenticationFilter.java # JWT authentication filter
│   └── CustomUserDetailsService.java # User details service
└── config/
    └── UserSecurityConfig.java      # Security configuration

src/main/resources/user/
└── mapper/
    └── UserMapper.xml               # MyBatis mapper XML
```

## Key Benefits of This Structure

1. **Modularity**: All user-related code is contained within the `user` package
2. **Easy Extraction**: The entire module can be easily moved to a separate project
3. **Clear Separation**: User functionality is isolated from the main application logic
4. **Self-Contained**: The module includes all necessary components (controllers, services, repositories, security, etc.)

## Module Dependencies

The user module depends on:
- Spring Boot Starter Web
- Spring Security
- MyBatis Spring Boot Starter
- H2 Database
- JWT libraries
- Lombok

## Configuration

The module is configured through:
- `application.properties`: Database and MyBatis configuration
- `UserSecurityConfig.java`: Security configuration specific to the user module
- `UserMapper.xml`: Database mapping configuration

## API Endpoints

### Authentication (`/api/auth`)
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### User Management (`/api/users`)
- `GET /api/users` - Get all users (Admin only)
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user (Admin only)

## Extraction Guide

To extract this module to a separate project:

1. Copy the entire `src/main/java/com/motioncare/appointments/user/` directory
2. Copy the `src/main/resources/user/` directory
3. Copy relevant dependencies from `pom.xml`
4. Copy relevant configuration from `application.properties`
5. Update package names to match the new project structure
6. Create a new main application class

## Integration with Main Application

The user module integrates seamlessly with the main application:
- Uses the same database configuration
- Shares the same security context
- Maintains the same API structure
- Can be accessed through the same base URL

This structure ensures that the user management functionality is completely modular while maintaining full integration with the existing application.
