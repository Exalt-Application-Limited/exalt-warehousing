// API Response Types
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
  timestamp: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// Vendor Storage Location Types
export interface VendorStorageLocation {
  id: string;
  vendorId: string;
  vendorName: string;
  locationName: string;
  storageType: StorageType;
  status: StorageStatus;
  
  // Address
  streetAddress: string;
  addressLine2?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  latitude?: number;
  longitude?: number;
  
  // Contact
  contactName: string;
  contactPhone: string;
  contactEmail?: string;
  alternativePhone?: string;
  
  // Specifications
  storageAreaSqft: number;
  currentUtilization: number;
  maxWeightCapacity?: number;
  hasClimateControl: boolean;
  targetTemperature?: number;
  targetHumidity?: number;
  hasSecurity: boolean;
  securityFeatures?: string;
  
  // Operating Hours
  openingTime?: string;
  closingTime?: string;
  operatingDays?: string;
  is24x7: boolean;
  pickupInstructions?: string;
  
  // Performance
  avgProcessingTime: number;
  fulfillmentRate: number;
  performanceScore: number;
  totalOrdersProcessed: number;
  lastPickupDate?: string;
  
  // Metadata
  verificationDate?: string;
  verificationNotes?: string;
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export enum StorageType {
  HOME_GARAGE = 'HOME_GARAGE',
  HOME_BASEMENT = 'HOME_BASEMENT',
  HOME_SPARE_ROOM = 'HOME_SPARE_ROOM',
  COMMERCIAL_WAREHOUSE = 'COMMERCIAL_WAREHOUSE',
  COMMERCIAL_UNIT = 'COMMERCIAL_UNIT',
  RETAIL_STORE = 'RETAIL_STORE',
  RETAIL_BACK_ROOM = 'RETAIL_BACK_ROOM',
  SHARED_WAREHOUSE = 'SHARED_WAREHOUSE',
  COWORKING_STORAGE = 'COWORKING_STORAGE',
  CLIMATE_CONTROLLED = 'CLIMATE_CONTROLLED',
  COLD_STORAGE = 'COLD_STORAGE',
  SECURE_FACILITY = 'SECURE_FACILITY'
}

export enum StorageStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED',
  MAINTENANCE = 'MAINTENANCE',
  VERIFICATION = 'VERIFICATION',
  REJECTED = 'REJECTED',
  CAPACITY_FULL = 'CAPACITY_FULL'
}

// Dashboard Types
export interface VendorDashboardData {
  summary: VendorLocationsSummary;
  recentOrders: OrderSummary[];
  alerts: LocationAlert[];
  performanceMetrics: PerformanceMetric[];
  utilizationData: UtilizationData[];
}

export interface VendorLocationsSummary {
  vendorId: string;
  totalLocations: number;
  activeLocations: number;
  totalCapacity: number;
  avgUtilization: number;
  avgPerformanceScore: number;
  locationsRequiringAttention: number;
  
  // Trends
  locationsTrend?: number;
  activeTrend?: number;
  utilizationTrend?: number;
  performanceTrend?: number;
}

export interface LocationAlert {
  locationId: string;
  locationName: string;
  type: AlertType;
  severity: AlertSeverity;
  message: string;
  recommendedAction: string;
  createdAt: string;
}

export enum AlertType {
  CAPACITY_WARNING = 'CAPACITY_WARNING',
  PERFORMANCE_ISSUE = 'PERFORMANCE_ISSUE',
  STATUS_ATTENTION = 'STATUS_ATTENTION',
  VERIFICATION_REQUIRED = 'VERIFICATION_REQUIRED',
  MAINTENANCE_DUE = 'MAINTENANCE_DUE'
}

export enum AlertSeverity {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  CRITICAL = 'CRITICAL'
}

// Order Types
export interface OrderSummary {
  orderId: string;
  customerName: string;
  customerEmail: string;
  locationId: string;
  locationName: string;
  status: OrderStatus;
  itemCount: number;
  totalValue: number;
  courierService?: string;
  trackingNumber?: string;
  createdAt: string;
  updatedAt: string;
  
  // UI helpers
  statusBadgeClass: string;
  canPrintLabel: boolean;
  canFulfill: boolean;
}

export interface OrderDetails extends OrderSummary {
  shippingAddress: Address;
  billingAddress: Address;
  items: OrderItem[];
  paymentStatus: PaymentStatus;
  shippingCost: number;
  taxAmount: number;
  discountAmount: number;
  specialInstructions?: string;
  
  // Fulfillment
  fulfillmentStatus: FulfillmentStatus;
  fulfillmentDate?: string;
  pickupScheduledDate?: string;
  actualPickupDate?: string;
  deliveryEstimate?: string;
}

export interface OrderItem {
  sku: string;
  productName: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  weight?: number;
  dimensions?: string;
  requiresClimateControl: boolean;
  isFragile: boolean;
}

export interface Address {
  streetAddress: string;
  addressLine2?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
}

export enum OrderStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  PROCESSING = 'PROCESSING',
  READY_FOR_PICKUP = 'READY_FOR_PICKUP',
  IN_TRANSIT = 'IN_TRANSIT',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED',
  RETURNED = 'RETURNED'
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  FAILED = 'FAILED',
  REFUNDED = 'REFUNDED'
}

