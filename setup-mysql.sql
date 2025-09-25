-- MySQL Database Setup Script for MotionCare Appointments
-- Run this script as MySQL root user

-- Create the database
CREATE DATABASE IF NOT EXISTS motioncare_appointments 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- Create the user
CREATE USER IF NOT EXISTS 'motioncare'@'localhost' IDENTIFIED BY 'motioncare123';

-- Grant privileges
GRANT ALL PRIVILEGES ON motioncare_appointments.* TO 'motioncare'@'localhost';

-- Apply changes
FLUSH PRIVILEGES;

-- Show confirmation
SELECT 'Database and user created successfully!' as status;
SHOW DATABASES LIKE 'motioncare_appointments';
SELECT User, Host FROM mysql.user WHERE User = 'motioncare';
