# Database Configuration

This application uses **MySQL** as the primary database.

## MySQL Setup

1. **Install MySQL** (if not already installed)
2. **Create the database:**
   ```sql
   CREATE DATABASE motioncare_appointments;
   ```

3. **Create a user:**
   ```sql
   CREATE USER 'motioncare'@'localhost' IDENTIFIED BY 'motioncare123';
   GRANT ALL PRIVILEGES ON motioncare_appointments.* TO 'motioncare'@'localhost';
   FLUSH PRIVILEGES;
   ```

4. **Start the application:**
   ```bash
   mvn spring-boot:run
   ```

The application will automatically create the necessary tables using Liquibase migrations.

## Database Configuration

- **URL:** `jdbc:mysql://localhost:3306/motioncare_appointments`
- **Username:** `motioncare`
- **Password:** `motioncare123`
- **Driver:** `com.mysql.cj.jdbc.Driver`

## Testing

For tests, the application automatically uses H2 in-memory database configured in `application-test.properties`.

## Configuration Files

- `application.properties` - MySQL configuration
- `application-test.properties` - Test-specific H2 configuration

## Liquibase Migrations

Database schema is managed by Liquibase migrations located in:
- `src/main/resources/db/changelog/db.changelog-master.xml`
- `src/main/resources/db/changelog/001-initial-schema.sql`

The migrations will run automatically on application startup.

## Troubleshooting

### MySQL Connection Issues
1. Ensure MySQL is running
2. Check if the database and user exist
3. Verify connection parameters in `application.properties`
4. Check firewall settings