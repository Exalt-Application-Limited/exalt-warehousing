<template>
  <div>
    <!-- Header -->
    <v-row class="mb-6">
      <v-col>
        <h1 class="text-h4 font-weight-bold">
          Customer Operations Dashboard
        </h1>
        <p class="text-subtitle-1 text-medium-emphasis">
          {{ formatDate(new Date()) }}
        </p>
      </v-col>
    </v-row>

    <!-- Key Metrics Cards -->
    <v-row class="mb-6">
      <v-col cols="12" sm="6" md="2.4">
        <MetricCard
          title="Customers"
          :value="customerCount"
          :change="customerGrowth"
          icon="mdi-account-group"
          color="primary"
        />
      </v-col>
      <v-col cols="12" sm="6" md="2.4">
        <MetricCard
          title="Occupancy"
          :value="`${occupancyRate.toFixed(1)}%`"
          :change="occupancyChange"
          icon="mdi-home-variant"
          color="success"
        />
      </v-col>
      <v-col cols="12" sm="6" md="2.4">
        <MetricCard
          title="Revenue"
          :value="formatCurrency(monthlyRevenue)"
          :change="revenueGrowth"
          icon="mdi-currency-usd"
          color="info"
        />
      </v-col>
      <v-col cols="12" sm="6" md="2.4">
        <MetricCard
          title="Rating"
          :value="`${facilityRating} ★`"
          :change="ratingChange"
          icon="mdi-star"
          color="warning"
        />
      </v-col>
      <v-col cols="12" sm="6" md="2.4">
        <MetricCard
          title="Growth"
          :value="`+${growthRate}%`"
          :change="growthTrend"
          icon="mdi-trending-up"
          color="purple"
        />
      </v-col>
    </v-row>

    <!-- Main Content Grid -->
    <v-row>
      <!-- Unit Occupancy Status -->
      <v-col cols="12" lg="6">
        <v-card class="h-100">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">mdi-home-variant</v-icon>
            Unit Occupancy Status
          </v-card-title>
          <v-card-text>
            <!-- Floor Plan Visualization -->
            <div class="mb-4">
              <v-img
                src="/floorplan-placeholder.png"
                height="200"
                cover
                class="rounded"
              >
                <template v-slot:placeholder>
                  <div class="d-flex align-center justify-center fill-height">
                    <v-icon size="64" color="grey-lighten-1">
                      mdi-floor-plan
                    </v-icon>
                  </div>
                </template>
              </v-img>
            </div>

            <!-- Unit Type Breakdown -->
            <div class="mb-4">
              <h4 class="mb-3">Unit Types</h4>
              <div
                v-for="unitType in unitTypes"
                :key="unitType.type"
                class="d-flex justify-space-between align-center mb-2"
              >
                <span>{{ unitType.type }}</span>
                <div class="d-flex align-center">
                  <v-progress-linear
                    :model-value="unitType.occupancyRate"
                    :color="getOccupancyColor(unitType.occupancyRate)"
                    height="8"
                    rounded
                    class="mr-3"
                    style="width: 100px"
                  ></v-progress-linear>
                  <span class="text-caption">
                    {{ unitType.occupied }}/{{ unitType.total }} ({{ unitType.occupancyRate }}%)
                  </span>
                </div>
              </div>
            </div>

            <!-- Quick Stats -->
            <v-row dense>
              <v-col cols="6">
                <div class="text-center">
                  <div class="text-h6 text-primary">{{ waitlistCount }}</div>
                  <div class="text-caption">Waitlist</div>
                </div>
              </v-col>
              <v-col cols="6">
                <div class="text-center">
                  <div class="text-h6 text-info">{{ averageStay }}</div>
                  <div class="text-caption">Avg. Stay</div>
                </div>
              </v-col>
            </v-row>
          </v-card-text>
          <v-card-actions>
            <v-btn
              variant="text"
              color="primary"
              to="/units"
            >
              Detailed Occupancy
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>

      <!-- Customer Activity -->
      <v-col cols="12" lg="6">
        <v-card class="h-100">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">mdi-account-clock</v-icon>
            Customer Activity
          </v-card-title>
          <v-card-text>
            <div class="mb-4">
              <v-row dense>
                <v-col cols="4">
                  <div class="text-center">
                    <div class="text-h6 text-success">{{ recentSignups }}</div>
                    <div class="text-caption">Recent Sign-ups</div>
                  </div>
                </v-col>
                <v-col cols="4">
                  <div class="text-center">
                    <div class="text-h6 text-warning">{{ scheduledMoveouts }}</div>
                    <div class="text-caption">Move-outs This Week</div>
                  </div>
                </v-col>
                <v-col cols="4">
                  <div class="text-center">
                    <div class="text-h6 text-error">{{ paymentReminders }}</div>
                    <div class="text-caption">Payment Reminders</div>
                  </div>
                </v-col>
              </v-row>
            </div>

            <!-- Top Customers -->
            <h4 class="mb-3">Top Customers</h4>
            <div
              v-for="customer in topCustomers"
              :key="customer.id"
              class="d-flex align-center mb-3 pa-2 rounded"
              style="background-color: rgba(0,0,0,0.02)"
            >
              <v-avatar size="32" class="mr-3">
                <v-img
                  :src="customer.avatar || '/default-avatar.png'"
                  :alt="customer.name"
                ></v-img>
              </v-avatar>
              <div class="flex-grow-1">
                <div class="font-weight-medium">{{ customer.name }}</div>
                <div class="text-caption text-medium-emphasis">
                  {{ customer.duration }} • {{ formatRating(customer.rating) }}
                </div>
              </div>
              <v-chip size="small" variant="outlined">
                {{ customer.units }} units
              </v-chip>
            </div>
          </v-card-text>
          <v-card-actions>
            <v-btn
              variant="text"
              color="primary"
              to="/customers"
            >
              Manage Customers
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

    <!-- Second Row -->
    <v-row class="mt-4">
      <!-- Financial Performance -->
      <v-col cols="12" lg="8">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">mdi-chart-line</v-icon>
            Financial Performance
          </v-card-title>
          <v-card-text>
            <!-- Revenue Chart -->
            <div class="mb-4">
              <canvas ref="revenueChart" height="120"></canvas>
            </div>

            <!-- Revenue Breakdown -->
            <v-row dense>
              <v-col cols="12" md="8">
                <h4 class="mb-3">Revenue Breakdown</h4>
                <div
                  v-for="item in revenueBreakdown"
                  :key="item.category"
                  class="d-flex justify-space-between align-center mb-2"
                >
                  <span>{{ item.category }}</span>
                  <span class="font-weight-medium">{{ formatCurrency(item.amount) }}</span>
                </div>
              </v-col>
              <v-col cols="12" md="4">
                <div class="text-center">
                  <div class="text-h5 text-success mb-1">{{ collectionRate }}%</div>
                  <div class="text-caption">Collection Rate</div>
                  <div class="text-h6 text-error mt-3">{{ formatCurrency(outstandingBalance) }}</div>
                  <div class="text-caption">Outstanding</div>
                </div>
              </v-col>
            </v-row>
          </v-card-text>
          <v-card-actions>
            <v-btn
              variant="text"
              color="primary"
              to="/financial"
            >
              Financial Details
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>

      <!-- Customer Satisfaction -->
      <v-col cols="12" lg="4">
        <v-card class="h-100">
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">mdi-heart</v-icon>
            Customer Satisfaction
          </v-card-title>
          <v-card-text>
            <div class="text-center mb-4">
              <div class="text-h3 text-warning">{{ overallRating }}</div>
              <div class="text-subtitle-1">Overall Rating</div>
              <v-rating
                :model-value="overallRating"
                readonly
                half-increments
                color="warning"
                size="small"
              ></v-rating>
            </div>

            <!-- Review Breakdown -->
            <h4 class="mb-3">Review Breakdown</h4>
            <div
              v-for="review in reviewBreakdown"
              :key="review.stars"
              class="d-flex align-center mb-2"
            >
              <span class="mr-2">{{ review.stars }} stars</span>
              <v-progress-linear
                :model-value="review.percentage"
                color="warning"
                height="8"
                rounded
                class="flex-grow-1 mr-2"
              ></v-progress-linear>
              <span class="text-caption">{{ review.count }} ({{ review.percentage }}%)</span>
            </div>

            <!-- Common Feedback -->
            <h4 class="mb-3 mt-4">Common Feedback</h4>
            <div
              v-for="feedback in commonFeedback"
              :key="feedback.text"
              class="d-flex justify-space-between align-center mb-1"
            >
              <span class="text-caption">{{ feedback.text }}</span>
              <span class="text-caption text-medium-emphasis">({{ feedback.percentage }}%)</span>
            </div>
          </v-card-text>
          <v-card-actions>
            <v-btn
              variant="text"
              color="primary"
              @click="openReviewsDialog"
            >
              Read All Reviews
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

    <!-- Customer Alerts & Actions -->
    <v-row class="mt-4">
      <v-col cols="12">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="mr-2">mdi-alert-circle</v-icon>
            Customer Alerts & Actions
          </v-card-title>
          <v-card-text>
            <AlertItem
              v-for="alert in customerAlerts"
              :key="alert.id"
              :alert="alert"
              @action="handleAlertAction"
            />
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useFacilityStore } from '@/stores/facility'
import { format } from 'date-fns'
import MetricCard from '@/components/MetricCard.vue'
import AlertItem from '@/components/AlertItem.vue'

