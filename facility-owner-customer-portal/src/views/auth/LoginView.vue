<template>
  <v-container fluid class="fill-height pa-0">
    <v-row no-gutters class="fill-height">
      <v-col cols="12" md="6" class="d-flex align-center justify-center">
        <v-card class="pa-8" max-width="400" width="100%" elevation="2">
          <v-card-title class="text-center mb-4">
            <h1 class="text-h4">Facility Login</h1>
          </v-card-title>
          
          <v-form @submit.prevent="handleLogin">
            <v-text-field
              v-model="email"
              label="Email"
              type="email"
              variant="outlined"
              required
              class="mb-3"
              data-testid="email-input"
            />
            
            <v-text-field
              v-model="password"
              label="Password"
              type="password"
              variant="outlined"
              required
              class="mb-4"
              data-testid="password-input"
            />
            
            <v-btn
              type="submit"
              color="primary"
              size="large"
              block
              :loading="loading"
              data-testid="login-button"
            >
              Sign In
            </v-btn>
          </v-form>
          
          <div class="text-center mt-4">
            <router-link to="/forgot-password" class="text-decoration-none">
              Forgot Password?
            </router-link>
          </div>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="6" class="d-none d-md-flex align-center justify-center bg-primary">
        <div class="text-center text-white">
          <h2 class="text-h3 mb-4">Welcome Back</h2>
          <p class="text-h6">Manage your storage facility with ease</p>
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const loading = ref(false)

const handleLogin = async () => {
  loading.value = true
  try {
    await authStore.login(email.value, password.value)
    router.push('/dashboard')
  } catch (error) {
    console.error('Login failed:', error)
  } finally {
    loading.value = false
  }
}
</script>