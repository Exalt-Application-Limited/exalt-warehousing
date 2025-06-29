#!/bin/bash

echo "🔧 Standardizing Node.js Services..."
echo ""

# Node.js services
NODE_SERVICES=(
    "global-hq-admin"
    "regional-admin"
    "staff-mobile-app"
)

# Create standard package.json template function
create_standard_package() {
    local service=$1
    local package_file="$service/package.json"
    
    if [ -f "$package_file" ]; then
        echo "📦 Standardizing $service..."
        
        # Backup original
        cp "$package_file" "$package_file.original" 2>/dev/null
        
        # Extract existing info
        local name=$(jq -r '.name // "'$service'"' "$package_file" 2>/dev/null || echo "$service")
        local description=$(jq -r '.description // "Warehousing '$service' service"' "$package_file" 2>/dev/null || echo "Warehousing $service service")
        
        # Determine app type
        local app_type="react"
        if [[ "$service" == "regional-admin" ]]; then
            app_type="vue"
        elif [[ "$service" == "staff-mobile-app" ]]; then
            app_type="react-native"
        fi
        
        # Create standardized package.json
        if [[ "$app_type" == "react" ]]; then
            cat > "$package_file" << EOF
{
  "name": "@exalt-warehousing/$service",
  "version": "1.0.0",
  "private": true,
  "description": "$description",
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build",
    "test": "react-scripts test",
    "eject": "react-scripts eject",
    "lint": "eslint src --ext .js,.jsx,.ts,.tsx",
    "lint:fix": "eslint src --ext .js,.jsx,.ts,.tsx --fix",
    "format": "prettier --write 'src/**/*.{js,jsx,ts,tsx,json,css,scss,md}'"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.16.0",
    "axios": "^1.5.1",
    "@mui/material": "^5.14.10",
    "@emotion/react": "^11.11.1",
    "@emotion/styled": "^11.11.0",
    "react-query": "^3.39.3",
    "zustand": "^4.4.3",
    "react-hook-form": "^7.47.0",
    "date-fns": "^2.30.0"
  },
  "devDependencies": {
    "react-scripts": "5.0.1",
    "@types/react": "^18.2.28",
    "@types/react-dom": "^18.2.13",
    "@types/node": "^20.8.6",
    "typescript": "^5.2.2",
    "eslint": "^8.51.0",
    "prettier": "^3.0.3",
    "@testing-library/react": "^14.0.0",
    "@testing-library/jest-dom": "^6.1.3",
    "@testing-library/user-event": "^14.5.1"
  },
  "eslintConfig": {
    "extends": [
      "react-app",
      "react-app/jest"
    ]
  },
  "browserslist": {
    "production": [
      ">0.2%",
      "not dead",
      "not op_mini all"
    ],
    "development": [
      "last 1 chrome version",
      "last 1 firefox version",
      "last 1 safari version"
    ]
  }
}
EOF
        elif [[ "$app_type" == "vue" ]]; then
            cat > "$package_file" << EOF
{
  "name": "@exalt-warehousing/$service",
  "version": "1.0.0",
  "private": true,
  "description": "$description",
  "scripts": {
    "serve": "vue-cli-service serve",
    "build": "vue-cli-service build",
    "test": "vue-cli-service test:unit",
    "lint": "vue-cli-service lint",
    "format": "prettier --write 'src/**/*.{js,jsx,ts,tsx,vue,json,css,scss,md}'"
  },
  "dependencies": {
    "vue": "^3.3.4",
    "vue-router": "^4.2.5",
    "vuex": "^4.1.0",
    "axios": "^1.5.1",
    "vuetify": "^3.3.19",
    "@mdi/font": "^7.3.67",
    "date-fns": "^2.30.0",
    "vee-validate": "^4.11.8"
  },
  "devDependencies": {
    "@vue/cli-plugin-router": "~5.0.8",
    "@vue/cli-plugin-vuex": "~5.0.8",
    "@vue/cli-plugin-typescript": "~5.0.8",
    "@vue/cli-plugin-unit-jest": "~5.0.8",
    "@vue/cli-service": "~5.0.8",
    "@types/jest": "^29.5.5",
    "@typescript-eslint/eslint-plugin": "^6.7.5",
    "@typescript-eslint/parser": "^6.7.5",
    "@vue/eslint-config-typescript": "^12.0.0",
    "@vue/test-utils": "^2.4.1",
    "@vue/vue3-jest": "^29.2.6",
    "eslint": "^8.51.0",
    "eslint-plugin-vue": "^9.17.0",
    "jest": "^29.7.0",
    "prettier": "^3.0.3",
    "sass": "^1.69.3",
    "sass-loader": "^13.3.2",
    "typescript": "^5.2.2",
    "vue-tsc": "^1.8.19"
  }
}
EOF
        elif [[ "$app_type" == "react-native" ]]; then
            cat > "$package_file" << EOF
{
  "name": "@exalt-warehousing/$service",
  "version": "1.0.0",
  "private": true,
  "description": "$description",
  "main": "index.js",
  "scripts": {
    "start": "expo start",
    "android": "expo start --android",
    "ios": "expo start --ios",
    "web": "expo start --web",
    "test": "jest",
    "lint": "eslint . --ext .js,.jsx,.ts,.tsx",
    "format": "prettier --write '**/*.{js,jsx,ts,tsx,json,md}'"
  },
  "dependencies": {
    "expo": "~49.0.13",
    "expo-status-bar": "~1.6.0",
    "react": "18.2.0",
    "react-native": "0.72.5",
    "@react-navigation/native": "^6.1.8",
    "@react-navigation/stack": "^6.3.18",
    "@react-navigation/bottom-tabs": "^6.5.9",
    "react-native-screens": "~3.25.0",
    "react-native-safe-area-context": "4.6.3",
    "react-native-gesture-handler": "~2.12.0",
    "axios": "^1.5.1",
    "react-native-paper": "^5.10.6",
    "react-native-vector-icons": "^10.0.0",
    "@react-native-async-storage/async-storage": "1.18.2",
    "expo-barcode-scanner": "~12.5.3",
    "zustand": "^4.4.3"
  },
  "devDependencies": {
    "@babel/core": "^7.20.0",
    "@types/react": "~18.2.14",
    "@types/react-native": "^0.72.3",
    "typescript": "^5.1.3",
    "jest": "^29.2.1",
    "@testing-library/react-native": "^12.3.0",
    "eslint": "^8.51.0",
    "eslint-config-expo": "^7.0.0",
    "prettier": "^3.0.3"
  },
  "jest": {
    "preset": "jest-expo"
  }
}
EOF
        fi
        
        echo "✅ Standardized $service"
    else
        echo "⚠️  $service/package.json not found"
    fi
}

