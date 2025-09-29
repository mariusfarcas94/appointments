@echo off
echo WARNING: This will delete all data in the database!
echo.
set /p confirm="Are you sure you want to reset the database? (y/N): "
if /i not "%confirm%"=="y" (
    echo Operation cancelled.
    pause
    exit /b 0
)

echo.
echo Stopping services and removing database volumes...
echo.

REM Check if Docker is running
docker version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker is not running or not installed.
    pause
    exit /b 1
)

REM Stop services and remove volumes
docker-compose down -v
docker-compose -f docker-compose.full.yml down -v

REM Remove any orphaned containers
docker-compose down --remove-orphans
docker-compose -f docker-compose.full.yml down --remove-orphans

echo.
echo Database has been reset. All data has been deleted.
echo You can now start the services again using start-database.bat or start-full-stack.bat
echo.
pause
