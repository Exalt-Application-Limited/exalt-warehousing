export interface StorageBooking {
  id: string;
  bookingNumber: string;
  customerId: string;
  facilityId: string;
  unitId: string;
  facility: FacilityBasicInfo;
  unit: BookedUnit;
  customer: CustomerInfo;
  rentalPeriod: RentalPeriod;
  pricing: BookingPricing;
  status: BookingStatus;
  accessCredentials?: AccessCredentials;
  documents: BookingDocument[];
  payments: PaymentRecord[];
  insurance?: InsuranceInfo;
  additionalServices: AdditionalService[];
  specialRequests?: string;
  createdAt: string;
  updatedAt: string;
  activatedAt?: string;
  expiresAt?: string;
}

export interface FacilityBasicInfo {
  id: string;
  name: string;
  address: string;
  phone: string;
  managerName?: string;
  accessHours: string;
}

export interface BookedUnit {
  id: string;
  unitNumber: string;
  size: {
    width: number;
    length: number;
    area: number;
    unit: string;
  };
  type: string;
  floor: number;
  features: string[];
}

export interface CustomerInfo {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  address?: Address;
  emergencyContact?: EmergencyContact;
}

export interface Address {
  street: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
}

export interface EmergencyContact {
  name: string;
  relationship: string;
  phone: string;
}

export interface RentalPeriod {
  startDate: string;
  endDate?: string;
  isMonthToMonth: boolean;
  autoRenew: boolean;
  minimumTerm: number;
  termUnit: 'DAYS' | 'MONTHS' | 'YEARS';
}

export interface BookingPricing {
  monthlyRent: number;
  proRatedAmount?: number;
  deposit: number;
  adminFee: number;
  lockFee?: number;
  insuranceFee?: number;
  taxes: number;
  discounts: AppliedDiscount[];
  totalDue: number;
  currency: string;
}

export interface AppliedDiscount {
  type: string;
  name: string;
  amount: number;
  appliedTo: 'RENT' | 'FEES' | 'TOTAL';
}

export enum BookingStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  ACTIVE = 'ACTIVE',
  CANCELLED = 'CANCELLED',
  EXPIRED = 'EXPIRED',
  COMPLETED = 'COMPLETED',
  OVERDUE = 'OVERDUE',
  SUSPENDED = 'SUSPENDED',
}

export interface AccessCredentials {
  gateCode?: string;
  unitCode?: string;
  keyCode?: string;
  accessCard?: AccessCard;
  biometric?: BiometricAccess;
  instructions: string;
  issueDate: string;
  expiryDate?: string;
}

export interface AccessCard {
  cardNumber: string;
  issueDate: string;
  expiryDate?: string;
  status: 'ACTIVE' | 'INACTIVE' | 'LOST' | 'DAMAGED';
}

export interface BiometricAccess {
  type: 'FINGERPRINT' | 'FACIAL_RECOGNITION';
  enrolled: boolean;
  enrollmentDate?: string;
}

export interface BookingDocument {
  id: string;
  type: DocumentType;
  name: string;
  url: string;
  uploadedAt: string;
  signedAt?: string;
  required: boolean;
  status: DocumentStatus;
}

export enum DocumentType {
  RENTAL_AGREEMENT = 'RENTAL_AGREEMENT',
  INSURANCE_POLICY = 'INSURANCE_POLICY',
  PAYMENT_AUTHORIZATION = 'PAYMENT_AUTHORIZATION',
  IDENTIFICATION = 'IDENTIFICATION',
  INVENTORY_LIST = 'INVENTORY_LIST',
  PHOTO_VERIFICATION = 'PHOTO_VERIFICATION',
  EMERGENCY_CONTACT = 'EMERGENCY_CONTACT',
}

export enum DocumentStatus {
  PENDING = 'PENDING',
  UPLOADED = 'UPLOADED',
  VERIFIED = 'VERIFIED',
  REJECTED = 'REJECTED',
  SIGNED = 'SIGNED',
}

