<template>
  <v-card
    :class="[`text-${color}`]"
    class="metric-card"
    elevation="2"
    hover
  >
    <v-card-text>
      <div class="d-flex justify-space-between align-center">
        <div>
          <div class="text-h6 font-weight-medium">{{ title }}</div>
          <div class="text-h4 font-weight-bold mt-2">{{ value }}</div>
          <div 
            v-if="change"
            :class="[changeColor]"
            class="text-body-2 mt-1 font-weight-medium"
          >
            {{ change }}
          </div>
        </div>
        <v-avatar
          :color="color"
          size="56"
          class="elevation-1"
        >
          <v-icon
            :icon="icon"
            color="white"
            size="24"
          />
        </v-avatar>
      </div>
    </v-card-text>
  </v-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  title: string
  value: string | number
  change?: string
  icon: string
  color?: string
}

const props = withDefaults(defineProps<Props>(), {
  color: 'primary',
  change: undefined
})

const changeColor = computed(() => {
  if (!props.change) return ''
  
  if (props.change.startsWith('+')) {
    return 'text-success'
  } else if (props.change.startsWith('-')) {
    return 'text-error'
  }
  return 'text-info'
})
</script>

<style scoped>
.metric-card {
  transition: transform 0.2s ease-in-out;
}

.metric-card:hover {
  transform: translateY(-2px);
}
</style>