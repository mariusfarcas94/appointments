# Docker Setup for MotionCare Appointments

This project includes Docker configuration for easy database setup and full application deployment.

## Quick Start

### Option 1: Database Only (Recommended for Development)

Start just the MySQL database:

```bash
docker-compose up -d mysql
```

Then run your Spring Boot application locally with MySQL:

```bash
# Using Maven
mvn spring-boot:run -Dspring-boot.run.profiles=mysql

# Or using JAR
java -jar target/*.jar --spring.profiles.active=mysql
```

### Option 2: Full Stack (Database + Application)

Start everything (database + your application):

```bash
docker-compose -f docker-compose.full.yml up -d
```

## Services

### MySQL Database
- **Port**: 3306
- **Database**: motioncare_appointments
- **Username**: motioncare
- **Password**: motioncare123
- **Health Check**: Included

### phpMyAdmin (Database Management)
- **URL**: http://localhost:8080
- **Username**: motioncare
- **Password**: motioncare123

### Spring Boot Application (when using full stack)
- **URL**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health

## Database Connection Details

When connecting to the MySQL database:

```
Host: localhost
Port: 3306
Database: motioncare_appointments
Username: motioncare
Password: motioncare123
```

## Useful Commands

### Start Services
```bash
# Database only
docker-compose up -d mysql

# Full stack
docker-compose -f docker-compose.full.yml up -d
```

### Stop Services
```bash
# Database only
docker-compose down

# Full stack
docker-compose -f docker-compose.full.yml down
```

### View Logs
```bash
# Database logs
docker-compose logs mysql

# Application logs
docker-compose -f docker-compose.full.yml logs app

# All logs
docker-compose logs
```

### Reset Database
```bash
# Stop and remove volumes (WARNING: This deletes all data!)
docker-compose down -v
docker-compose up -d mysql
```

## Development Workflow

1. **Start Database**: `docker-compose up -d mysql`
2. **Run Application**: `mvn spring-boot:run -Dspring-boot.run.profiles=mysql`
3. **Access phpMyAdmin**: http://localhost:8080
4. **Access Application**: http://localhost:8080

## Production Considerations

For production deployment:

1. Change default passwords
2. Use environment variables for sensitive data
3. Configure proper networking
4. Set up SSL/TLS
5. Configure backup strategies

## Troubleshooting

### Database Connection Issues
- Ensure MySQL container is running: `docker-compose ps`
- Check database logs: `docker-compose logs mysql`
- Verify port 3306 is not used by another service

### Application Issues
- Check application logs: `docker-compose logs app`
- Verify database is healthy: `docker-compose ps`
- Ensure Liquibase migrations completed successfully
