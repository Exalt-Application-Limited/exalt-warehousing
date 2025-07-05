import axios from 'axios'
import type { Facility, FacilityMetrics } from '@/types/facility'

const API_BASE_URL = '/api'

const facilityClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Add auth token to requests
facilityClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const facilityApi: any = {
  async getCurrentFacility(): Promise<Facility> {
    const response = await facilityClient.get('/facility')
    return response.data
  },

  async updateFacility(updates: Partial<Facility>): Promise<Facility> {
    const response = await facilityClient.put('/facility', updates)
    return response.data
  },

  async getFacilityMetrics(): Promise<FacilityMetrics> {
    const response = await facilityClient.get('/facility/metrics')
    return response.data
  },

  async getFacilityUnits(): Promise<any[]> {
    const response = await facilityClient.get('/facility/units')
    return response.data
  },

  async getUnits(): Promise<any[]> {
    const response = await facilityClient.get('/facility/units')
    return response.data
  },

  async getFacilityCustomers(): Promise<any[]> {
    const response = await facilityClient.get('/facility/customers')
    return response.data
  },

  async getCustomers(): Promise<any[]> {
    const response = await facilityClient.get('/facility/customers')
    return response.data
  },

  async getFacilityFinancials(): Promise<any> {
    const response = await facilityClient.get('/facility/financials')
    return response.data
  },

  async getOccupancyData(): Promise<any> {
    const response = await facilityClient.get('/facility/occupancy')
    return response.data
  },

  async updateUnit(unitId: string, updates: any): Promise<any> {
    const response = await facilityClient.put(`/facility/units/${unitId}`, updates)
    return response.data
  },

  async updateCustomer(customerId: string, updates: any): Promise<any> {
    const response = await facilityClient.put(`/facility/customers/${customerId}`, updates)
    return response.data
  }
}