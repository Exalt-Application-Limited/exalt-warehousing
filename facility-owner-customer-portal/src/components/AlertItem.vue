<template>
  <v-list-item
    :prepend-icon="getIcon(alert.type)"
    :class="`alert-${alert.type}`"
    class="mb-2 border rounded"
  >
    <v-list-item-title class="font-weight-medium">
      {{ alert.title }}
    </v-list-item-title>
    
    <v-list-item-subtitle class="mt-1">
      {{ alert.message }}
    </v-list-item-subtitle>
    
    <template v-slot:append>
      <div class="d-flex flex-column align-center">
        <v-chip
          :color="getColor(alert.type)"
          size="small"
          class="mb-1"
        >
          {{ alert.type.toUpperCase() }}
        </v-chip>
        
        <span class="text-caption text-medium-emphasis">
          {{ formatTime(alert.timestamp) }}
        </span>
      </div>
    </template>
  </v-list-item>
</template>

<script setup lang="ts">
import { format } from 'date-fns'

interface Alert {
  id: string
  type: 'info' | 'warning' | 'error' | 'success'
  title: string
  message: string
  timestamp: Date
}

defineProps<{
  alert: Alert
}>()

const getIcon = (type: string) => {
  switch (type) {
    case 'error': return 'mdi-alert-circle'
    case 'warning': return 'mdi-alert'
    case 'success': return 'mdi-check-circle'
    case 'info': return 'mdi-information'
    default: return 'mdi-information'
  }
}

const getColor = (type: string) => {
  switch (type) {
    case 'error': return 'error'
    case 'warning': return 'warning'
    case 'success': return 'success'
    case 'info': return 'info'
    default: return 'info'
  }
}

const formatTime = (date: Date) => {
  return format(date, 'MMM dd, HH:mm')
}
</script>

<style scoped>
.alert-error {
  border-left: 4px solid rgb(var(--v-theme-error));
}

.alert-warning {
  border-left: 4px solid rgb(var(--v-theme-warning));
}

.alert-success {
  border-left: 4px solid rgb(var(--v-theme-success));
}

.alert-info {
  border-left: 4px solid rgb(var(--v-theme-info));
}
</style>