export enum FulfillmentStatus {
  PENDING = 'PENDING',
  PREPARING = 'PREPARING',
  READY = 'READY',
  PICKED_UP = 'PICKED_UP',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED'
}

// Analytics Types
export interface PerformanceMetric {
  date: string;
  processingTime: number;
  fulfillmentRate: number;
  performanceScore: number;
  orderCount: number;
}

export interface UtilizationData {
  locationId: string;
  locationName: string;
  utilization: number;
  capacity: number;
  storageType: StorageType;
}

// Label Printing Types
export interface LabelGenerationRequest {
  orderId: string;
  courierService: string;
  printerType: 'PDF' | 'THERMAL' | 'PNG';
  packageDimensions?: PackageDimensions;
}

export interface PackageDimensions {
  length: number;
  width: number;
  height: number;
  weight: number;
  units: 'IN' | 'CM';
  weightUnits: 'LB' | 'KG';
}

export interface ShippingLabelResponse {
  labelId: string;
  orderId: string;
  courierService: string;
  trackingNumber: string;
  labelFormats: {
    pdf?: string;
    png?: string;
    zpl?: string;
    thermal?: string;
  };
  dimensions: {
    width: string;
    height: string;
    dpi: number;
  };
  expirationTime: string;
}

// Form Types
export interface LocationRegistrationForm {
  locationName: string;
  storageType: StorageType;
  streetAddress: string;
  addressLine2?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  contactName: string;
  contactPhone: string;
  contactEmail?: string;
  storageAreaSqft: number;
  hasClimateControl: boolean;
  targetTemperature?: number;
  targetHumidity?: number;
  hasSecurity: boolean;
  securityFeatures?: string;
  openingTime?: string;
  closingTime?: string;
  is24x7: boolean;
  pickupInstructions?: string;
}

export interface LocationUpdateForm extends Partial<LocationRegistrationForm> {
  notes?: string;
}

// User & Auth Types
export interface User {
  id: string;
  email: string;
  name: string;
  vendorId: string;
  vendorName: string;
  role: UserRole;
  permissions: Permission[];
  lastLoginAt?: string;
  createdAt: string;
}

export enum UserRole {
  VENDOR_OWNER = 'VENDOR_OWNER',
  VENDOR_MANAGER = 'VENDOR_MANAGER',
  VENDOR_STAFF = 'VENDOR_STAFF',
  ADMIN = 'ADMIN'
}

export enum Permission {
  VIEW_DASHBOARD = 'VIEW_DASHBOARD',
  MANAGE_LOCATIONS = 'MANAGE_LOCATIONS',
  PROCESS_ORDERS = 'PROCESS_ORDERS',
  VIEW_ANALYTICS = 'VIEW_ANALYTICS',
  MANAGE_SETTINGS = 'MANAGE_SETTINGS',
  PRINT_LABELS = 'PRINT_LABELS'
}

// Settings Types
export interface VendorSettings {
  vendorId: string;
  businessName: string;
  businessType: string;
  defaultCourierService: string;
  autoFulfillment: boolean;
  notifications: NotificationSettings;
  integrations: IntegrationSettings;
  branding: BrandingSettings;
}

export interface NotificationSettings {
  email: boolean;
  sms: boolean;
  push: boolean;
  orderAlerts: boolean;
  performanceAlerts: boolean;
  capacityAlerts: boolean;
  maintenanceReminders: boolean;
}

export interface IntegrationSettings {
  printerType: 'THERMAL' | 'STANDARD';
  printerModel?: string;
  courierIntegrations: {
    fedex: boolean;
    ups: boolean;
    dhl: boolean;
    usps: boolean;
  };
}

export interface BrandingSettings {
  logoUrl?: string;
  primaryColor: string;
  secondaryColor: string;
  companyName: string;
  website?: string;
}

// WebSocket Types
export interface WebSocketMessage {
  type: WebSocketMessageType;
  payload: any;
  timestamp: string;
}

export enum WebSocketMessageType {
  ORDER_UPDATE = 'ORDER_UPDATE',
  LOCATION_UPDATE = 'LOCATION_UPDATE',
  PERFORMANCE_UPDATE = 'PERFORMANCE_UPDATE',
  ALERT_NEW = 'ALERT_NEW',
  ALERT_RESOLVED = 'ALERT_RESOLVED'
}

// Utility Types
export type LoadingState = 'idle' | 'loading' | 'success' | 'error';

export interface SelectOption {
  value: string;
  label: string;
  disabled?: boolean;
}

export interface TableColumn<T> {
  key: keyof T;
  title: string;
  sortable?: boolean;
  width?: string;
  render?: (value: any, record: T) => React.ReactNode;
}

export interface PaginationInfo {
  page: number;
  size: number;
  total: number;
  totalPages: number;
}

// Error Types
export interface ApiError {
  code: string;
  message: string;
  details?: Record<string, any>;
  timestamp: string;
}

export interface ValidationError {
  field: string;
  message: string;
  code: string;
}