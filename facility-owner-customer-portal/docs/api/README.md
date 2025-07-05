# API Integration Documentation

This document provides comprehensive information about backend service integration for the Facility Owner Customer Portal.

## 🔌 Backend Services Integration

The Vue.js frontend integrates with multiple backend microservices to provide a complete facility management experience.

### Service Architecture

```
Frontend (Vue.js 3 - Port 3202)
├── warehouse-management-service (Port 8206) - Core facility operations
├── customer-storage-marketplace-service (Port 8230) - B2C marketplace
├── billing-service (Port 8200) - Financial operations
├── auth-service (Port 8080) - Authentication & authorization
├── inventory-service (Port 8203) - Stock management
├── warehouse-analytics (Port 8205) - Performance metrics
└── notification-service (Port 8090) - Real-time alerts
```

## 📡 API Endpoints

### Authentication Service (Port 8080)

```typescript
// Authentication endpoints
const AUTH_ENDPOINTS = {
  LOGIN: '/auth/api/v1/auth/login',
  LOGOUT: '/auth/api/v1/auth/logout',
  PROFILE: '/auth/api/v1/auth/profile',
  REFRESH: '/auth/api/v1/auth/refresh',
  CHANGE_PASSWORD: '/auth/api/v1/auth/change-password'
}

// Example usage
const loginResponse = await authApi.login({
  email: 'owner@facility.com',
  password: 'securePassword123'
})
```

### Warehouse Management Service (Port 8206)

```typescript
// Facility management endpoints
const WAREHOUSE_ENDPOINTS = {
  FACILITIES: '/api/v1/warehouses',
  FACILITY_DETAIL: '/api/v1/warehouses/{facilityId}',
  UNITS: '/api/v1/warehouses/{facilityId}/zones',
  UNIT_DETAIL: '/api/v1/warehouses/{facilityId}/zones/{unitId}',
  CAPACITY: '/api/v1/warehouses/{facilityId}/capacity',
  UTILIZATION: '/api/v1/warehouses/{facilityId}/utilization'
}

// Example usage
const facilities = await facilityApi.getFacilities()
const units = await facilityApi.getUnits(facilityId)
const capacity = await facilityApi.getCapacity(facilityId)
```

### Customer Storage Marketplace Service (Port 8230)

```typescript
// Customer marketplace endpoints
const MARKETPLACE_ENDPOINTS = {
  CUSTOMERS: '/marketplace/api/v1/customers',
  CUSTOMER_DETAIL: '/marketplace/api/v1/customers/{customerId}',
  RENTALS: '/marketplace/api/v1/customers/{customerId}/rentals',
  RESERVATIONS: '/marketplace/api/v1/reservations',
  PRICING: '/marketplace/api/v1/pricing'
}

// Example usage
const customers = await marketplaceApi.getCustomers()
const customerRentals = await marketplaceApi.getCustomerRentals(customerId)
const reservations = await marketplaceApi.getReservations()
```

### Billing Service (Port 8200)

```typescript
// Billing and financial endpoints
const BILLING_ENDPOINTS = {
  ACCOUNTS: '/api/v1/billing/accounts/{accountId}',
  INVOICES: '/api/v1/billing/invoices',
  PAYMENTS: '/api/v1/billing/payments',
  USAGE_REPORTS: '/api/v1/billing/usage-reports'
}

// Example usage
const account = await billingApi.getAccount(accountId)
const invoices = await billingApi.getInvoices()
const payments = await billingApi.getPayments()
```

## 🔒 Authentication Flow

### JWT Token Management

```typescript
// Authentication store implementation
export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('auth_token'))
  
  const login = async (credentials: LoginCredentials) => {
    const response = await authApi.login(credentials)
    token.value = response.token
    localStorage.setItem('auth_token', response.token)
    return response
  }

  const logout = async () => {
    await authApi.logout()
    token.value = null
    localStorage.removeItem('auth_token')
  }
})
```

### Request Interceptors

```typescript
// Axios request interceptor for token injection
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      await authStore.logout()
      router.push('/login')
    }
    return Promise.reject(error)
  }
)
```

## 📊 Data Models

### Facility Data Model

```typescript
interface Facility {
  id: string
  name: string
  address: {
    street: string
    city: string
    state: string
    zipCode: string
    country: string
  }
  phone: string
  email: string
  managerId: string
  totalUnits: number
  occupiedUnits: number
  occupancyRate: number
  monthlyRevenue: number
  rating: number
  amenities: string[]
  operatingHours: {
    [key: string]: {
      open: string
      close: string
      is24Hours: boolean
    }
  }
  createdAt: string
  updatedAt: string
}
```

