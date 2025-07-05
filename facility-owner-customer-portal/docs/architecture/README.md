# Architecture Documentation

This document outlines the technical architecture and design patterns for the Facility Owner Customer Portal Vue.js application.

## 🏗️ System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    FACILITY OWNER CUSTOMER PORTAL              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  PRESENTATION LAYER (Vue.js 3)                                │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ Components │ Views │ Router │ Vuetify │ Composables     │   │
│  └─────────────────────────────────────────────────────────┘   │
│                               │                                 │
│  STATE MANAGEMENT LAYER (Pinia)                               │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ Auth Store │ Facility Store │ Customer Store │ UI Store │   │
│  └─────────────────────────────────────────────────────────┘   │
│                               │                                 │
│  SERVICE LAYER (API Integration)                              │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ HTTP Client │ WebSocket │ Cache │ Error Handler         │   │
│  └─────────────────────────────────────────────────────────┘   │
│                               │                                 │
│  INFRASTRUCTURE LAYER                                          │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ Nginx │ Docker │ Kubernetes │ Monitoring │ Security     │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                    BACKEND MICROSERVICES                       │
├─────────────────────────────────────────────────────────────────┤
│ warehouse-management-service │ customer-storage-marketplace    │
│ auth-service │ billing-service │ analytics-engine │ etc...     │
└─────────────────────────────────────────────────────────────────┘
```

## 🎯 Frontend Architecture

### Component Hierarchy

```
App.vue (Root Component)
├── NavigationDrawer.vue
├── AppBar.vue
├── MainContent.vue
│   └── RouterView
│       ├── DashboardView.vue
│       │   ├── MetricCard.vue
│       │   ├── OccupancyChart.vue
│       │   ├── CustomerActivity.vue
│       │   └── AlertItem.vue
│       ├── CustomersView.vue
│       │   ├── CustomerTable.vue
│       │   ├── CustomerFilter.vue
│       │   └── CustomerDetail.vue
│       ├── UnitsView.vue
│       │   ├── UnitGrid.vue
│       │   ├── FloorPlan.vue
│       │   └── UnitDetail.vue
│       ├── FinancialView.vue
│       │   ├── RevenueChart.vue
│       │   ├── RevenueBreakdown.vue
│       │   └── FinancialMetrics.vue
│       └── SettingsView.vue
│           ├── ProfileSettings.vue
│           ├── FacilitySettings.vue
│           └── NotificationSettings.vue
└── SnackbarNotifications.vue
```

### State Management Architecture

```typescript
// Pinia Store Structure
interface StoreArchitecture {
  auth: {
    user: User | null
    token: string | null
    isAuthenticated: boolean
    permissions: string[]
  }
  
  facility: {
    currentFacility: Facility | null
    units: Unit[]
    customers: Customer[]
    occupancyData: OccupancyData
    financialData: FinancialData
  }
  
  ui: {
    loading: boolean
    drawer: boolean
    theme: 'light' | 'dark'
    snackbar: SnackbarState
    modals: ModalState
  }
  
  notification: {
    notifications: Notification[]
    unreadCount: number
    preferences: NotificationPreferences
  }
}
```

## 🔄 Data Flow Architecture

### Request Flow Pattern

```
User Interaction
      ↓
Vue Component (emit event)
      ↓
Pinia Store Action
      ↓
API Service Layer
      ↓
HTTP Client (Axios)
      ↓
Backend Microservice
      ↓
Database Query
      ↓
Response Chain (reverse order)
      ↓
Reactive UI Update
```

### Error Handling Flow

```
API Error Occurs
      ↓
Axios Interceptor
      ↓
Error Handler Service
      ↓
Store Error State Update
      ↓
UI Error Display
      ↓