# Fix caniuse-lite issues
fix_caniuse_lite() {
    echo ""
    echo "🔧 Fixing caniuse-lite issues..."
    npx browserslist@latest --update-db 2>/dev/null || true
}

# Create .env.template for each service
create_env_templates() {
    echo ""
    echo "📝 Creating .env.template files..."
    
    for service in "${NODE_SERVICES[@]}"; do
        if [ -d "$service" ]; then
            cat > "$service/.env.template" << 'EOF'
# Environment Configuration
NODE_ENV=development
PORT=3000

# API Configuration
REACT_APP_API_URL=http://localhost:8080
REACT_APP_API_VERSION=v1

# Authentication
REACT_APP_AUTH_DOMAIN=
REACT_APP_AUTH_CLIENT_ID=

# Feature Flags
REACT_APP_ENABLE_ANALYTICS=false
REACT_APP_ENABLE_DEBUG=true

# External Services
REACT_APP_GOOGLE_MAPS_KEY=
REACT_APP_SENTRY_DSN=

# Build Configuration
GENERATE_SOURCEMAP=false
EOF
            echo "✅ Created .env.template for $service"
        fi
    done
}

# Standardize each Node.js service
for service in "${NODE_SERVICES[@]}"; do
    create_standard_package "$service"
done

# Fix caniuse-lite
fix_caniuse_lite

# Create env templates
create_env_templates

echo ""
echo "✅ Node.js services standardization completed!"
echo ""
echo "📊 Summary:"
echo "- ${#NODE_SERVICES[@]} services standardized"
echo "- Dependencies updated to latest stable versions"
echo "- ESLint and Prettier configured"
echo "- .env.template files created"
echo ""
echo "⚠️  Note: Run 'npm install' in each service directory to install dependencies"