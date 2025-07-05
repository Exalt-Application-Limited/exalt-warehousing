import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { facilityApi } from '@/services/api/facility'
import type { Facility, Unit, Customer, OccupancyData } from '@/types/facility'

export const useFacilityStore = defineStore('facility', () => {
  const currentFacility = ref<Facility | null>(null)
  const units = ref<Unit[]>([])
  const customers = ref<Customer[]>([])
  const occupancyData = ref<OccupancyData | null>(null)
  const loading = ref(false)

  const totalUnits = computed(() => units.value.length)
  const occupiedUnits = computed(() => units.value.filter(unit => unit.isOccupied).length)
  const occupancyRate = computed(() => 
    totalUnits.value > 0 ? (occupiedUnits.value / totalUnits.value) * 100 : 0
  )
  
  const availableUnits = computed(() => units.value.filter(unit => !unit.isOccupied))
  const monthlyRevenue = computed(() => 
    units.value.reduce((total, unit) => total + (unit.isOccupied ? unit.monthlyRate : 0), 0)
  )

  const loadCurrentFacility = async () => {
    loading.value = true
    try {
      const facility = await facilityApi.getCurrentFacility()
      currentFacility.value = facility
      return facility
    } catch (error) {
      console.error('Failed to load facility:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const loadUnits = async () => {
    if (!currentFacility.value) return

    loading.value = true
    try {
      const response = await facilityApi.getUnits()
      units.value = response
      return response
    } catch (error) {
      console.error('Failed to load units:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const loadCustomers = async () => {
    if (!currentFacility.value) return

    loading.value = true
    try {
      const response = await facilityApi.getCustomers()
      customers.value = response
      return response
    } catch (error) {
      console.error('Failed to load customers:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const loadOccupancyData = async () => {
    if (!currentFacility.value) return

    loading.value = true
    try {
      const response = await facilityApi.getOccupancyData()
      occupancyData.value = response
      return response
    } catch (error) {
      console.error('Failed to load occupancy data:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const updateUnit = async (unitId: string, updates: Partial<Unit>) => {
    loading.value = true
    try {
      const updatedUnit = await facilityApi.updateUnit(unitId, updates)
      const index = units.value.findIndex(unit => unit.id === unitId)
      if (index !== -1) {
        units.value[index] = { ...units.value[index], ...updatedUnit }
      }
      return updatedUnit
    } catch (error) {
      console.error('Failed to update unit:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const updateCustomer = async (customerId: string, updates: Partial<Customer>) => {
    loading.value = true
    try {
      const updatedCustomer = await facilityApi.updateCustomer(customerId, updates)
      const index = customers.value.findIndex(customer => customer.id === customerId)
      if (index !== -1) {
        customers.value[index] = { ...customers.value[index], ...updatedCustomer }
      }
      return updatedCustomer
    } catch (error) {
      console.error('Failed to update customer:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const getCustomerById = (customerId: string) => {
    return customers.value.find(customer => customer.id === customerId)
  }

  const getUnitById = (unitId: string) => {
    return units.value.find(unit => unit.id === unitId)
  }

  const getUnitsByType = (type: string) => {
    return units.value.filter(unit => unit.type === type)
  }

  const refreshData = async () => {
    await Promise.all([
      loadCurrentFacility(),
      loadUnits(),
      loadCustomers(),
      loadOccupancyData()
    ])
  }

  return {
    currentFacility: computed(() => currentFacility.value),
    units: computed(() => units.value),
    customers: computed(() => customers.value),
    occupancyData: computed(() => occupancyData.value),
    loading: computed(() => loading.value),
    totalUnits,
    occupiedUnits,
    occupancyRate,
    availableUnits,
    monthlyRevenue,
    loadCurrentFacility,
    loadUnits,
    loadCustomers,
    loadOccupancyData,
    updateUnit,
    updateCustomer,
    getCustomerById,
    getUnitById,
    getUnitsByType,
    refreshData
  }
})