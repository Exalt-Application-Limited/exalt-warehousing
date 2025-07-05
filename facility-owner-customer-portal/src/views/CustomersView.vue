<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Customer Management</h1>
        
        <v-card>
          <v-card-title>
            Customer Directory
          </v-card-title>
          
          <v-card-text>
            <v-data-table
              :headers="headers"
              :items="customers"
              item-value="id"
              class="elevation-1"
            >
              <template v-slot:item.actions="{ item }">
                <v-btn
                  size="small"
                  color="primary"
                  @click="viewCustomer(item.id)"
                >
                  View Details
                </v-btn>
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
import { useRouter } from 'vue-router'

const router = useRouter()

const headers = [
  { title: 'Customer ID', key: 'id' },
  { title: 'Name', key: 'name' },
  { title: 'Email', key: 'email' },
  { title: 'Phone', key: 'phone' },
  { title: 'Units', key: 'unitCount' },
  { title: 'Actions', key: 'actions', sortable: false }
]

const customers = ref([
  { id: 'CUST-001', name: 'John Doe', email: 'john@example.com', phone: '(555) 123-4567', unitCount: 1 },
  { id: 'CUST-002', name: 'Jane Smith', email: 'jane@example.com', phone: '(555) 987-6543', unitCount: 2 },
  { id: 'CUST-003', name: 'Bob Johnson', email: 'bob@example.com', phone: '(555) 456-7890', unitCount: 1 }
])

const viewCustomer = (customerId: string) => {
  router.push(`/customers/${customerId}`)
}
</script>