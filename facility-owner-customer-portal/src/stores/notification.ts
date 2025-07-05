import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface Notification {
  id: string
  title: string
  message: string
  type: 'info' | 'success' | 'warning' | 'error'
  read: boolean
  timestamp: Date
}

export interface SnackbarState {
  show: boolean
  message: string
  color: string
  timeout: number
}

export const useNotificationStore = defineStore('notification', () => {
  const notifications = ref<Notification[]>([])
  const snackbar = ref<SnackbarState>({
    show: false,
    message: '',
    color: 'info',
    timeout: 4000
  })

  const unreadCount = computed(() => 
    notifications.value.filter(n => !n.read).length
  )

  const unreadNotifications = computed(() => 
    notifications.value.filter(n => !n.read)
  )

  const loadNotifications = async () => {
    // Mock notifications for development
    notifications.value = [
      {
        id: '1',
        title: 'New Rental',
        message: 'Unit A-105 has been rented by John Doe',
        type: 'success',
        read: false,
        timestamp: new Date(Date.now() - 1000 * 60 * 30) // 30 minutes ago
      },
      {
        id: '2',
        title: 'Payment Received',
        message: 'Payment of $120 received from Jane Smith',
        type: 'success',
        read: false,
        timestamp: new Date(Date.now() - 1000 * 60 * 60 * 2) // 2 hours ago
      },
      {
        id: '3',
        title: 'Maintenance Required',
        message: 'Unit B-203 requires attention',
        type: 'warning',
        read: true,
        timestamp: new Date(Date.now() - 1000 * 60 * 60 * 24) // 1 day ago
      }
    ]
  }

  const markAsRead = (notificationId: string) => {
    const notification = notifications.value.find(n => n.id === notificationId)
    if (notification) {
      notification.read = true
    }
  }

  const markAllAsRead = () => {
    notifications.value.forEach(n => n.read = true)
  }

  const showSnackbar = (message: string, color: string = 'info', timeout: number = 4000) => {
    snackbar.value = {
      show: true,
      message,
      color,
      timeout
    }
  }

  const hideSnackbar = () => {
    snackbar.value.show = false
  }

  const addNotification = (notification: Omit<Notification, 'id' | 'timestamp'>) => {
    const newNotification: Notification = {
      ...notification,
      id: Date.now().toString(),
      timestamp: new Date()
    }
    notifications.value.unshift(newNotification)
  }

  const removeNotification = (notificationId: string) => {
    const index = notifications.value.findIndex(n => n.id === notificationId)
    if (index > -1) {
      notifications.value.splice(index, 1)
    }
  }

  return {
    notifications: computed(() => notifications.value),
    snackbar: computed(() => snackbar.value),
    unreadCount,
    unreadNotifications,
    loadNotifications,
    markAsRead,
    markAllAsRead,
    showSnackbar,
    hideSnackbar,
    addNotification,
    removeNotification
  }
})