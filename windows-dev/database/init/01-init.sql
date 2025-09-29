-- MySQL Database Setup Script for MotionCare Appointments
-- This script runs automatically when the MySQL container starts for the first time

-- Create the database (if not exists)
CREATE DATABASE IF NOT EXISTS motioncare_appointments 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- Create the user (if not exists)
CREATE USER IF NOT EXISTS 'motioncare'@'%' IDENTIFIED BY 'motioncare123';

-- Grant privileges
GRANT ALL PRIVILEGES ON motioncare_appointments.* TO 'motioncare'@'%';

-- Apply changes
FLUSH PRIVILEGES;

-- Use the database
USE motioncare_appointments;

-- Show confirmation
SELECT 'Database and user created successfully!' as status;
SHOW DATABASES LIKE 'motioncare_appointments';
SELECT User, Host FROM mysql.user WHERE User = 'motioncare';
