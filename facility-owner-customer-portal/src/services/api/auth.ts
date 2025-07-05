import axios from 'axios'
import type { LoginCredentials, User, AuthResponse } from '@/types/auth'

const API_BASE_URL = '/auth'

const authClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Add auth token to requests
authClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const authApi = {
  async login(credentials: LoginCredentials): Promise<AuthResponse> {
    const response = await authClient.post('/login', credentials)
    return response.data
  },

  async logout(): Promise<void> {
    await authClient.post('/logout')
  },

  async getProfile(): Promise<User> {
    const response = await authClient.get('/profile')
    return response.data
  },

  async updateProfile(updates: Partial<User>): Promise<User> {
    const response = await authClient.put('/profile', updates)
    return response.data
  },

  async changePassword(currentPassword: string, newPassword: string): Promise<void> {
    await authClient.put('/password', {
      currentPassword,
      newPassword
    })
  },

  async refreshToken(): Promise<string> {
    const response = await authClient.post('/refresh')
    return response.data.token
  },

  async forgotPassword(email: string): Promise<void> {
    await authClient.post('/forgot-password', { email })
  },

  async resetPassword(token: string, newPassword: string): Promise<void> {
    await authClient.post('/reset-password', {
      token,
      newPassword
    })
  }
}