# MotionCare Appointments - Start Database (PowerShell)
Write-Host "Starting MotionCare Appointments Database..." -ForegroundColor Green
Write-Host ""

# Check if Docker is running
try {
    docker version | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw "Docker not running"
    }
} catch {
    Write-Host "ERROR: Docker is not running or not installed." -ForegroundColor Red
    Write-Host "Please start Docker Desktop and try again." -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

# Start the database services
Write-Host "Starting MySQL database and phpMyAdmin..." -ForegroundColor Yellow
docker-compose up -d

# Wait a moment for services to start
Start-Sleep -Seconds 5

# Check if services are running
Write-Host ""
Write-Host "Checking service status..." -ForegroundColor Yellow
docker-compose ps

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Database Setup Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Services:" -ForegroundColor Cyan
Write-Host "- MySQL Database: localhost:3306" -ForegroundColor White
Write-Host "- phpMyAdmin: http://localhost:8080" -ForegroundColor White
Write-Host ""
Write-Host "Database Credentials:" -ForegroundColor Cyan
Write-Host "- Database: motioncare_appointments" -ForegroundColor White
Write-Host "- Username: motioncare" -ForegroundColor White
Write-Host "- Password: motioncare123" -ForegroundColor White
Write-Host ""
Write-Host "You can now run your Spring Boot application locally." -ForegroundColor Yellow
Write-Host "Use: mvn spring-boot:run -Dspring-boot.run.profiles=mysql" -ForegroundColor Yellow
Write-Host ""
Read-Host "Press Enter to exit"
