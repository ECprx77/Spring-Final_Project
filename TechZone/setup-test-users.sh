#!/bin/bash

# TechZone - Setup Test Users
# This script creates test users via the registration API

echo "🚀 TechZone - Setting up test users..."
echo ""

API_URL="http://localhost:8080/api"

# Check if server is running
if ! curl -s "$API_URL/products" > /dev/null 2>&1; then
    echo "❌ Error: Server is not running at $API_URL"
    echo "Start the server first with: ./mvnw spring-boot:run"
    exit 1
fi

echo "✅ Server is running"
echo ""

# Register users
echo "📝 Registering test users..."
echo ""

# Register 2 admin accounts (will need to be promoted to ADMIN role)
echo "1. Registering admin@techzone.com..."
curl -s -X POST "$API_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@techzone.com",
    "fullName": "Admin Principal",
    "password": "Admin123!"
  }' | jq -r '.message'

echo ""
echo "2. Registering admin2@techzone.com..."
curl -s -X POST "$API_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin2@techzone.com",
    "fullName": "Admin Secondaire",
    "password": "Admin123!"
  }' | jq -r '.message'

echo ""
echo "3. Registering user1@techzone.com..."
curl -s -X POST "$API_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user1@techzone.com",
    "fullName": "Jean Dupont",
    "password": "User123!"
  }' | jq -r '.message'

echo ""
echo "4. Registering user2@techzone.com..."
curl -s -X POST "$API_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user2@techzone.com",
    "fullName": "Marie Martin",
    "password": "User123!"
  }' | jq -r '.message'

echo ""
echo "5. Registering user3@techzone.com..."
curl -s -X POST "$API_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user3@techzone.com",
    "fullName": "Pierre Bernard",
    "password": "User123!"
  }' | jq -r '.message'

echo ""
echo "✅ All users registered!"
echo ""
echo "⚠️  IMPORTANT: To make admin accounts work, you need to promote them:"
echo ""
echo "1. Open H2 Console: http://localhost:8080/api/h2-console"
echo "   - JDBC URL: jdbc:h2:mem:techzonedb"
echo "   - User: sa"
echo "   - Password: mdp"
echo ""
echo "2. Run these SQL commands:"
echo "   UPDATE users SET role_id = 1 WHERE email = 'admin@techzone.com';"
echo "   UPDATE users SET role_id = 1 WHERE email = 'admin2@techzone.com';"
echo ""
echo "3. Verify:"
echo "   SELECT email, full_name, role_id FROM users;"
echo ""
echo "📋 Test Credentials:"
echo "   ADMIN: admin@techzone.com / Admin123!"
echo "   ADMIN: admin2@techzone.com / Admin123!"
echo "   USER:  user1@techzone.com / User123!"
echo "   USER:  user2@techzone.com / User123!"
echo "   USER:  user3@techzone.com / User123!"
echo ""