User Notification
```

## 🛠️ Technology Stack

### Core Technologies

| Layer | Technology | Version | Purpose |
|-------|------------|---------|---------|
| **Framework** | Vue.js | 3.4.0 | Reactive UI framework |
| **Language** | TypeScript | 5.3.3 | Type safety and developer experience |
| **UI Library** | Vuetify | 3.4.8 | Material Design components |
| **State Management** | Pinia | 2.1.7 | Reactive state management |
| **Build Tool** | Vite | 5.0.10 | Fast development and build |
| **HTTP Client** | Axios | 1.6.2 | API communication |
| **Router** | Vue Router | 4.2.5 | Client-side routing |
| **Charts** | Chart.js | 4.4.1 | Data visualization |
| **Validation** | VeeValidate + Yup | 4.12.4 | Form validation |
| **Testing** | Vitest + Playwright | Latest | Unit and E2E testing |

### Development Tools

| Tool | Purpose | Configuration |
|------|---------|---------------|
| **ESLint** | Code quality | Airbnb + Vue rules |
| **Prettier** | Code formatting | Standard config |
| **TypeScript** | Type checking | Strict mode |
| **Husky** | Git hooks | Pre-commit validation |
| **Commitizen** | Commit standards | Conventional commits |

## 🔐 Security Architecture

### Authentication Flow

```
1. User Login Request
   ↓
2. Credentials sent to auth-service (HTTPS)
   ↓
3. JWT token returned (httpOnly cookie option)
   ↓
4. Token stored in localStorage (with expiration)
   ↓
5. Token included in API requests (Authorization header)
   ↓
6. Backend validates token on each request
   ↓
7. Auto-refresh mechanism before expiration
```

### Security Measures

| Security Layer | Implementation | Details |
|----------------|----------------|---------|
| **Authentication** | JWT Bearer tokens | Secure token-based auth |
| **Authorization** | Role-based access control | Granular permissions |
| **HTTPS** | SSL/TLS encryption | End-to-end encryption |
| **XSS Protection** | Content Security Policy | Prevents script injection |
| **CSRF Protection** | SameSite cookies | Cross-site request forgery prevention |
| **Input Validation** | Client + server validation | Sanitized user inputs |
| **Error Handling** | Generic error messages | Prevents information disclosure |

## 📱 Responsive Design Architecture

### Breakpoint Strategy

```scss
// Vuetify Breakpoints
$breakpoints: (
  'xs': 0,      // Extra small devices (phones)
  'sm': 600,    // Small devices (tablets)
  'md': 960,    // Medium devices (small laptops)
  'lg': 1264,   // Large devices (desktops)
  'xl': 1904    // Extra large devices (large desktops)
);
```

### Component Responsiveness

```vue
<template>
  <v-container>
    <!-- Mobile-first responsive grid -->
    <v-row>
      <v-col 
        cols="12" 
        sm="6" 
        md="4" 
        lg="3"
        v-for="metric in metrics"
        :key="metric.id"
      >
        <MetricCard :data="metric" />
      </v-col>
    </v-row>
  </v-container>
</template>
```

## ⚡ Performance Architecture

### Optimization Strategies

| Strategy | Implementation | Impact |
|----------|----------------|--------|
| **Code Splitting** | Route-based lazy loading | Reduced initial bundle size |
| **Tree Shaking** | ES6 modules + Vite | Eliminate unused code |
| **Asset Optimization** | Image compression + WebP | Faster load times |
| **Caching** | HTTP cache + Service Worker | Reduced server requests |
| **Virtual Scrolling** | Large data sets | Improved list performance |
| **Debouncing** | Search inputs | Reduced API calls |
| **Memoization** | Expensive computations | Cached calculations |

### Bundle Analysis

```typescript
// Vite bundle analysis configuration
export default defineConfig({
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['vue', 'pinia', 'vue-router'],
          ui: ['vuetify'],
          charts: ['chart.js', 'vue-chartjs'],
          utils: ['axios', 'date-fns']
        }
      }
    }
  }
})
```

## 🔌 API Integration Architecture

### Service Layer Pattern

```typescript
// API Service Architecture
interface APIServiceLayer {
  // Base HTTP client
  httpClient: AxiosInstance
  
  // Service implementations
  authService: AuthService
  facilityService: FacilityService
  customerService: CustomerService
  billingService: BillingService
  
