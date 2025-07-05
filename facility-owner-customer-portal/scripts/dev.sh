#!/bin/bash

# Development script for Facility Owner Customer Portal
# Sets up development environment and starts the application

set -e

echo "🚀 Starting Facility Owner Customer Portal Development Environment"
echo "=================================================="

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js 18+ to continue."
    exit 1
fi

# Check Node.js version
NODE_VERSION=$(node --version | cut -d'v' -f2 | cut -d'.' -f1)
if [ "$NODE_VERSION" -lt 18 ]; then
    echo "❌ Node.js version 18+ is required. Current version: $(node --version)"
    exit 1
fi

echo "✅ Node.js version: $(node --version)"

# Check if npm is installed
if ! command -v npm &> /dev/null; then
    echo "❌ npm is not installed. Please install npm to continue."
    exit 1
fi

echo "✅ npm version: $(npm --version)"

# Install dependencies if node_modules doesn't exist
if [ ! -d "node_modules" ]; then
    echo "📦 Installing dependencies..."
    npm install
else
    echo "✅ Dependencies already installed"
fi

# Create .env.local if it doesn't exist
if [ ! -f ".env.local" ]; then
    echo "📄 Creating .env.local file..."
    cat > .env.local << EOF
# Development environment variables
NODE_ENV=development
VUE_APP_API_BASE_URL=http://localhost:8206
VUE_APP_AUTH_URL=http://localhost:8080
VUE_APP_MARKETPLACE_URL=http://localhost:8230
VUE_APP_BILLING_URL=http://localhost:8200
VUE_APP_ANALYTICS_URL=http://localhost:8205
VUE_APP_NOTIFICATION_URL=http://localhost:8090

# Feature flags
VUE_APP_ENABLE_ANALYTICS=true
VUE_APP_ENABLE_REAL_TIME=true
VUE_APP_DEBUG_MODE=true
EOF
    echo "✅ Created .env.local with default development settings"
else
    echo "✅ .env.local already exists"
fi

# Check if backend services are running
echo "🔍 Checking backend service availability..."

check_service() {
    local service_name=$1
    local service_url=$2
    
    if curl -f -s "$service_url/health" > /dev/null 2>&1; then
        echo "✅ $service_name is running at $service_url"
        return 0
    else
        echo "⚠️  $service_name is not available at $service_url"
        return 1
    fi
}

# Check critical backend services
SERVICES_OK=true

if ! check_service "Warehouse Management Service" "http://localhost:8206"; then
    SERVICES_OK=false
fi

if ! check_service "Auth Service" "http://localhost:8080"; then
    SERVICES_OK=false
fi

if ! check_service "Billing Service" "http://localhost:8200"; then
    SERVICES_OK=false
fi

if [ "$SERVICES_OK" = false ]; then
    echo ""
    echo "⚠️  Some backend services are not running."
    echo "   The application will start but some features may not work."
    echo "   Please ensure the following services are running:"
    echo "   - warehouse-management-service (port 8206)"
    echo "   - auth-service (port 8080)"
    echo "   - billing-service (port 8200)"
    echo ""
    read -p "Continue anyway? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Run type checking
echo "🔍 Running type check..."
npm run type-check

# Run linting
echo "🧹 Running linter..."
npm run lint

echo ""
echo "🎉 Development environment is ready!"
echo "📱 Starting development server on http://localhost:3202"
echo "🔄 Hot reload is enabled - changes will be reflected automatically"
echo ""
echo "Available commands:"
echo "  npm run dev          - Start development server"
echo "  npm run build        - Build for production"
echo "  npm run test:unit    - Run unit tests"
echo "  npm run lint         - Run linter"
echo "  npm run type-check   - Run TypeScript type checking"
echo ""

# Start the development server
npm run dev