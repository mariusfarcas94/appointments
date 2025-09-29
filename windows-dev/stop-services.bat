@echo off
echo Stopping MotionCare Appointments Services...
echo.

REM Check if Docker is running
docker version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker is not running or not installed.
    pause
    exit /b 1
)

REM Stop database services
echo Stopping database services...
docker-compose down

REM Stop full stack services
echo Stopping full stack services...
docker-compose -f docker-compose.full.yml down

echo.
echo All services have been stopped.
echo.
pause