export interface PaymentRecord {
  id: string;
  amount: number;
  type: PaymentType;
  method: PaymentMethod;
  status: PaymentStatus;
  transactionId?: string;
  processedAt?: string;
  dueDate: string;
  description: string;
  invoice?: InvoiceInfo;
}

export enum PaymentType {
  RENTAL_FEE = 'RENTAL_FEE',
  DEPOSIT = 'DEPOSIT',
  ADMIN_FEE = 'ADMIN_FEE',
  LATE_FEE = 'LATE_FEE',
  INSURANCE = 'INSURANCE',
  ADDITIONAL_SERVICE = 'ADDITIONAL_SERVICE',
  REFUND = 'REFUND',
}

export enum PaymentMethod {
  CREDIT_CARD = 'CREDIT_CARD',
  DEBIT_CARD = 'DEBIT_CARD',
  ACH = 'ACH',
  CASH = 'CASH',
  CHECK = 'CHECK',
  MONEY_ORDER = 'MONEY_ORDER',
  WIRE_TRANSFER = 'WIRE_TRANSFER',
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
  CANCELLED = 'CANCELLED',
  REFUNDED = 'REFUNDED',
  DISPUTED = 'DISPUTED',
}

export interface InvoiceInfo {
  invoiceNumber: string;
  issueDate: string;
  dueDate: string;
  url: string;
}

export interface InsuranceInfo {
  provider: string;
  policyNumber: string;
  coverage: number;
  monthlyPremium: number;
  startDate: string;
  endDate: string;
  status: InsuranceStatus;
}

export enum InsuranceStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  PENDING = 'PENDING',
  EXPIRED = 'EXPIRED',
  CANCELLED = 'CANCELLED',
}

export interface AdditionalService {
  id: string;
  type: ServiceType;
  name: string;
  description: string;
  price: number;
  recurring: boolean;
  startDate: string;
  endDate?: string;
  status: ServiceStatus;
}

export enum ServiceType {
  MOVING_ASSISTANCE = 'MOVING_ASSISTANCE',
  PACKING_SUPPLIES = 'PACKING_SUPPLIES',
  TRUCK_RENTAL = 'TRUCK_RENTAL',
  PICKUP_SERVICE = 'PICKUP_SERVICE',
  DELIVERY_SERVICE = 'DELIVERY_SERVICE',
  CLEANING_SERVICE = 'CLEANING_SERVICE',
  INVENTORY_SERVICE = 'INVENTORY_SERVICE',
  CLIMATE_MONITORING = 'CLIMATE_MONITORING',
}

export enum ServiceStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  PENDING = 'PENDING',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
}

export interface BookingRequest {
  facilityId: string;
  unitId: string;
  customerId?: string;
  startDate: string;
  endDate?: string;
  isMonthToMonth: boolean;
  autoRenew: boolean;
  emergencyContact: EmergencyContact;
  selectedInsurance?: InsuranceOption;
  additionalServices: string[];
  paymentMethodId: string;
  specialRequests?: string;
  agreedToTerms: boolean;
}

export interface InsuranceOption {
  providerId: string;
  planId: string;
  coverage: number;
  monthlyPremium: number;
}

export interface BookingCalculation {
  monthlyRent: number;
  proRatedAmount: number;
  deposit: number;
  fees: CalculatedFee[];
  taxes: number;
  discounts: CalculatedDiscount[];
  insurance?: number;
  additionalServices: CalculatedService[];
  totalDue: number;
  monthlyRecurring: number;
  currency: string;
}

export interface CalculatedFee {
  type: string;
  name: string;
  amount: number;
  required: boolean;
  description?: string;
}

export interface CalculatedDiscount {
  type: string;
  name: string;
  amount: number;
  percentage?: number;
  description?: string;
}

export interface CalculatedService {
  id: string;
  name: string;
  price: number;
  recurring: boolean;
  description?: string;
}