### Customer Data Model

```typescript
interface Customer {
  id: string
  firstName: string
  lastName: string
  email: string
  phone: string
  address: {
    street: string
    city: string
    state: string
    zipCode: string
  }
  units: string[]
  totalMonthlyRent: number
  paymentMethod: {
    type: 'credit_card' | 'bank_transfer' | 'cash'
    lastFour?: string
    autopay: boolean
  }
  leaseStatus: 'active' | 'past_due' | 'ending_soon' | 'terminated'
  joinDate: string
  rating: number
  notes?: string
}
```

### Unit Data Model

```typescript
interface Unit {
  id: string
  number: string
  type: string
  size: {
    width: number
    height: number
    area: number
  }
  features: string[]
  monthlyRate: number
  isOccupied: boolean
  customerId?: string
  customer?: Customer
  leaseStartDate?: string
  leaseEndDate?: string
  accessCode?: string
  status: 'available' | 'occupied' | 'reserved' | 'maintenance'
  floor: number
  building: string
  zone: string
}
```

## 🚀 API Service Implementation

### Base API Configuration

```typescript
// services/api/index.ts
import axios from 'axios'

const api = axios.create({
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Environment-based URL configuration
const getBaseUrl = (service: string) => {
  const urls = {
    warehouse: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8206',
    auth: process.env.VUE_APP_AUTH_URL || 'http://localhost:8080',
    marketplace: process.env.VUE_APP_MARKETPLACE_URL || 'http://localhost:8230',
    billing: process.env.VUE_APP_BILLING_URL || 'http://localhost:8200'
  }
  return urls[service] || urls.warehouse
}
```

### Facility API Service

```typescript
// services/api/facility.ts
export const facilityApi = {
  async getCurrentFacility(): Promise<Facility> {
    const response = await api.get(`${getBaseUrl('warehouse')}/api/v1/warehouses/current`)
    return response.data
  },

  async getUnits(facilityId: string): Promise<Unit[]> {
    const response = await api.get(`${getBaseUrl('warehouse')}/api/v1/warehouses/${facilityId}/zones`)
    return response.data
  },

  async updateUnit(unitId: string, updates: Partial<Unit>): Promise<Unit> {
    const response = await api.put(`${getBaseUrl('warehouse')}/api/v1/units/${unitId}`, updates)
    return response.data
  },

  async getCapacity(facilityId: string): Promise<OccupancyData> {
    const response = await api.get(`${getBaseUrl('warehouse')}/api/v1/warehouses/${facilityId}/capacity`)
    return response.data
  }
}
```

### Customer API Service

```typescript
// services/api/customer.ts
export const customerApi = {
  async getCustomers(): Promise<Customer[]> {
    const response = await api.get(`${getBaseUrl('marketplace')}/marketplace/api/v1/customers`)
    return response.data
  },

  async getCustomer(customerId: string): Promise<Customer> {
    const response = await api.get(`${getBaseUrl('marketplace')}/marketplace/api/v1/customers/${customerId}`)
    return response.data
  },

  async updateCustomer(customerId: string, updates: Partial<Customer>): Promise<Customer> {
    const response = await api.put(`${getBaseUrl('marketplace')}/marketplace/api/v1/customers/${customerId}`, updates)
    return response.data
  },

  async getCustomerRentals(customerId: string): Promise<Rental[]> {
    const response = await api.get(`${getBaseUrl('marketplace')}/marketplace/api/v1/customers/${customerId}/rentals`)
    return response.data
  }
}
```

## ⚡ Real-time Updates

### WebSocket Integration

```typescript
// services/websocket.ts
export class WebSocketService {
  private ws: WebSocket | null = null
  private readonly url: string

  constructor() {
    this.url = process.env.VUE_APP_WEBSOCKET_URL || 'ws://localhost:8090/ws'
  }

  connect() {
    this.ws = new WebSocket(this.url)
    
    this.ws.onopen = () => {
      console.log('WebSocket connected')
    }
    
    this.ws.onmessage = (event) => {
      const data = JSON.parse(event.data)
      this.handleMessage(data)
    }
    
    this.ws.onclose = () => {
      console.log('WebSocket disconnected')
      // Implement reconnection logic
    }
  }

  private handleMessage(data: any) {
    switch (data.type) {
      case 'CUSTOMER_UPDATE':
        // Update customer data in store
        break
      case 'UNIT_STATUS_CHANGE':
        // Update unit availability
        break
      case 'PAYMENT_RECEIVED':
        // Update financial data
        break
    }
  }
}
```

## 🔧 Error Handling

### API Error Types

