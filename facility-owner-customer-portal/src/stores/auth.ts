import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/services/api/auth'
import type { User, LoginCredentials } from '@/types/auth'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(localStorage.getItem('auth_token'))
  const loading = ref(false)
  const initialized = ref(false)

  const isAuthenticated = computed(() => !!token.value && !!user.value)

  const login = async (credentials: LoginCredentials) => {
    loading.value = true
    try {
      const response = await authApi.login(credentials)
      
      token.value = response.token
      user.value = response.user
      
      localStorage.setItem('auth_token', response.token)
      
      return response
    } catch (error) {
      console.error('Login error:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const logout = async () => {
    loading.value = true
    try {
      if (token.value) {
        await authApi.logout()
      }
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      user.value = null
      token.value = null
      localStorage.removeItem('auth_token')
      loading.value = false
    }
  }

  const initialize = async () => {
    if (initialized.value) return

    const storedToken = localStorage.getItem('auth_token')
    if (storedToken) {
      token.value = storedToken
      try {
        loading.value = true
        const userProfile = await authApi.getProfile()
        user.value = userProfile
      } catch (error) {
        console.error('Failed to get user profile:', error)
        // Token might be invalid, clear it
        token.value = null
        localStorage.removeItem('auth_token')
      } finally {
        loading.value = false
      }
    }
    
    initialized.value = true
  }

  const updateProfile = async (updates: Partial<User>) => {
    if (!user.value) throw new Error('No user logged in')
    
    loading.value = true
    try {
      const updatedUser = await authApi.updateProfile(updates)
      user.value = { ...user.value, ...updatedUser }
      return updatedUser
    } catch (error) {
      console.error('Profile update error:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const changePassword = async (currentPassword: string, newPassword: string) => {
    loading.value = true
    try {
      await authApi.changePassword(currentPassword, newPassword)
    } catch (error) {
      console.error('Password change error:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    user: computed(() => user.value),
    token: computed(() => token.value),
    loading: computed(() => loading.value),
    initialized: computed(() => initialized.value),
    isAuthenticated,
    login,
    logout,
    initialize,
    updateProfile,
    changePassword
  }
})