#!/bin/bash

# Update Git Configuration Script
# This script sets the global git username and email

echo "Updating Git configuration..."

# Set git username
git config --global user.name "mariusfarcas94"
echo "Git username set to: mariusfarcas94"

# Set git email
git config --global user.email "marius.farcas94@gmail.com"
echo "Git email set to: marius.farcas94@gmail.com"

# Display current configuration
echo ""
echo "Current Git configuration:"
git config --global --list | grep user

echo ""
echo "Git configuration updated successfully!"
