<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-btn
          prepend-icon="mdi-arrow-left"
          variant="text"
          @click="$router.go(-1)"
          class="mb-4"
        >
          Back to Customers
        </v-btn>
        
        <h1 class="text-h4 mb-6">Customer Details</h1>
        
        <v-row>
          <v-col cols="12" md="8">
            <v-card class="mb-4">
              <v-card-title>Personal Information</v-card-title>
              <v-card-text>
                <v-row>
                  <v-col cols="6">
                    <strong>Name:</strong> {{ customer.name }}
                  </v-col>
                  <v-col cols="6">
                    <strong>Customer ID:</strong> {{ customer.id }}
                  </v-col>
                  <v-col cols="6">
                    <strong>Email:</strong> {{ customer.email }}
                  </v-col>
                  <v-col cols="6">
                    <strong>Phone:</strong> {{ customer.phone }}
                  </v-col>
                </v-row>
              </v-card-text>
            </v-card>
            
            <v-card>
              <v-card-title>Rental Units</v-card-title>
              <v-card-text>
                <v-data-table
                  :headers="unitHeaders"
                  :items="customer.units"
                  item-value="id"
                />
              </v-card-text>
            </v-card>
          </v-col>
          
          <v-col cols="12" md="4">
            <v-card>
              <v-card-title>Account Summary</v-card-title>
              <v-card-text>
                <div class="text-center">
                  <div class="text-h3 text-primary mb-2">${{ customer.totalMonthlyRent }}</div>
                  <div class="text-subtitle-1">Monthly Rent</div>
                </div>
                <v-divider class="my-4" />
                <div class="d-flex justify-space-between">
                  <span>Units Rented:</span>
                  <span>{{ customer.units.length }}</span>
                </div>
                <div class="d-flex justify-space-between">
                  <span>Account Status:</span>
                  <v-chip color="success" size="small">Active</v-chip>
                </div>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const customerId = route.params.id as string

const unitHeaders = [
  { title: 'Unit ID', key: 'id' },
  { title: 'Size', key: 'size' },
  { title: 'Type', key: 'type' },
  { title: 'Monthly Rate', key: 'rate' }
]

const customer = ref({
  id: customerId,
  name: 'John Doe',
  email: 'john@example.com',
  phone: '(555) 123-4567',
  units: [
    { id: 'A-101', size: '5x5', type: 'Climate Controlled', rate: 120 }
  ],
  totalMonthlyRent: 120
})
</script>