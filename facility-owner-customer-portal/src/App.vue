<template>
  <v-app>
    <v-navigation-drawer
      v-model="drawer"
      :rail="rail"
      permanent
      @click="rail = false"
    >
      <v-list-item
        prepend-avatar="/logo.png"
        :title="facility?.name || 'Exalt Storage'"
        nav
      >
        <template v-slot:append>
          <v-btn
            variant="text"
            icon="mdi-chevron-left"
            @click.stop="rail = !rail"
          ></v-btn>
        </template>
      </v-list-item>

      <v-divider></v-divider>

      <v-list density="compact" nav>
        <v-list-item
          v-for="item in menuItems"
          :key="item.value"
          :prepend-icon="item.icon"
          :title="item.title"
          :value="item.value"
          :to="item.route"
          color="primary"
        ></v-list-item>
      </v-list>
    </v-navigation-drawer>

    <v-app-bar>
      <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
      
      <v-toolbar-title>Facility Owner Portal</v-toolbar-title>

      <v-spacer></v-spacer>

      <v-btn icon="mdi-bell" size="small" variant="text">
        <v-icon>mdi-bell</v-icon>
        <v-badge
          v-if="notifications > 0"
          :content="notifications"
          color="error"
        ></v-badge>
      </v-btn>

      <v-menu>
        <template v-slot:activator="{ props }">
          <v-btn
            v-bind="props"
            icon
            size="small"
            variant="text"
          >
            <v-avatar size="32">
              <v-img
                :src="user?.avatar || '/default-avatar.png'"
                :alt="user?.name"
              ></v-img>
            </v-avatar>
          </v-btn>
        </template>
        <v-list>
          <v-list-item>
            <v-list-item-title>{{ user?.name }}</v-list-item-title>
            <v-list-item-subtitle>{{ user?.email }}</v-list-item-subtitle>
          </v-list-item>
          <v-divider></v-divider>
          <v-list-item @click="logout">
            <v-list-item-title>Logout</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-app-bar>

    <v-main>
      <v-container fluid>
        <router-view />
      </v-container>
    </v-main>

    <!-- Loading overlay -->
    <v-overlay v-model="loading" class="align-center justify-center">
      <v-progress-circular
        color="primary"
        size="64"
        indeterminate
      ></v-progress-circular>
    </v-overlay>

    <!-- Snackbar for notifications -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="snackbar.timeout"
    >
      {{ snackbar.message }}
      <template v-slot:actions>
        <v-btn
          color="white"
          variant="text"
          @click="snackbar.show = false"
        >
          Close
        </v-btn>
      </template>
    </v-snackbar>
  </v-app>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useFacilityStore } from '@/stores/facility'
import { useNotificationStore } from '@/stores/notification'

const router = useRouter()
const authStore = useAuthStore()
const facilityStore = useFacilityStore()
const notificationStore = useNotificationStore()

const drawer = ref(true)
const rail = ref(false)

const user = computed(() => authStore.user)
const facility = computed(() => facilityStore.currentFacility)
const loading = computed(() => authStore.loading || facilityStore.loading)
const notifications = computed(() => notificationStore.unreadCount)
const snackbar = computed(() => notificationStore.snackbar)

const menuItems = [
  {
    title: 'Dashboard',
    value: 'dashboard',
    icon: 'mdi-view-dashboard',
    route: '/dashboard'
  },
  {
    title: 'Units & Occupancy',
    value: 'units',
    icon: 'mdi-home-variant',
    route: '/units'
  },
  {
    title: 'Customers',
    value: 'customers',
    icon: 'mdi-account-group',
    route: '/customers'
  },
  {
    title: 'Financial Reports',
    value: 'financial',
    icon: 'mdi-chart-line',
    route: '/financial'
  },
  {
    title: 'Support Tickets',
    value: 'support',
    icon: 'mdi-help-circle',
    route: '/support'
  },
  {
    title: 'Settings',
    value: 'settings',
    icon: 'mdi-cog',
    route: '/settings'
  }
]

const logout = async () => {
  await authStore.logout()
  router.push('/login')
}

onMounted(async () => {
  await authStore.initialize()
  if (authStore.isAuthenticated) {
    await facilityStore.loadCurrentFacility()
    await notificationStore.loadNotifications()
  }
})
</script>

<style scoped>
.v-application {
  font-family: 'Roboto', sans-serif;
}
</style>