```typescript
// types/api.ts
export interface ApiError {
  code: string
  message: string
  details?: Record<string, any>
  timestamp: string
}

export interface ValidationError extends ApiError {
  field: string
  value: any
}

export interface AuthenticationError extends ApiError {
  reason: 'invalid_credentials' | 'token_expired' | 'insufficient_permissions'
}
```

### Error Handling Service

```typescript
// services/errorHandler.ts
export class ErrorHandler {
  static handle(error: any) {
    if (axios.isAxiosError(error)) {
      const status = error.response?.status
      const data = error.response?.data

      switch (status) {
        case 400:
          return this.handleValidationError(data)
        case 401:
          return this.handleAuthError(data)
        case 403:
          return this.handlePermissionError(data)
        case 404:
          return this.handleNotFoundError(data)
        case 500:
          return this.handleServerError(data)
        default:
          return this.handleGenericError(error)
      }
    }
    
    return this.handleGenericError(error)
  }

  private static handleValidationError(data: any) {
    // Show validation errors to user
    const notificationStore = useNotificationStore()
    notificationStore.showError('Please check your input and try again')
  }

  private static handleAuthError(data: any) {
    // Redirect to login
    const authStore = useAuthStore()
    authStore.logout()
    router.push('/login')
  }
}
```

## 📈 Performance Optimization

### API Caching Strategy

```typescript
// services/cache.ts
export class ApiCache {
  private cache = new Map<string, { data: any; timestamp: number; ttl: number }>()

  set(key: string, data: any, ttl: number = 300000) { // 5 minutes default
    this.cache.set(key, {
      data,
      timestamp: Date.now(),
      ttl
    })
  }

  get(key: string): any | null {
    const cached = this.cache.get(key)
    if (!cached) return null

    if (Date.now() - cached.timestamp > cached.ttl) {
      this.cache.delete(key)
      return null
    }

    return cached.data
  }

  invalidate(pattern: string) {
    for (const key of this.cache.keys()) {
      if (key.includes(pattern)) {
        this.cache.delete(key)
      }
    }
  }
}
```

### Request Deduplication

```typescript
// services/requestDeduplication.ts
export class RequestDeduplicator {
  private pendingRequests = new Map<string, Promise<any>>()

  async dedupe<T>(key: string, requestFn: () => Promise<T>): Promise<T> {
    if (this.pendingRequests.has(key)) {
      return this.pendingRequests.get(key)!
    }

    const promise = requestFn().finally(() => {
      this.pendingRequests.delete(key)
    })

    this.pendingRequests.set(key, promise)
    return promise
  }
}
```

## 🧪 Testing API Integration

### Mock API for Development

```typescript
// services/api/mock.ts
export const mockFacilityApi = {
  async getCurrentFacility(): Promise<Facility> {
    return {
      id: '1',
      name: 'SecureStore LA Downtown',
      address: {
        street: '1234 Storage Blvd',
        city: 'Los Angeles',
        state: 'CA',
        zipCode: '90210',
        country: 'USA'
      },
      // ... rest of mock data
    }
  },

  async getUnits(facilityId: string): Promise<Unit[]> {
    return [
      {
        id: '1',
        number: '5x5-A01',
        type: '5x5',
        size: { width: 5, height: 5, area: 25 },
        features: ['climate_controlled'],
        monthlyRate: 55,
        isOccupied: true,
        status: 'occupied',
        floor: 1,
        building: 'A',
        zone: 'A'
      },
      // ... more mock units
    ]
  }
}
```

### API Integration Tests

```typescript
// tests/api.test.ts
import { describe, it, expect, vi } from 'vitest'
import { facilityApi } from '@/services/api/facility'

describe('Facility API', () => {
  it('should fetch current facility', async () => {
    const mockResponse = { id: '1', name: 'Test Facility' }
    vi.spyOn(facilityApi, 'getCurrentFacility').mockResolvedValue(mockResponse)

    const facility = await facilityApi.getCurrentFacility()
    expect(facility).toEqual(mockResponse)
  })

  it('should handle API errors gracefully', async () => {
    vi.spyOn(facilityApi, 'getCurrentFacility').mockRejectedValue(new Error('API Error'))

    await expect(facilityApi.getCurrentFacility()).rejects.toThrow('API Error')
  })
})
```

## 📚 Additional Resources

- [Axios Documentation](https://axios-http.com/docs/intro)
- [Vue 3 Composition API](https://vuejs.org/api/composition-api-setup.html)
- [Pinia Store Management](https://pinia.vuejs.org/)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)

---

**Last Updated**: 2025-06-30  
**API Version**: v1.0.0  
**Maintained By**: Frontend Development Team