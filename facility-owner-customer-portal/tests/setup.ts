import { config } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import { createPinia } from 'pinia'
import { vi } from 'vitest'

// Create global Vuetify instance for tests
const vuetify = createVuetify()
const pinia = createPinia()

// Mock API calls to prevent hanging
vi.mock('@/services/api/auth', () => ({
  authApi: {
    login: vi.fn().mockResolvedValue({ token: 'mock-token', user: { id: '1', name: 'Test User' } }),
    logout: vi.fn().mockResolvedValue(undefined),
    getProfile: vi.fn().mockResolvedValue({ id: '1', name: 'Test User' }),
    updateProfile: vi.fn().mockResolvedValue({ id: '1', name: 'Updated User' }),
    changePassword: vi.fn().mockResolvedValue(undefined)
  }
}))

vi.mock('@/services/api/facility', () => ({
  facilityApi: {
    getCurrentFacility: vi.fn().mockResolvedValue({ id: '1', name: 'Test Facility' }),
    updateFacility: vi.fn().mockResolvedValue({ id: '1', name: 'Updated Facility' }),
    getFacilityMetrics: vi.fn().mockResolvedValue({ customers: 100, occupancy: 85 }),
    getFacilityUnits: vi.fn().mockResolvedValue([]),
    getFacilityCustomers: vi.fn().mockResolvedValue([]),
    getFacilityFinancials: vi.fn().mockResolvedValue({})
  }
}))

// Mock localStorage
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
}
vi.stubGlobal('localStorage', localStorageMock)

// Global test configuration
config.global.plugins = [vuetify, pinia]

// Mock console methods to reduce noise in tests
global.console = {
  ...console,
  warn: vi.fn(),
  error: vi.fn(),
}

// Mock window.matchMedia
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(), // deprecated
    removeListener: vi.fn(), // deprecated
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
})

// Mock IntersectionObserver
global.IntersectionObserver = class IntersectionObserver {
  constructor() {}
  disconnect() {}
  observe() {}
  unobserve() {}
}

// Mock ResizeObserver
global.ResizeObserver = class ResizeObserver {
  constructor() {}
  disconnect() {}
  observe() {}
  unobserve() {}
}