@echo off
echo Starting MotionCare Appointments Full Stack...
echo.

REM Check if Docker is running
docker version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker is not running or not installed.
    echo Please start Docker Desktop and try again.
    pause
    exit /b 1
)

REM Check if .env file exists
if not exist .env (
    echo WARNING: .env file not found.
    echo Copying env.example to .env...
    copy env.example .env
    echo.
    echo Please edit .env file with your configuration before continuing.
    echo Press any key to continue after editing .env...
    pause
)

REM Start the full stack services
echo Starting MySQL database, Spring Boot application, and phpMyAdmin...
docker-compose -f docker-compose.full.yml up -d

REM Wait for services to start
echo.
echo Waiting for services to start (this may take a few minutes)...
timeout /t 10 /nobreak >nul

REM Check if services are running
echo.
echo Checking service status...
docker-compose -f docker-compose.full.yml ps

echo.
echo ========================================
echo Full Stack Setup Complete!
echo ========================================
echo.
echo Services:
echo - MySQL Database: localhost:3306
echo - Spring Boot App: http://localhost:8081
echo - phpMyAdmin: http://localhost:8080
echo.
echo Database Credentials:
echo - Database: motioncare_appointments
echo - Username: motioncare
echo - Password: motioncare123
echo.
echo Application Health Check: http://localhost:8081/actuator/health
echo.
pause