const facilityStore = useFacilityStore()

// Computed properties from store
const customerCount = computed(() => facilityStore.customers.length)
const occupancyRate = computed(() => facilityStore.occupancyRate)
const monthlyRevenue = computed(() => facilityStore.monthlyRevenue)

// Mock data (replace with API calls)
const customerGrowth = ref('+12')
const occupancyChange = ref('+3.2%')
const revenueGrowth = ref('+$2,340')
const facilityRating = ref(4.8)
const ratingChange = ref('+0.1★')
const growthRate = ref(15.3)
const growthTrend = ref('+2.1%')

const unitTypes = ref([
  { type: '5x5 Small', total: 28, occupied: 24, occupancyRate: 86 },
  { type: '10x10 Medium', total: 52, occupied: 45, occupancyRate: 87 },
  { type: '10x15 Large', total: 24, occupied: 18, occupancyRate: 75 },
  { type: '10x20 XL', total: 32, occupied: 28, occupancyRate: 88 },
  { type: 'Climate Controlled', total: 95, occupied: 89, occupancyRate: 94 }
])

const waitlistCount = ref(12)
const averageStay = ref('8.3 months')
const recentSignups = ref(5)
const scheduledMoveouts = ref(3)
const paymentReminders = ref(8)

const topCustomers = ref([
  { id: '1', name: 'Sarah M.', duration: '18 months', rating: 4.9, units: 1, avatar: null },
  { id: '2', name: 'Mike R.', duration: '12 months', rating: 5.0, units: 2, avatar: null },
  { id: '3', name: 'Business Corp', duration: '24 months', rating: 4.7, units: 5, avatar: null }
])

