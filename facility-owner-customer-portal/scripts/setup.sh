#!/bin/bash

# Setup script for Facility Owner Customer Portal
# Prepares the development environment and installs all dependencies

set -e

echo "⚡ Facility Owner Customer Portal Setup"
echo "======================================"

# Function to check command availability
check_command() {
    if ! command -v "$1" &> /dev/null; then
        echo "❌ $1 is not installed. Please install $1 to continue."
        return 1
    fi
    return 0
}

# Function to check minimum version
check_version() {
    local cmd=$1
    local version=$2
    local min_version=$3
    
    if [ "$version" -lt "$min_version" ]; then
        echo "❌ $cmd version $min_version+ is required. Current version: $version"
        return 1
    fi
    return 0
}

echo "🔍 Checking system requirements..."

# Check Node.js
if check_command "node"; then
    NODE_VERSION=$(node --version | cut -d'v' -f2 | cut -d'.' -f1)
    if check_version "Node.js" "$NODE_VERSION" "18"; then
        echo "✅ Node.js $(node --version)"
    else
        exit 1
    fi
else
    echo "📥 Please install Node.js 18+ from https://nodejs.org/"
    exit 1
fi

# Check npm
if check_command "npm"; then
    echo "✅ npm $(npm --version)"
else
    echo "❌ npm is not available. Please install npm."
    exit 1
fi

# Check Git
if check_command "git"; then
    echo "✅ Git $(git --version | cut -d' ' -f3)"
else
    echo "⚠️  Git is not installed. Some features may not work properly."
fi

# Check Docker (optional)
if check_command "docker"; then
    echo "✅ Docker $(docker --version | cut -d' ' -f3 | cut -d',' -f1)"
else
    echo "⚠️  Docker is not installed. Container operations will not be available."
fi

echo ""
echo "📦 Installing dependencies..."

# Clean install
if [ -d "node_modules" ]; then
    echo "🧹 Cleaning existing node_modules..."
    rm -rf node_modules package-lock.json
fi

npm install

echo "✅ Dependencies installed successfully"

# Create environment files
echo ""
echo "⚙️  Setting up environment configuration..."

# Development environment
if [ ! -f ".env.local" ]; then
    cat > .env.local << EOF
# Development Environment Configuration
NODE_ENV=development

# Backend Service URLs
VUE_APP_API_BASE_URL=http://localhost:8206
VUE_APP_AUTH_URL=http://localhost:8080
VUE_APP_MARKETPLACE_URL=http://localhost:8230
VUE_APP_BILLING_URL=http://localhost:8200
VUE_APP_ANALYTICS_URL=http://localhost:8205
VUE_APP_NOTIFICATION_URL=http://localhost:8090

# Feature Flags
VUE_APP_ENABLE_ANALYTICS=true
VUE_APP_ENABLE_REAL_TIME=true
VUE_APP_ENABLE_NOTIFICATIONS=true
VUE_APP_DEBUG_MODE=true

# UI Configuration
VUE_APP_THEME=light
VUE_APP_LOCALE=en
VUE_APP_PAGINATION_SIZE=20

# Development Tools
VUE_APP_SHOW_DEVTOOLS=true
VUE_APP_LOG_LEVEL=debug
EOF
    echo "✅ Created .env.local"
else
    echo "⚠️  .env.local already exists"
fi

# Production environment template
if [ ! -f ".env.production" ]; then
    cat > .env.production << EOF
# Production Environment Configuration
NODE_ENV=production

# Backend Service URLs (Update with production URLs)
VUE_APP_API_BASE_URL=https://api.exalt-storage.com
VUE_APP_AUTH_URL=https://auth.exalt-storage.com
VUE_APP_MARKETPLACE_URL=https://marketplace.exalt-storage.com
VUE_APP_BILLING_URL=https://billing.exalt-storage.com
VUE_APP_ANALYTICS_URL=https://analytics.exalt-storage.com
VUE_APP_NOTIFICATION_URL=https://notifications.exalt-storage.com

# Feature Flags
VUE_APP_ENABLE_ANALYTICS=true
VUE_APP_ENABLE_REAL_TIME=true
VUE_APP_ENABLE_NOTIFICATIONS=true
VUE_APP_DEBUG_MODE=false

# UI Configuration
VUE_APP_THEME=light
VUE_APP_LOCALE=en
VUE_APP_PAGINATION_SIZE=50

# Production Settings
VUE_APP_SHOW_DEVTOOLS=false
VUE_APP_LOG_LEVEL=error
EOF
    echo "✅ Created .env.production template"
else
    echo "⚠️  .env.production already exists"
fi

# Git hooks setup
echo ""
echo "🪝 Setting up Git hooks..."

if [ -d ".git" ]; then
    # Pre-commit hook
    cat > .git/hooks/pre-commit << 'EOF'
#!/bin/sh
# Pre-commit hook for Vue.js application

echo "🔍 Running pre-commit checks..."

# Run type checking
echo "📝 Type checking..."
npm run type-check
if [ $? -ne 0 ]; then
    echo "❌ Type checking failed"
    exit 1
fi

# Run linting
echo "🧹 Linting..."
npm run lint
if [ $? -ne 0 ]; then
    echo "❌ Linting failed"
    exit 1
fi

# Run unit tests
echo "🧪 Running unit tests..."
npm run test:unit
if [ $? -ne 0 ]; then
    echo "❌ Unit tests failed"
    exit 1
fi

echo "✅ All pre-commit checks passed"
EOF

    chmod +x .git/hooks/pre-commit
    echo "✅ Git pre-commit hook installed"
else
    echo "⚠️  Not a Git repository - Git hooks not installed"
fi

# VS Code settings
echo ""
echo "💻 Setting up VS Code configuration..."

mkdir -p .vscode

cat > .vscode/settings.json << 'EOF'
{
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  },
  "typescript.preferences.importModuleSpecifier": "relative",
  "vue.codeActions.enabled": true,
  "emmet.includeLanguages": {
    "vue": "html"
  },
  "files.associations": {
    "*.vue": "vue"
  }
}
EOF

cat > .vscode/extensions.json << 'EOF'
{
  "recommendations": [
    "Vue.volar",
    "Vue.vscode-typescript-vue-plugin",
    "bradlc.vscode-tailwindcss",
    "esbenp.prettier-vscode",
    "dbaeumer.vscode-eslint"
  ]
}
EOF

echo "✅ VS Code configuration created"

# Initial build test
echo ""
echo "🔨 Testing build process..."

npm run build
if [ $? -eq 0 ]; then
    echo "✅ Build test successful"
    rm -rf dist  # Clean up test build
else
    echo "❌ Build test failed"
    exit 1
fi

echo ""
echo "🎉 Setup completed successfully!"
echo ""
echo "📋 Next steps:"
echo "   1. Review and update .env.local with your backend service URLs"
echo "   2. Start development server: npm run dev"
echo "   3. Open http://localhost:3202 in your browser"
echo ""
echo "🔧 Available commands:"
echo "   npm run dev          - Start development server"
echo "   npm run build        - Build for production"
echo "   npm run preview      - Preview production build"
echo "   npm run test:unit    - Run unit tests"
echo "   npm run test:e2e     - Run end-to-end tests"
echo "   npm run lint         - Run ESLint"
echo "   npm run type-check   - Run TypeScript type checking"
echo ""
echo "📚 Documentation:"
echo "   - Project README: ./README.md"
echo "   - API Documentation: ../api-docs/"
echo "   - Design System: ../docs/design-system/"
echo ""

# Make scripts executable
chmod +x scripts/*.sh

echo "✅ All setup tasks completed!"