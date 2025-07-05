# Setup Documentation

This document provides detailed instructions for setting up the Facility Owner Customer Portal development environment and deploying to various environments.

## 🛠️ Development Environment Setup

### Prerequisites

#### Required Software

| Software | Version | Purpose | Installation |
|----------|---------|---------|-------------|
| **Node.js** | 18.17.0+ | JavaScript runtime | [Download](https://nodejs.org/) |
| **npm** | 9.0.0+ | Package manager | Included with Node.js |
| **Git** | 2.40.0+ | Version control | [Download](https://git-scm.com/) |
| **Docker** | 20.10.0+ | Containerization | [Download](https://docker.com/) |
| **kubectl** | 1.27.0+ | Kubernetes CLI | [Install Guide](https://kubernetes.io/docs/tasks/tools/) |
| **Visual Studio Code** | Latest | IDE (recommended) | [Download](https://code.visualstudio.com/) |

#### Recommended VS Code Extensions

```bash
# Install recommended extensions
code --install-extension Vue.volar
code --install-extension Vue.vscode-typescript-vue-plugin
code --install-extension bradlc.vscode-tailwindcss
code --install-extension esbenp.prettier-vscode
code --install-extension dbaeumer.vscode-eslint
code --install-extension ms-vscode.vscode-typescript-next
```

### Initial Setup

#### 1. Clone Repository

```bash
# Clone the warehousing repository
git clone https://github.com/Gogidix-Application-Limited/gogidix-warehousing.git
cd gogidix-warehousing/facility-owner-customer-portal

# Switch to development branch
git checkout dev
```

#### 2. Install Dependencies

```bash
# Install Node.js dependencies
npm install

# Verify installation
npm run dev --dry-run
```

#### 3. Environment Configuration

```bash
# Copy environment template
cp .env.template .env.local

# Edit environment variables
# VITE_API_BASE_URL=http://localhost:8080/api/v1
# VITE_WS_URL=ws://localhost:8080/ws
# VITE_AUTH_SERVICE_URL=http://localhost:8081
# VITE_STORAGE_SERVICE_URL=http://localhost:8082
```

#### Environment Variables Reference

| Variable | Description | Development | Staging | Production |
|----------|-------------|-------------|---------|------------|
| **VITE_API_BASE_URL** | Backend API URL | http://localhost:8080/api/v1 | https://staging-api.gogidix-storage.com/api/v1 | https://api.gogidix-storage.com/api/v1 |
| **VITE_WS_URL** | WebSocket URL | ws://localhost:8080/ws | wss://staging-ws.gogidix-storage.com/ws | wss://ws.gogidix-storage.com/ws |
| **VITE_AUTH_SERVICE_URL** | Authentication service | http://localhost:8081 | https://staging-auth.gogidix-storage.com | https://auth.gogidix-storage.com |
| **VITE_STORAGE_SERVICE_URL** | Storage service URL | http://localhost:8082 | https://staging-storage.gogidix-storage.com | https://storage.gogidix-storage.com |
| **VITE_CDN_URL** | CDN for assets | http://localhost:3202/assets | https://staging-cdn.gogidix-storage.com | https://cdn.gogidix-storage.com |

### Backend Services Setup

#### Required Backend Services

```bash
# Start required services using Docker Compose
docker-compose -f ../docker-compose.dev.yml up -d \
  warehouse-management-service \
  auth-service \
  billing-service \
  analytics-engine

# Verify services are running
docker-compose ps
```

#### Service Health Checks

```bash
# Check warehouse management service
curl http://localhost:8080/actuator/health

# Check authentication service
curl http://localhost:8081/actuator/health

# Check billing service
curl http://localhost:8082/actuator/health

# Check analytics engine
curl http://localhost:8083/actuator/health
```

### Database Setup

#### PostgreSQL Database

```bash
# Start PostgreSQL container
docker run --name facility-portal-db \
  -e POSTGRES_DB=facility_portal \
  -e POSTGRES_USER=portal_user \
  -e POSTGRES_PASSWORD=portal_pass \
  -p 5432:5432 \
  -d postgres:15

# Run database migrations
npm run db:migrate

# Seed development data
npm run db:seed
```

#### Redis Cache

```bash
# Start Redis container
docker run --name facility-portal-redis \
  -p 6379:6379 \
  -d redis:7-alpine

# Test Redis connection
redis-cli ping
```

### Development Server

```bash
# Start development server
npm run dev

# Server will start on http://localhost:3202
# Hot reload enabled for development
```

#### Development Commands

| Command | Description |
|---------|-------------|
| `npm run dev` | Start development server with hot reload |
| `npm run build` | Build for production |
| `npm run preview` | Preview production build locally |
| `npm run test` | Run unit tests |
| `npm run test:e2e` | Run end-to-end tests |
| `npm run lint` | Run ESLint |
| `npm run lint:fix` | Fix ESLint errors |
| `npm run type-check` | Run TypeScript type checking |

## 🧪 Testing Setup

### Unit Tests

```bash
# Run unit tests
npm run test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test:coverage
```

### End-to-End Tests

```bash
# Install Playwright browsers
npx playwright install

# Run E2E tests
npm run test:e2e

# Run E2E tests in headed mode
npm run test:e2e:headed

# Generate E2E test report
npm run test:e2e:report
```

### Test Configuration

```typescript
// vitest.config.ts
export default defineConfig({
  test: {
    environment: 'jsdom',
    setupFiles: ['./tests/setup.ts'],
    coverage: {
      provider: 'v8',
      threshold: {
        global: {
          branches: 80,
          functions: 80,
          lines: 80,
          statements: 80
        }
      }
    }
  }
})
```

## 🏗️ Build & Deployment

### Local Build

```bash
# Build for production
npm run build

# Output will be in dist/ directory
ls -la dist/

# Preview production build
npm run preview
```

### Docker Build

```bash
# Build Docker image
docker build -t facility-portal:latest .

# Run Docker container
docker run -p 3202:3202 facility-portal:latest

# Test container health
curl http://localhost:3202/health
```

### Multi-stage Docker Build

```dockerfile
# Build stage
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build

# Production stage
FROM nginx:alpine AS production
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 3202
CMD ["nginx", "-g", "daemon off;"]
```

## ☁️ Cloud Deployment

### AWS ECS Deployment

```bash
# Build and push to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

docker build -t facility-portal .
docker tag facility-portal:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/facility-portal:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/facility-portal:latest

# Deploy to ECS
aws ecs update-service --cluster warehousing-cluster --service facility-portal --force-new-deployment
```

### Kubernetes Deployment

```bash
# Apply Kubernetes manifests
kubectl apply -f k8s/

# Check deployment status
kubectl get pods -n warehousing -l app=facility-portal

# View logs
kubectl logs -f deployment/facility-portal -n warehousing

# Port forward for testing
kubectl port-forward svc/facility-portal 3202:3202 -n warehousing
```

### Staging Deployment

```bash
# Deploy to staging
git checkout staging
git merge dev
git push origin staging

# Trigger staging deployment
kubectl set image deployment/facility-portal facility-portal=registry.gogidix-storage.com/facility-portal:staging -n warehousing-staging

# Verify staging deployment
curl https://portal-staging.gogidix-storage.com/health
```

### Production Deployment

```bash
# Deploy to production (requires approval)
git checkout main
git merge staging
git tag v1.0.0
git push origin main --tags

# Production deployment (automated via GitHub Actions)
# Manual deployment if needed:
kubectl set image deployment/facility-portal facility-portal=registry.gogidix-storage.com/facility-portal:v1.0.0 -n warehousing-prod

# Verify production deployment
curl https://portal.gogidix-storage.com/health
```

## 🔧 Configuration Management

### Environment-Specific Configs

```bash
# Development
export NODE_ENV=development
export VITE_API_BASE_URL=http://localhost:8080/api/v1

# Staging
export NODE_ENV=staging
export VITE_API_BASE_URL=https://staging-api.gogidix-storage.com/api/v1

# Production
export NODE_ENV=production
export VITE_API_BASE_URL=https://api.gogidix-storage.com/api/v1
```

### Kubernetes ConfigMaps

```yaml
# Apply configuration
kubectl apply -f - <<EOF
apiVersion: v1
kind: ConfigMap
metadata:
  name: facility-portal-config
  namespace: warehousing
data:
  API_BASE_URL: "https://api.gogidix-storage.com/api/v1"
  WS_URL: "wss://ws.gogidix-storage.com/ws"
  CDN_URL: "https://cdn.gogidix-storage.com"
EOF
```

### Secrets Management

```bash
# Create secrets
kubectl create secret generic facility-portal-secrets \
  --from-literal=auth-token="<jwt-secret>" \
  --from-literal=api-key="<api-key>" \
  -n warehousing

# Verify secrets
kubectl get secrets -n warehousing
```

## 🔍 Monitoring & Debugging

### Health Checks

```bash
# Application health
curl http://localhost:3202/health

# Detailed health information
curl http://localhost:3202/health/detailed

# Metrics endpoint
curl http://localhost:3202/metrics
```

### Log Analysis

```bash
# View application logs
npm run logs

# Filter error logs
npm run logs | grep ERROR

# Tail logs in real-time
npm run logs:tail
```

### Performance Profiling

```bash
# Bundle analysis
npm run build:analyze

# Performance audit
npm run audit:performance

# Lighthouse CI
npm run lighthouse
```

## 🔐 Security Setup

### SSL/TLS Configuration

```bash
# Generate self-signed certificate for development
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -nodes

# Configure HTTPS for development
npm run dev:https
```

### Environment Security

```bash
# Audit dependencies
npm audit

# Fix vulnerabilities
npm audit fix

# Security scan
npm run security:scan
```

## 🚨 Troubleshooting

### Common Issues

#### Node.js Version Issues

```bash
# Check Node.js version
node --version

# Install correct version using nvm
nvm install 18.17.0
nvm use 18.17.0
```

#### Dependency Conflicts

```bash
# Clear npm cache
npm cache clean --force

# Remove node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

#### Port Conflicts

```bash
# Find process using port 3202
lsof -i :3202

# Kill process
kill -9 <PID>

# Use different port
VITE_PORT=3203 npm run dev
```

#### Backend Connection Issues

```bash
# Check backend services
docker-compose ps

# Restart services
docker-compose restart warehouse-management-service

# View service logs
docker-compose logs -f warehouse-management-service
```

### Debug Mode

```bash
# Enable debug mode
DEBUG=facility-portal:* npm run dev

# Debug specific module
DEBUG=facility-portal:auth npm run dev

# Debug with Chrome DevTools
node --inspect-brk node_modules/.bin/vite
```

## 📚 Development Guidelines

### Code Style

```bash
# Format code
npm run format

# Lint code
npm run lint

# Type check
npm run type-check

# Pre-commit hooks
npm run prepare
```

### Git Workflow

```bash
# Create feature branch
git checkout dev
git pull origin dev
git checkout -b feature/new-feature

# Commit changes
git add .
git commit -m "feat: add new feature"

# Push and create PR
git push origin feature/new-feature
# Create PR to dev branch
```

### Component Development

```vue
<!-- Component template -->
<template>
  <v-card>
    <v-card-title>{{ title }}</v-card-title>
    <v-card-text>
      <slot />
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
interface Props {
  title: string
}

defineProps<Props>()
</script>
```

---

**Last Updated**: 2025-06-30  
**Setup Version**: 1.0.0  
**Maintained By**: Frontend Development Team