const revenueBreakdown = ref([
  { category: 'Monthly rent', amount: 26890 },
  { category: 'Late fees', amount: 345 },
  { category: 'Insurance', amount: 512 },
  { category: 'Admin fees', amount: 703 }
])

const collectionRate = ref(98.2)
const outstandingBalance = ref(435)

const overallRating = ref(4.8)
const reviewBreakdown = ref([
  { stars: 5, count: 156, percentage: 67 },
  { stars: 4, count: 58, percentage: 25 },
  { stars: 3, count: 15, percentage: 6 },
  { stars: 2, count: 3, percentage: 1 },
  { stars: 1, count: 2, percentage: 1 }
])

const commonFeedback = ref([
  { text: 'Great security', percentage: 89 },
  { text: 'Clean facility', percentage: 85 },
  { text: 'Helpful staff', percentage: 78 },
  { text: 'Good value', percentage: 72 }
])

const customerAlerts = ref([
  {
    id: '1',
    type: 'urgent',
    title: 'Payment overdue - Sarah M, Unit 10x10-B23 (5 days late)',
    actions: ['CALL', 'EMAIL', 'PAYMENT PLAN', 'NOTICE']
  },
  {
    id: '2',
    type: 'attention',
    title: 'Move-out notice - Mike R, Unit 5x5-A15 (Dec 31)',
    actions: ['INSPECTION', 'REFUND', 'SURVEY', 'RELIST']
  },
  {
    id: '3',
    type: 'info',
    title: 'New customer inquiry - Large unit for business storage',
    actions: ['CALL BACK', 'QUOTE', 'TOUR', 'DISCOUNT']
  }
])

// Methods
const formatDate = (date: Date) => {
  return format(date, 'EEEE, MMMM d, yyyy')
}

const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(amount)
}

const formatRating = (rating: number) => {
  return `${rating}★`
}

const getOccupancyColor = (rate: number) => {
  if (rate >= 90) return 'success'
  if (rate >= 80) return 'warning'
  return 'error'
}

const handleAlertAction = (alertId: string, action: string) => {
  console.log(`Handling action ${action} for alert ${alertId}`)
  // Implement action handling
}

const openReviewsDialog = () => {
  // Open reviews dialog
}

onMounted(async () => {
  await facilityStore.refreshData()
})
</script>

<style scoped>
.v-card {
  transition: transform 0.2s ease-in-out;
}

.v-card:hover {
  transform: translateY(-2px);
}
</style>