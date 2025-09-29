@echo off
echo MotionCare Appointments - View Logs
echo.

REM Check if Docker is running
docker version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker is not running or not installed.
    pause
    exit /b 1
)

echo Select which logs to view:
echo 1. Database logs (MySQL)
echo 2. Application logs (Spring Boot)
echo 3. phpMyAdmin logs
echo 4. All logs
echo 5. Exit
echo.

set /p choice="Enter your choice (1-5): "

if "%choice%"=="1" (
    echo.
    echo Showing MySQL database logs...
    echo Press Ctrl+C to exit
    echo.
    docker-compose logs -f mysql
) else if "%choice%"=="2" (
    echo.
    echo Showing Spring Boot application logs...
    echo Press Ctrl+C to exit
    echo.
    docker-compose -f docker-compose.full.yml logs -f app
) else if "%choice%"=="3" (
    echo.
    echo Showing phpMyAdmin logs...
    echo Press Ctrl+C to exit
    echo.
    docker-compose logs -f phpmyadmin
) else if "%choice%"=="4" (
    echo.
    echo Showing all logs...
    echo Press Ctrl+C to exit
    echo.
    docker-compose logs -f
) else if "%choice%"=="5" (
    echo Goodbye!
    exit /b 0
) else (
    echo Invalid choice. Please try again.
    pause
    goto :eof
)

pause
