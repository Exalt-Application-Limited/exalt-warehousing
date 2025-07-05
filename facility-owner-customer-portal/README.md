# Facility Owner Customer Portal

> Vue.js 3 frontend application for facility owners to manage their B2C storage marketplace operations

## 🎯 Overview

The Facility Owner Customer Portal is a comprehensive Vue.js 3 application that provides facility owners with a modern, intuitive interface to manage their storage facilities, customers, and business operations. This application is part of the Gogidix Warehousing Domain and leverages the existing backend infrastructure to deliver a seamless B2C storage marketplace experience.

## 🏗️ Architecture

### Technology Stack
- **Frontend**: Vue.js 3 + Composition API
- **UI Framework**: Vuetify 3 (Material Design)
- **State Management**: Pinia
- **Build Tool**: Vite
- **Language**: TypeScript
- **Charts**: Chart.js + Vue-ChartJS
- **HTTP Client**: Axios
- **Validation**: VeeValidate + Yup

### Backend Integration
This frontend consumes APIs from the existing Gogidix warehousing backend services:

- **warehouse-management-service** (Port 8206) - Core facility operations
- **customer-storage-marketplace-service** (Port 8230) - B2C marketplace APIs  
- **billing-service** (Port 8200) - Financial operations
- **auth-service** (Port 8080) - Authentication & authorization
- **analytics-engine** - Performance metrics
- **notification-service** - Real-time alerts

## 🚀 Features

### Dashboard
- **Real-time Metrics**: Customer count, occupancy rate, revenue, ratings
- **Visual Floor Plans**: Interactive facility layout visualization
- **Unit Management**: Comprehensive unit status and availability tracking
- **Customer Activity**: Recent sign-ups, move-outs, payment reminders
- **Financial Performance**: Revenue trends, collection rates, breakdown analysis
- **Customer Satisfaction**: Review aggregation and sentiment analysis
- **Alert System**: Priority-based customer alerts and action items

### Core Modules
1. **Customer Management** - Complete customer lifecycle management
2. **Unit Operations** - Real-time unit status, pricing, and availability
3. **Financial Reports** - Revenue tracking, billing, and analytics
4. **Support Tickets** - Customer service and issue resolution
5. **Settings** - Facility configuration and user preferences

## 📁 Project Structure

```
facility-owner-customer-portal/
├── src/
│   ├── components/           # Reusable Vue components
│   │   ├── MetricCard.vue   # Dashboard metric cards
│   │   ├── AlertItem.vue    # Customer alert components
│   │   └── ...
│   ├── views/               # Page-level components
│   │   ├── DashboardView.vue
│   │   ├── CustomersView.vue
│   │   ├── UnitsView.vue
│   │   ├── FinancialView.vue
│   │   └── ...
│   ├── stores/              # Pinia state management
│   │   ├── auth.ts          # Authentication store
│   │   ├── facility.ts      # Facility operations store
│   │   └── notification.ts  # Notification store
│   ├── services/            # API integration layer
│   │   ├── api/
│   │   │   ├── index.ts     # Axios configuration
│   │   │   ├── auth.ts      # Auth API calls
│   │   │   ├── facility.ts  # Facility API calls
│   │   │   └── ...
│   ├── types/               # TypeScript type definitions
│   │   ├── auth.ts
│   │   ├── facility.ts
│   │   └── ...
│   ├── router/              # Vue Router configuration
│   │   └── index.ts
│   ├── App.vue              # Root application component
│   └── main.ts              # Application entry point
├── public/                  # Static assets
├── package.json
├── vite.config.ts           # Vite configuration
├── tsconfig.json           # TypeScript configuration
└── README.md
```

## 🛠️ Development Setup

### Prerequisites
- Node.js 18+ 
- npm or yarn
- Access to Gogidix backend services

### Installation

1. **Clone the repository**
   ```bash
   cd warehousing/facility-owner-customer-portal
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Configure environment**
   ```bash
   cp .env.example .env.local
   # Edit .env.local with your backend service URLs
   ```

4. **Start development server**
   ```bash
   npm run dev
   ```

The application will be available at `http://localhost:3202`

### Development Commands

```bash
# Development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Run unit tests
npm run test:unit

# Run e2e tests
npm run test:e2e

# Lint and fix
npm run lint

# Type checking
npm run type-check
```

## 🔌 API Integration

### Backend Service Endpoints

The application connects to multiple backend services:

