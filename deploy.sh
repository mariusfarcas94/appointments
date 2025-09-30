#!/bin/bash

# Simple deployment script for MotionCare Appointments
set -e

SERVER_IP="161.35.201.78"
SERVER_USER="root"
APP_DIR="/opt/motioncare-appointments"

echo "🚀 Starting deployment..."

# 1. Copy docker-compose file to server
echo "📋 Copying docker-compose file..."
scp docker-compose.yml $SERVER_USER@$SERVER_IP:$APP_DIR/

# 2. Build the app
echo "🔨 Building application..."
./mvnw clean package -DskipTests

# 3. Copy jar to server
echo "📦 Copying JAR file..."
scp target/*.jar $SERVER_USER@$SERVER_IP:$APP_DIR/app.jar

# 4. (Re)start the app
echo "🔄 Restarting application..."
ssh $SERVER_USER@$SERVER_IP "cd $APP_DIR && docker-compose down || true && docker-compose up -d"

echo "✅ Deployment completed!"
echo "Application available at: http://$SERVER_IP:8080"
