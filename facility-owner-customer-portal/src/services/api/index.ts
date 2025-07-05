import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

// Create axios instance with base configuration
const api = axios.create({
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    const token = authStore.token
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor to handle errors
api.interceptors.response.use(
  (response) => {
    return response
  },
  async (error) => {
    const authStore = useAuthStore()
    
    if (error.response?.status === 401) {
      // Token expired or invalid
      await authStore.logout()
      window.location.href = '/login'
    }
    
    return Promise.reject(error)
  }
)

export default api

// API endpoints configuration
export const API_ENDPOINTS = {
  // Auth service
  AUTH: {
    LOGIN: '/auth/api/v1/auth/login',
    LOGOUT: '/auth/api/v1/auth/logout',
    PROFILE: '/auth/api/v1/auth/profile',
    REFRESH: '/auth/api/v1/auth/refresh',
    CHANGE_PASSWORD: '/auth/api/v1/auth/change-password',
  },
  
  // Warehouse Management Service
  WAREHOUSE: {
    FACILITIES: '/api/v1/warehouses',
    UNITS: '/api/v1/warehouses/{facilityId}/zones',
    UNIT_DETAIL: '/api/v1/warehouses/{facilityId}/zones/{unitId}',
    CAPACITY: '/api/v1/warehouses/{facilityId}/capacity',
    UTILIZATION: '/api/v1/warehouses/{facilityId}/utilization',
  },
  
  // Customer Storage Marketplace Service  
  MARKETPLACE: {
    CUSTOMERS: '/marketplace/api/v1/customers',
    CUSTOMER_DETAIL: '/marketplace/api/v1/customers/{customerId}',
    RENTALS: '/marketplace/api/v1/customers/{customerId}/rentals',
    RESERVATIONS: '/marketplace/api/v1/reservations',
    PRICING: '/marketplace/api/v1/pricing',
  },
  
  // Billing Service
  BILLING: {
    ACCOUNTS: '/api/v1/billing/accounts/{accountId}',
    INVOICES: '/api/v1/billing/invoices',
    PAYMENTS: '/api/v1/billing/payments',
    USAGE_REPORTS: '/api/v1/billing/usage-reports',
  },
  
  // Analytics
  ANALYTICS: {
    OCCUPANCY: '/api/v1/analytics/occupancy',
    REVENUE: '/api/v1/analytics/revenue',
    CUSTOMER_METRICS: '/api/v1/analytics/customers',
    PERFORMANCE: '/api/v1/analytics/performance',
  },
  
  // Support
  SUPPORT: {
    TICKETS: '/marketplace/api/v1/support/tickets',
    TICKET_DETAIL: '/marketplace/api/v1/support/tickets/{ticketId}',
    COMMUNICATIONS: '/marketplace/api/v1/support/communications',
  },
  
  // Notifications
  NOTIFICATIONS: {
    LIST: '/api/v1/notifications',
    MARK_READ: '/api/v1/notifications/{notificationId}/read',
    MARK_ALL_READ: '/api/v1/notifications/read-all',
  }
}

// Helper function to replace path parameters
export const buildUrl = (endpoint: string, params: Record<string, string | number> = {}) => {
  let url = endpoint
  Object.entries(params).forEach(([key, value]) => {
    url = url.replace(`{${key}}`, String(value))
  })
  return url
}