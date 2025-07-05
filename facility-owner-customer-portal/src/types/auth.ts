export interface User {
  id: string
  email: string
  name: string
  role: 'facility_owner' | 'manager' | 'staff'
  avatar?: string
  facilityId: string
  permissions: string[]
  createdAt: string
  updatedAt: string
}

export interface LoginCredentials {
  email: string
  password: string
}

export interface LoginResponse {
  user: User
  token: string
  refreshToken: string
  expiresIn: number
}

export interface AuthResponse {
  user: User
  token: string
  refreshToken?: string
  expiresIn?: number
}

export interface AuthError {
  message: string
  code: string
  details?: Record<string, any>
}