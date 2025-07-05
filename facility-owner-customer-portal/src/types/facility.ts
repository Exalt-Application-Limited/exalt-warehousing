export interface Facility {
  id: string
  name: string
  address: {
    street: string
    city: string
    state: string
    zipCode: string
    country: string
  }
  phone: string
  email: string
  managerId: string
  totalUnits: number
  occupiedUnits: number
  occupancyRate: number
  monthlyRevenue: number
  rating: number
  amenities: string[]
  operatingHours: {
    [key: string]: {
      open: string
      close: string
      is24Hours: boolean
    }
  }
  createdAt: string
  updatedAt: string
}

export interface Unit {
  id: string
  number: string
  type: string // '5x5', '10x10', '10x20', etc.
  size: {
    width: number
    height: number
    area: number
  }
  features: string[] // ['climate_controlled', 'drive_up', 'ground_floor']
  monthlyRate: number
  isOccupied: boolean
  customerId?: string
  customer?: Customer
  leaseStartDate?: string
  leaseEndDate?: string
  accessCode?: string
  notes?: string
  status: 'available' | 'occupied' | 'reserved' | 'maintenance'
  floor: number
  building: string
  zone: string
}

export interface Customer {
  id: string
  firstName: string
  lastName: string
  email: string
  phone: string
  address: {
    street: string
    city: string
    state: string
    zipCode: string
  }
  units: string[] // Unit IDs
  totalMonthlyRent: number
  paymentMethod: {
    type: 'credit_card' | 'bank_transfer' | 'cash'
    lastFour?: string
    autopay: boolean
  }
  leaseStatus: 'active' | 'past_due' | 'ending_soon' | 'terminated'
  joinDate: string
  rating: number
  notes?: string
  emergencyContact: {
    name: string
    phone: string
    relationship: string
  }
}

export interface OccupancyData {
  total: number
  occupied: number
  available: number
  maintenance: number
  reserved: number
  byType: {
    [key: string]: {
      total: number
      occupied: number
      rate: number
    }
  }
  byFloor: {
    [key: string]: {
      total: number
      occupied: number
      rate: number
    }
  }
  trends: {
    date: string
    occupancyRate: number
    newMoveIns: number
    moveOuts: number
  }[]
}

export interface FinancialData {
  monthlyRevenue: number
  yearlyRevenue: number
  collectionRate: number
  outstandingBalance: number
  revenueByType: {
    [key: string]: number
  }
  revenueHistory: {
    month: string
    revenue: number
    units: number
  }[]
  expenses: {
    category: string
    amount: number
  }[]
}

export interface SupportTicket {
  id: string
  customerId: string
  customer: Customer
  unitId?: string
  unit?: Unit
  subject: string
  description: string
  priority: 'low' | 'medium' | 'high' | 'urgent'
  status: 'open' | 'in_progress' | 'resolved' | 'closed'
  category: 'access' | 'billing' | 'maintenance' | 'complaint' | 'other'
  assignedTo?: string
  resolution?: string
  createdAt: string
  updatedAt: string
  communications: TicketCommunication[]
}

export interface TicketCommunication {
  id: string
  ticketId: string
  senderId: string
  senderType: 'customer' | 'staff'
  message: string
  attachments?: string[]
  createdAt: string
}

export interface FacilityMetrics {
  customers: number
  customersGrowth: number
  occupancy: number
  occupancyChange: number
  monthlyRevenue: number
  revenueGrowth: number
  rating: number
  ratingChange: number
  growthRate: number
  growthTrend: number
}