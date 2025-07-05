<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Financial Management</h1>
        
        <v-row class="mb-6">
          <v-col cols="12" sm="6" md="3">
            <MetricCard
              title="Monthly Revenue"
              :value="'$12,450'"
              icon="mdi-currency-usd"
              color="success"
            />
          </v-col>
          <v-col cols="12" sm="6" md="3">
            <MetricCard
              title="Outstanding"
              :value="'$850'"
              icon="mdi-clock-outline"
              color="warning"
            />
          </v-col>
          <v-col cols="12" sm="6" md="3">
            <MetricCard
              title="Occupancy Rate"
              :value="'87%'"
              icon="mdi-chart-line"
              color="info"
            />
          </v-col>
          <v-col cols="12" sm="6" md="3">
            <MetricCard
              title="Total Units"
              :value="'156'"
              icon="mdi-home-outline"
              color="primary"
            />
          </v-col>
        </v-row>
        
        <v-card>
          <v-card-title>
            Recent Transactions
          </v-card-title>
          
          <v-card-text>
            <v-data-table
              :headers="headers"
              :items="transactions"
              item-value="id"
              class="elevation-1"
            >
              <template v-slot:item.amount="{ item }">
                <span :class="item.type === 'payment' ? 'text-success' : 'text-error'">
                  {{ item.type === 'payment' ? '+' : '-' }}${{ item.amount }}
                </span>
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
import MetricCard from '@/components/MetricCard.vue'

const headers = [
  { title: 'Date', key: 'date' },
  { title: 'Customer', key: 'customer' },
  { title: 'Description', key: 'description' },
  { title: 'Amount', key: 'amount' },
  { title: 'Type', key: 'type' }
]

const transactions = ref([
  { id: 1, date: '2024-01-15', customer: 'John Doe', description: 'Monthly Rent - Unit A-101', amount: 120, type: 'payment' },
  { id: 2, date: '2024-01-14', customer: 'Jane Smith', description: 'Monthly Rent - Unit B-201', amount: 200, type: 'payment' },
  { id: 3, date: '2024-01-13', customer: 'Bob Johnson', description: 'Late Fee', amount: 25, type: 'charge' }
])
</script>