```typescript
// API Configuration
const API_ENDPOINTS = {
  // Auth service (Port 8080)
  AUTH: {
    LOGIN: '/auth/api/v1/auth/login',
    PROFILE: '/auth/api/v1/auth/profile'
  },
  
  // Warehouse Management (Port 8206)  
  WAREHOUSE: {
    FACILITIES: '/api/v1/warehouses',
    UNITS: '/api/v1/warehouses/{facilityId}/zones'
  },
  
  // Customer Marketplace (Port 8230)
  MARKETPLACE: {
    CUSTOMERS: '/marketplace/api/v1/customers',
    RESERVATIONS: '/marketplace/api/v1/reservations'
  }
}
```

### Authentication Flow

1. User logs in via auth-service
2. JWT token stored in localStorage  
3. Token included in all API requests
4. Automatic logout on token expiration

## 📊 Data Flow

```
Vue Components
    ↓
Pinia Stores (State Management)
    ↓  
API Services (Axios)
    ↓
Backend Microservices
    ↓
PostgreSQL Database
```

## 🎨 UI/UX Design

### Design System
- **Material Design 3** via Vuetify
- **Responsive Layout** - Mobile-first approach
- **Dark/Light Themes** - User preference support
- **Accessibility** - WCAG 2.1 AA compliance

### Key Design Patterns
- **Dashboard Cards** - Metric visualization
- **Data Tables** - Customer and unit management
- **Modal Dialogs** - Form interactions
- **Progressive Enhancement** - Graceful degradation

## 🔒 Security

### Authentication & Authorization
- JWT-based authentication
- Role-based access control (facility_owner, manager, staff)
- Secure token storage and rotation
- API request interceptors for token handling

### Data Protection
- Input validation on all forms
- XSS protection via Vue's built-in escaping
- CSRF protection through API tokens
- Secure HTTP-only communication

## 📈 Performance

### Optimization Strategies
- **Code Splitting** - Route-based lazy loading
- **Tree Shaking** - Unused code elimination  
- **Asset Optimization** - Image compression and lazy loading
- **Caching** - API response caching with invalidation
- **Bundle Analysis** - Regular bundle size monitoring

### Monitoring
- Error tracking integration
- Performance metrics collection
- User interaction analytics
- Real-time uptime monitoring

## 🚀 Deployment

### Development Environment
```bash
# Build for development
npm run build:dev

# Deploy to staging
kubectl apply -f k8s/staging/
```

### Production Environment  
```bash
# Build for production
npm run build

# Deploy to production
kubectl apply -f k8s/production/
```

### Docker Support
```dockerfile
# Multi-stage build for optimized production image
FROM node:18-alpine as build
# ... build steps

FROM nginx:alpine as production
# ... nginx configuration
```

## 🧪 Testing

### Testing Strategy
- **Unit Tests** - Component logic and utility functions
- **Integration Tests** - API service interactions
- **E2E Tests** - Critical user journey validation
- **Visual Tests** - UI component regression testing

### Test Commands
```bash
# Unit tests with Vitest
npm run test:unit

# E2E tests with Playwright  
npm run test:e2e

# Coverage report
npm run test:coverage
```

## 📝 Contributing

### Development Workflow
1. Create feature branch from `dev`
2. Implement changes with tests
3. Run linting and type checking
4. Submit PR for code review
5. Deploy to staging for validation
6. Merge to `dev` branch

### Code Standards
- **TypeScript** - Strict type checking enabled
- **ESLint** - Airbnb configuration with Vue rules
- **Prettier** - Code formatting consistency
- **Conventional Commits** - Semantic commit messages

## 🔗 Related Services

This frontend application integrates with the following backend services:

- [warehouse-management-service](../warehouse-management-service/) - Core facility operations
- [customer-storage-marketplace-service](../customer-storage-marketplace-service/) - B2C marketplace  
- [billing-service](../billing-service/) - Financial operations
- [inventory-service](../inventory-service/) - Stock management
- [warehouse-analytics](../warehouse-analytics/) - Performance metrics

## 📞 Support

### Development Support
- **Technical Issues**: Contact development team
- **API Questions**: Reference backend service documentation
- **UI/UX Feedback**: Submit issues with screenshots

### Documentation
- [API Documentation](../../api-docs/)
- [Design System Guide](../../docs/design-system/)
- [Deployment Guide](../../docs/deployment/)

---

**Project Status**: 🚧 Active Development  
**Version**: 1.0.0  
**Last Updated**: 2025-06-30  
**Maintainer**: Gogidix Application Limited