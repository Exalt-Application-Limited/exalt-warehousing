import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/dashboard'
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/auth/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: () => import('@/views/DashboardView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/units',
      name: 'Units',
      component: () => import('@/views/UnitsView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/customers',
      name: 'Customers',
      component: () => import('@/views/CustomersView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/customers/:id',
      name: 'CustomerDetail',
      component: () => import('@/views/CustomerDetailView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/financial',
      name: 'Financial',
      component: () => import('@/views/FinancialView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/support',
      name: 'Support',
      component: () => import('@/views/SupportView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/settings',
      name: 'Settings',
      component: () => import('@/views/SettingsView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('@/views/NotFoundView.vue')
    }
  ]
})

// Navigation guard
router.beforeEach(async (to, _from, next) => {
  const authStore = useAuthStore()
  
  // Initialize auth store if not already done
  if (!authStore.initialized) {
    await authStore.initialize()
  }

  const requiresAuth = to.matched.some(record => record.meta.requiresAuth !== false)
  
  if (requiresAuth && !authStore.isAuthenticated) {
    // Redirect to login if authentication is required
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else if (to.name === 'Login' && authStore.isAuthenticated) {
    // Redirect to dashboard if already authenticated and trying to access login
    next({ name: 'Dashboard' })
  } else {
    next()
  }
})

export default router