  // Cross-cutting concerns
  errorHandler: ErrorHandler
  requestInterceptor: RequestInterceptor
  responseInterceptor: ResponseInterceptor
  cache: APICache
}
```

### Error Boundary Pattern

```typescript
// Global error handling
class ErrorBoundary {
  handleAPIError(error: AxiosError): void {
    switch (error.response?.status) {
      case 401:
        this.handleAuthError()
        break
      case 403:
        this.handlePermissionError()
        break
      case 404:
        this.handleNotFoundError()
        break
      case 500:
        this.handleServerError()
        break
      default:
        this.handleGenericError()
    }
  }
}
```

## 🧪 Testing Architecture

### Testing Pyramid

```
E2E Tests (Playwright)
├── Critical user journeys
├── Cross-browser compatibility
└── Mobile responsiveness

Integration Tests (Vitest)
├── Component integration
├── Store interactions
└── API service mocking

Unit Tests (Vitest + Vue Test Utils)
├── Component logic
├── Utility functions
├── Store mutations
└── Validation rules
```

### Test Configuration

```typescript
// Vitest configuration
export default defineConfig({
  test: {
    environment: 'jsdom',
    setupFiles: ['./tests/setup.ts'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
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

## 🚀 Deployment Architecture

### Container Strategy

```dockerfile
# Multi-stage build
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build

FROM nginx:alpine AS production
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 3202
CMD ["nginx", "-g", "daemon off;"]
```

### Kubernetes Deployment

```yaml
# Production deployment strategy
apiVersion: apps/v1
kind: Deployment
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  template:
    spec:
      containers:
      - name: facility-portal
        resources:
          requests:
            memory: "128Mi"
            cpu: "100m"
          limits:
            memory: "256Mi"
            cpu: "200m"
```

## 📊 Monitoring Architecture

### Observability Stack

| Component | Tool | Purpose |
|-----------|------|---------|
| **Metrics** | Prometheus | Performance metrics |
| **Logging** | ELK Stack | Centralized logging |
| **Tracing** | Jaeger | Request tracing |
| **Alerting** | Grafana | Real-time alerts |
| **Uptime** | Pingdom | Availability monitoring |

### Frontend Monitoring

```typescript
// Performance monitoring
class PerformanceMonitor {
  trackPageLoad(route: string): void {
    const loadTime = performance.now()
    this.sendMetric('page_load_time', loadTime, { route })
  }
  
  trackUserInteraction(action: string): void {
    this.sendMetric('user_interaction', 1, { action })
  }
  
  trackError(error: Error): void {
    this.sendMetric('frontend_error', 1, { 
      message: error.message,
      stack: error.stack 
    })
  }
}
```

## 🔄 Data Synchronization

### Real-time Updates

```typescript
// WebSocket integration for real-time data
class WebSocketManager {
  private ws: WebSocket
  
  connect(): void {
    this.ws = new WebSocket(WS_URL)
    this.ws.onmessage = this.handleMessage.bind(this)
  }
  
  handleMessage(event: MessageEvent): void {
    const data = JSON.parse(event.data)
    
    switch (data.type) {
      case 'CUSTOMER_UPDATE':
        customerStore.updateCustomer(data.payload)
        break
      case 'UNIT_STATUS_CHANGE':
        facilityStore.updateUnit(data.payload)
        break
      case 'PAYMENT_RECEIVED':
        billingStore.updatePayment(data.payload)
        break
    }
  }
}
```

## 📋 Architecture Decisions

### ADR-001: Vue.js 3 with Composition API

**Decision**: Use Vue.js 3 with Composition API instead of Options API
**Rationale**: Better TypeScript support, improved code organization, and better performance
**Status**: Accepted

### ADR-002: Pinia for State Management

**Decision**: Use Pinia instead of Vuex
**Rationale**: Better TypeScript integration, simpler API, and Vue 3 optimization
**Status**: Accepted

### ADR-003: Vuetify 3 for UI Components

**Decision**: Use Vuetify 3 for Material Design components
**Rationale**: Comprehensive component library, consistent design, and Vue 3 compatibility
**Status**: Accepted

### ADR-004: Vite for Build Tool

**Decision**: Use Vite instead of Vue CLI
**Rationale**: Faster development server, better HMR, and modern build optimization
**Status**: Accepted

---

**Last Updated**: 2025-06-30  
**Architecture Version**: 1.0.0  
**Maintained By**: Frontend Architecture Team