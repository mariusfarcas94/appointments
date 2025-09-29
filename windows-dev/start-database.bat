@echo off
echo Starting MotionCare Appointments Database...
echo.

REM Check if Docker is running
docker version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker is not running or not installed.
    echo Please start Docker Desktop and try again.
    pause
    exit /b 1
)

REM Start the database services
echo Starting MySQL database and phpMyAdmin...
docker-compose up -d

REM Wait a moment for services to start
timeout /t 5 /nobreak >nul

REM Check if services are running
echo.
echo Checking service status...
docker-compose ps

echo.
echo ========================================
echo Database Setup Complete!
echo ========================================
echo.
echo Services:
echo - MySQL Database: localhost:3306
echo - phpMyAdmin: http://localhost:8080
echo.
echo Database Credentials:
echo - Database: motioncare_appointments
echo - Username: motioncare
echo - Password: motioncare123
echo.
echo You can now run your Spring Boot application locally.
echo Use: mvn spring-boot:run -Dspring-boot.run.profiles=mysql
echo.
pause
