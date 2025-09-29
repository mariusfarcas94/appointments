# MotionCare Appointments - Windows Development Setup

This folder contains everything you need to run the MotionCare Appointments backend on Windows using Docker.

## Prerequisites

Before you begin, make sure you have the following installed on your Windows machine:

1. **Docker Desktop for Windows**
   - Download from: https://www.docker.com/products/docker-desktop/
   - Make sure Docker Desktop is running before using the scripts

2. **Java 17** (if running the application locally)
   - Download from: https://adoptium.net/
   - Or use the Docker setup (recommended)

3. **Maven** (if running the application locally)
   - Download from: https://maven.apache.org/download.cgi

## Quick Start

### Option 1: Database Only (Recommended for Development)

1. **Start the database:**
   ```cmd
   start-database.bat
   ```

2. **Run the Spring Boot application locally:**
   ```cmd
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

### Option 2: Full Stack (Database + Application in Docker)

1. **Configure environment variables:**
   ```cmd
   copy env.example .env
   ```
   Edit the `.env` file with your configuration.

2. **Start everything:**
   ```cmd
   start-full-stack.bat
   ```

## Available Scripts

| Script | Description |
|--------|-------------|
| `start-database.bat` | Starts only MySQL database and phpMyAdmin |
| `start-full-stack.bat` | Starts database, Spring Boot app, and phpMyAdmin |
| `stop-services.bat` | Stops all running services |
| `reset-database.bat` | **WARNING**: Deletes all data and resets the database |
| `view-logs.bat` | Interactive script to view service logs |

## Services and Ports

| Service | URL | Port | Description |
|---------|-----|------|-------------|
| MySQL Database | localhost:3306 | 3306 | Main database |
| Spring Boot App | http://localhost:8081 | 8081 | REST API (full stack only) |
| phpMyAdmin | http://localhost:8080 | 8080 | Database management UI |

## Database Credentials

- **Database**: `motioncare_appointments`
- **Username**: `motioncare`
- **Password**: `motioncare123`
- **Root Password**: `root123`

## Configuration

### Environment Variables

Copy `env.example` to `.env` and configure the following:

```env
# Google Calendar Configuration
GOOGLE_CALENDAR_SERVICE_ACCOUNT_KEY_PATH=path/to/your/service-account-key.json
GOOGLE_CALENDAR_CALENDAR_ID=your-calendar-id@group.calendar.google.com

# JWT Configuration
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# CORS Configuration (add your frontend URLs)
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200
```

### Google Calendar Setup

1. **Create a Google Cloud Project:**
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project or select existing one

2. **Enable Google Calendar API:**
   - Navigate to "APIs & Services" > "Library"
   - Search for "Google Calendar API" and enable it

3. **Create Service Account:**
   - Go to "APIs & Services" > "Credentials"
   - Click "Create Credentials" > "Service Account"
   - Download the JSON key file

4. **Share Calendar:**
   - Open Google Calendar
   - Create a new calendar or use existing one
   - Share it with your service account email (found in the JSON file)
   - Give "Make changes to events" permission

5. **Configure Application:**
   - Place the JSON key file in a secure location
   - Update `GOOGLE_CALENDAR_SERVICE_ACCOUNT_KEY_PATH` in `.env`
   - Update `GOOGLE_CALENDAR_CALENDAR_ID` with your calendar ID

## Development Workflow

### Local Development (Recommended)

1. **Start database:**
   ```cmd
   start-database.bat
   ```

2. **Run application locally:**
   ```cmd
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

3. **Access services:**
   - Application: http://localhost:8080
   - phpMyAdmin: http://localhost:8080
   - Database: localhost:3306

### Docker Development

1. **Configure environment:**
   ```cmd
   copy env.example .env
   # Edit .env with your settings
   ```

2. **Start full stack:**
   ```cmd
   start-full-stack.bat
   ```

3. **Access services:**
   - Application: http://localhost:8081
   - phpMyAdmin: http://localhost:8080
   - Database: localhost:3306

## API Endpoints

Once the application is running, you can access:

- **Health Check**: http://localhost:8080/actuator/health (or 8081 for Docker)
- **API Documentation**: http://localhost:8080/swagger-ui.html (or 8081 for Docker)
- **Database Management**: http://localhost:8080 (phpMyAdmin)

## Troubleshooting

### Common Issues

1. **Docker not running:**
   ```
   ERROR: Docker is not running or not installed.
   ```
   - Start Docker Desktop
   - Wait for it to fully start (green icon in system tray)

2. **Port already in use:**
   ```
   Port 3306 is already in use
   ```
   - Stop other MySQL services
   - Or change ports in docker-compose.yml

3. **Database connection failed:**
   - Check if MySQL container is running: `docker-compose ps`
   - View database logs: `view-logs.bat` → option 1
   - Wait for database to fully start (can take 30-60 seconds)

4. **Application won't start:**
   - Check application logs: `view-logs.bat` → option 2
   - Verify database is healthy: `docker-compose ps`
   - Check environment variables in `.env`

### Useful Commands

```cmd
# Check running containers
docker-compose ps

# View logs for specific service
docker-compose logs mysql
docker-compose -f docker-compose.full.yml logs app

# Restart a service
docker-compose restart mysql

# Remove all containers and volumes (WARNING: deletes data)
docker-compose down -v
```

### Reset Everything

If you encounter persistent issues:

1. **Stop all services:**
   ```cmd
   stop-services.bat
   ```

2. **Reset database:**
   ```cmd
   reset-database.bat
   ```

3. **Start fresh:**
   ```cmd
   start-database.bat
   ```

## File Structure

```
windows-dev/
├── docker-compose.yml          # Database only setup
├── docker-compose.full.yml     # Full stack setup
├── Dockerfile                  # Application container
├── env.example                 # Environment variables template
├── database/
│   └── init/
│       └── 01-init.sql        # Database initialization
├── start-database.bat         # Start database only
├── start-full-stack.bat       # Start everything
├── stop-services.bat          # Stop all services
├── reset-database.bat         # Reset database (WARNING)
├── view-logs.bat              # View service logs
└── README.md                  # This file
```

## Security Notes

- Default passwords are for development only
- Change all passwords in production
- Keep your Google Calendar service account key secure
- Use strong JWT secrets in production
- Configure proper CORS origins for your frontend

## Support

If you encounter issues:

1. Check the logs using `view-logs.bat`
2. Verify Docker Desktop is running
3. Ensure ports 3306, 8080, and 8081 are available
4. Check the troubleshooting section above

For additional help, refer to the main project documentation or create an issue in the project repository.
