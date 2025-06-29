#!/bin/bash

# Script to generate README.md files for warehousing services missing documentation

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Function to generate README for Java service
generate_java_readme() {
    local service_name=$1
    local service_path=$2
    local description=$3
    
    cat > "$service_path/README.md" << EOF
# ${service_name}

## Overview
${service_name} is a microservice component of the Warehousing Domain that ${description}.

## Technology Stack
- Java 17
- Spring Boot 3.x
- Maven
- MySQL/PostgreSQL (as applicable)
- Docker
- Kubernetes

## Prerequisites
- JDK 17 or higher
- Maven 3.8+
- Docker and Docker Compose
- MySQL/PostgreSQL (for local development)

## Getting Started

### Local Development
1. Clone the repository
2. Navigate to the service directory:
   \`\`\`bash
   cd ${service_name}
   \`\`\`

3. Install dependencies:
   \`\`\`bash
   mvn clean install
   \`\`\`

4. Run the service locally:
   \`\`\`bash
   mvn spring-boot:run
   \`\`\`

### Docker Development
Build and run with Docker:
\`\`\`bash
docker-compose up --build
\`\`\`

## API Documentation
API documentation is available at:
- Swagger UI: http://localhost:8080/swagger-ui.html (when running locally)
- OpenAPI spec: [api-docs/openapi.yaml](api-docs/openapi.yaml)

## Configuration
Configuration properties can be found in:
- \`src/main/resources/application.yml\` - Default configuration
- \`src/main/resources/application-dev.yml\` - Development configuration
- \`src/main/resources/application-prod.yml\` - Production configuration

Key configuration properties:
- \`server.port\` - Service port (default: 8080)
- \`spring.datasource.*\` - Database connection settings
- \`warehouse.*\` - Warehouse-specific settings

## Database
This service uses database migrations located in \`src/main/resources/db/migration/\`.
Migrations are automatically applied on startup using Flyway.

## Testing

### Unit Tests
\`\`\`bash
mvn test
\`\`\`

### Integration Tests
\`\`\`bash
mvn verify
\`\`\`

### Code Coverage
\`\`\`bash
mvn jacoco:report
\`\`\`

## Build
\`\`\`bash
mvn clean package
\`\`\`

## Deployment
The service is deployed using Kubernetes. Deployment configurations are in the \`k8s/\` directory.

## Monitoring
- Health check: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics
- Info: http://localhost:8080/actuator/info

## Contributing
Please read the main project's contributing guidelines before submitting pull requests.

## License
This project is part of the Warehousing Domain Ecosystem and follows the same license terms.
EOF
}

# Function to generate README for Frontend service
generate_frontend_readme() {
    local service_name=$1
    local service_path=$2
    local is_react_native=$3
    local description=$4
    
    if [ "$is_react_native" = "true" ]; then
        cat > "$service_path/README.md" << EOF
# ${service_name}

## Overview
${service_name} is a React Native mobile application for warehouse staff ${description}.

## Technology Stack
- React Native
- React 18.x
- Redux Toolkit
- React Navigation
- TypeScript
- Expo (if applicable)

## Prerequisites
- Node.js 18+
- npm or yarn
- React Native development environment setup
- Android Studio (for Android development)
- Xcode (for iOS development on macOS)

## Getting Started

### Installation
1. Clone the repository
2. Navigate to the app directory:
   \`\`\`bash
   cd ${service_name}
   \`\`\`

3. Install dependencies:
   \`\`\`bash
   npm install
   # or
   yarn install
   \`\`\`

4. Install iOS dependencies (macOS only):
   \`\`\`bash
   cd ios && pod install
   \`\`\`

### Running the App

#### Android
\`\`\`bash
npm run android
# or
yarn android
\`\`\`

#### iOS (macOS only)
\`\`\`bash
npm run ios
# or
yarn ios
\`\`\`

#### Metro Bundler
Start the Metro bundler separately:
\`\`\`bash
npm start
# or
yarn start
\`\`\`

## Development

### Code Style
\`\`\`bash
npm run lint
\`\`\`

### Testing
\`\`\`bash
npm test
\`\`\`

### Building for Production

#### Android
\`\`\`bash
npm run build:android
\`\`\`

The APK will be generated in \`android/app/build/outputs/apk/release/\`

#### iOS
Build through Xcode or use:
\`\`\`bash
cd ios && xcodebuild -workspace [WorkspaceName].xcworkspace -scheme [SchemeName] -configuration Release
\`\`\`

## Project Structure
\`\`\`
src/
├── components/     # Reusable components
├── screens/        # Screen components
├── navigation/     # Navigation configuration
├── store/          # Redux store and slices
├── services/       # API services
├── utils/          # Utility functions
└── types/          # TypeScript type definitions
\`\`\`

## Contributing
Please read the main project's contributing guidelines before submitting pull requests.

## License
This project is part of the Warehousing Domain Ecosystem and follows the same license terms.
EOF
    else
        cat > "$service_path/README.md" << EOF
# ${service_name}

## Overview
${service_name} is a web application for ${description}.

## Technology Stack
- React 18.x / Vue.js 3.x
- TypeScript
- ${service_name == "regional-admin" ? "Vuex/Pinia" : "Redux Toolkit"}
- ${service_name == "regional-admin" ? "Vue Router" : "React Router"}
- Axios for API calls
- ${service_name == "regional-admin" ? "Vuetify/Element Plus" : "Material-UI/Ant Design"}

## Prerequisites
- Node.js 18+
- npm or yarn

## Getting Started

### Installation
1. Clone the repository
2. Navigate to the app directory:
   \`\`\`bash
   cd ${service_name}
   \`\`\`

3. Install dependencies:
   \`\`\`bash
   npm install
   # or
   yarn install
   \`\`\`

### Development
Start the development server:
\`\`\`bash
npm run ${service_name == "regional-admin" ? "serve" : "start"}
# or
yarn ${service_name == "regional-admin" ? "serve" : "start"}
\`\`\`

The application will be available at http://localhost:${service_name == "regional-admin" ? "8081" : "3000"}

## Available Scripts

- \`npm run ${service_name == "regional-admin" ? "serve" : "start"}\` - Runs the app in development mode
- \`npm test\` - Launches the test runner
- \`npm run build\` - Builds the app for production
- \`npm run lint\` - Runs the linter

## Build
Build for production:
\`\`\`bash
npm run build
\`\`\`

The build artifacts will be stored in the \`${service_name == "regional-admin" ? "dist" : "build"}/\` directory.

## Testing

### Unit Tests
\`\`\`bash
npm test
\`\`\`

### E2E Tests
\`\`\`bash
npm run test:e2e
\`\`\`

## Environment Variables
Create a \`.env\` file in the root directory:
\`\`\`
${service_name == "regional-admin" ? "VUE_APP_API_URL" : "REACT_APP_API_URL"}=http://localhost:8080/api
${service_name == "regional-admin" ? "VUE_APP_ENV" : "REACT_APP_ENV"}=development
\`\`\`

## Project Structure
\`\`\`
src/
├── components/     # Reusable components
├── ${service_name == "regional-admin" ? "views" : "pages"}/          # Page components
├── ${service_name == "regional-admin" ? "router" : "routes"}/         # Routing configuration
├── services/       # API services
├── store/          # State management
├── utils/          # Utility functions
├── types/          # TypeScript types
└── ${service_name == "regional-admin" ? "assets" : "styles"}/         # Global styles and assets
\`\`\`

## Docker
Build and run with Docker:
\`\`\`bash
docker build -t ${service_name} .
docker run -p ${service_name == "regional-admin" ? "8081:80" : "3000:3000"} ${service_name}
\`\`\`

## Contributing
Please read the main project's contributing guidelines before submitting pull requests.

## License
This project is part of the Warehousing Domain Ecosystem and follows the same license terms.
EOF
    fi
}

# Service descriptions
declare -A service_descriptions=(
    ["self-storage-service"]="manages self-storage facilities, unit allocations, and customer storage operations"
    ["warehouse-subscription"]="handles warehouse subscription plans, billing cycles, and feature access management"
    ["config-server-enterprise"]="provides centralized configuration management for all warehousing microservices"
    ["integration-tests"]="contains integration test suites for validating cross-service interactions"
    ["warehousing-production"]="manages production environment configurations and deployment settings"
    ["warehousing-staging"]="manages staging environment configurations and testing deployments"
    ["global-hq-admin"]="warehouse headquarters administration and global operations management"
    ["regional-admin"]="regional warehouse management and administration"
    ["staff-mobile-app"]="to manage daily warehouse operations, inventory tracking, and task assignments"
)

# Generate README files for services missing them
echo "Generating README files for warehousing services..."

# Java services
for service in self-storage-service warehouse-subscription; do
    if [ -d "$service" ] && [ ! -f "$service/README.md" ]; then
        echo "Creating README for $service"
        desc="${service_descriptions[$service]:-"provides specialized warehousing functionality"}"
        generate_java_readme "$service" "$service" "$desc"
    fi
done

# Additional modules
for service in config-server-enterprise integration-tests warehousing-production warehousing-staging; do
    if [ -d "$service" ] && [ ! -f "$service/README.md" ]; then
        echo "Creating README for $service"
        desc="${service_descriptions[$service]:-"provides specialized warehousing functionality"}"
        generate_java_readme "$service" "$service" "$desc"
    fi
done

# Frontend services
if [ -d "global-hq-admin" ] && [ ! -f "global-hq-admin/README.md" ]; then
    echo "Creating README for global-hq-admin"
    desc="${service_descriptions["global-hq-admin"]}"
    generate_frontend_readme "global-hq-admin" "global-hq-admin" "false" "$desc"
fi

if [ -d "regional-admin" ] && [ ! -f "regional-admin/README.md" ]; then
    echo "Creating README for regional-admin"
    desc="${service_descriptions["regional-admin"]}"
    generate_frontend_readme "regional-admin" "regional-admin" "false" "$desc"
fi

if [ -d "staff-mobile-app" ] && [ ! -f "staff-mobile-app/README.md" ]; then
    echo "Creating README for staff-mobile-app"
    desc="${service_descriptions["staff-mobile-app"]}"
    generate_frontend_readme "staff-mobile-app" "staff-mobile-app" "true" "$desc"
fi

echo "README generation completed!"