<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Storage Units Management</h1>
        
        <v-card>
          <v-card-title>
            Units Overview
          </v-card-title>
          
          <v-card-text>
            <v-data-table
              :headers="headers"
              :items="units"
              item-value="id"
              class="elevation-1"
            >
              <template v-slot:item.status="{ item }">
                <v-chip
                  :color="getStatusColor(item.status)"
                  small
                >
                  {{ item.status }}
                </v-chip>
              </template>
            </v-data-table>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const headers = [
  { title: 'Unit ID', key: 'id' },
  { title: 'Size', key: 'size' },
  { title: 'Type', key: 'type' },
  { title: 'Status', key: 'status' },
  { title: 'Customer', key: 'customer' },
  { title: 'Monthly Rate', key: 'rate' }
]

const units = ref([
  { id: 'A-101', size: '5x5', type: 'Climate Controlled', status: 'Occupied', customer: 'John Doe', rate: '$120' },
  { id: 'A-102', size: '5x10', type: 'Standard', status: 'Available', customer: '', rate: '$80' },
  { id: 'B-201', size: '10x10', type: 'Climate Controlled', status: 'Occupied', customer: 'Jane Smith', rate: '$200' }
])

const getStatusColor = (status: string) => {
  switch (status) {
    case 'Available': return 'success'
    case 'Occupied': return 'primary'
    case 'Maintenance': return 'warning'
    default: return 'grey'
  }
}
</script>