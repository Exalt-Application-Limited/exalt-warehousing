export interface StorageFacility {
  id: string;
  name: string;
  slug: string;
  description: string;
  address: Address;
  coordinates: Coordinates;
  phone: string;
  email?: string;
  website?: string;
  images: FacilityImage[];
  operatingHours: OperatingHours[];
  amenities: FacilityAmenity[];
  securityFeatures: SecurityFeature[];
  units: StorageUnit[];
  pricing: PricingInfo;
  policies: FacilityPolicies;
  rating: number;
  reviewCount: number;
  verified: boolean;
  featured: boolean;
  status: FacilityStatus;
  managementCompany?: ManagementCompany;
  createdAt: string;
  updatedAt: string;
}

export interface Address {
  street: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
  formattedAddress: string;
}

export interface Coordinates {
  latitude: number;
  longitude: number;
}

export interface FacilityImage {
  id: string;
  url: string;
  alt: string;
  type: ImageType;
  isPrimary: boolean;
  order: number;
}

export enum ImageType {
  EXTERIOR = 'EXTERIOR',
  INTERIOR = 'INTERIOR',
  UNIT = 'UNIT',
  AMENITY = 'AMENITY',
  SECURITY = 'SECURITY',
  OFFICE = 'OFFICE',
}

export interface OperatingHours {
  dayOfWeek: DayOfWeek;
  isOpen: boolean;
  openTime?: string;
  closeTime?: string;
  isAlwaysOpen?: boolean;
}

export enum DayOfWeek {
  MONDAY = 'MONDAY',
  TUESDAY = 'TUESDAY',
  WEDNESDAY = 'WEDNESDAY',
  THURSDAY = 'THURSDAY',
  FRIDAY = 'FRIDAY',
  SATURDAY = 'SATURDAY',
  SUNDAY = 'SUNDAY',
}

export interface FacilityAmenity {
  type: AmenityType;
  name: string;
  description?: string;
  available: boolean;
  additionalCost?: number;
}

export enum AmenityType {
  CLIMATE_CONTROL = 'CLIMATE_CONTROL',
  DRIVE_UP_ACCESS = 'DRIVE_UP_ACCESS',
  ELEVATOR_ACCESS = 'ELEVATOR_ACCESS',
  GROUND_FLOOR = 'GROUND_FLOOR',
  LOADING_DOCK = 'LOADING_DOCK',
  MOVING_SUPPLIES = 'MOVING_SUPPLIES',
  TRUCK_RENTAL = 'TRUCK_RENTAL',
  DOLLIES_CARTS = 'DOLLIES_CARTS',
  PACKAGING_SUPPLIES = 'PACKAGING_SUPPLIES',
  BUSINESS_CENTER = 'BUSINESS_CENTER',
  WIFI = 'WIFI',
  RESTROOMS = 'RESTROOMS',
  COVERED_LOADING = 'COVERED_LOADING',
  WIDE_AISLES = 'WIDE_AISLES',
}

export interface SecurityFeature {
  type: SecurityType;
  name: string;
  description?: string;
  active: boolean;
}

export enum SecurityType {
  SECURITY_CAMERAS = 'SECURITY_CAMERAS',
  GATED_ACCESS = 'GATED_ACCESS',
  KEYPAD_ACCESS = 'KEYPAD_ACCESS',
  INDIVIDUAL_ALARMS = 'INDIVIDUAL_ALARMS',
  ON_SITE_MANAGER = 'ON_SITE_MANAGER',
  SECURITY_PATROL = 'SECURITY_PATROL',
  WELL_LIT = 'WELL_LIT',
  FENCED_PERIMETER = 'FENCED_PERIMETER',
  MOTION_SENSORS = 'MOTION_SENSORS',
  FIRE_SUPPRESSION = 'FIRE_SUPPRESSION',
}

export interface StorageUnit {
  id: string;
  unitNumber: string;
  size: UnitSize;
  type: UnitType;
  floor: number;
  features: UnitFeature[];
  monthlyPrice: number;
  salePrice?: number;
  isAvailable: boolean;
  availableDate?: string;
  images?: string[];
  description?: string;
}

export interface UnitSize {
  width: number;
  length: number;
  height?: number;
  area: number;
  unit: SizeUnit;
}

export enum SizeUnit {
  SQUARE_FEET = 'SQUARE_FEET',
  SQUARE_METERS = 'SQUARE_METERS',
}

export enum UnitType {
  STANDARD = 'STANDARD',
  CLIMATE_CONTROLLED = 'CLIMATE_CONTROLLED',
  DRIVE_UP = 'DRIVE_UP',
  BUSINESS = 'BUSINESS',
  VEHICLE = 'VEHICLE',
  BOAT_RV = 'BOAT_RV',
  WINE_STORAGE = 'WINE_STORAGE',
}

export interface UnitFeature {
  type: UnitFeatureType;
  name: string;
  included: boolean;
}

export enum UnitFeatureType {
  CLIMATE_CONTROL = 'CLIMATE_CONTROL',
  DRIVE_UP = 'DRIVE_UP',
  GROUND_FLOOR = 'GROUND_FLOOR',
  ELEVATOR_ACCESS = 'ELEVATOR_ACCESS',
  ELECTRICAL_OUTLET = 'ELECTRICAL_OUTLET',
  INTERIOR_LIGHT = 'INTERIOR_LIGHT',
  HIGH_CEILING = 'HIGH_CEILING',
}

export interface PricingInfo {
  basePrice: number;
  discounts: Discount[];
  promotions: Promotion[];
  currency: string;
  priceIncludes: string[];
  additionalFees: AdditionalFee[];
}

export interface Discount {
  type: DiscountType;
  name: string;
  value: number;
  unit: DiscountUnit;
  conditions?: string;
}

export enum DiscountType {
  FIRST_MONTH = 'FIRST_MONTH',
  LONG_TERM = 'LONG_TERM',
  STUDENT = 'STUDENT',
  MILITARY = 'MILITARY',
  SENIOR = 'SENIOR',
  ONLINE_BOOKING = 'ONLINE_BOOKING',
}

export enum DiscountUnit {
  PERCENTAGE = 'PERCENTAGE',
  DOLLAR_AMOUNT = 'DOLLAR_AMOUNT',
  FREE_MONTHS = 'FREE_MONTHS',
}

export interface Promotion {
  id: string;
  name: string;
  description: string;
  discount: Discount;
  validFrom: string;
  validUntil: string;
  active: boolean;
}

export interface AdditionalFee {
  type: FeeType;
  name: string;
  amount: number;
  required: boolean;
  description?: string;
}

export enum FeeType {
  ADMIN_FEE = 'ADMIN_FEE',
  SECURITY_DEPOSIT = 'SECURITY_DEPOSIT',
  LOCK_FEE = 'LOCK_FEE',
  INSURANCE = 'INSURANCE',
  LATE_FEE = 'LATE_FEE',
  ACCESS_FEE = 'ACCESS_FEE',
}

export interface FacilityPolicies {
  accessHours: string;
  insurance: InsurancePolicy;
  cancellation: CancellationPolicy;
  payment: PaymentPolicy;
  restrictions: string[];
}

export interface InsurancePolicy {
  required: boolean;
  providedBy: string;
  monthlyRate?: number;
  coverage?: number;
  allowThirdParty: boolean;
}

export interface CancellationPolicy {
  noticeRequired: number;
  penaltyFee?: number;
  refundPolicy: string;
}

export interface PaymentPolicy {
  acceptedMethods: PaymentMethod[];
  autoPayRequired: boolean;
  lateFeeGracePeriod: number;
  lateFeeAmount: number;
}

export enum PaymentMethod {
  CREDIT_CARD = 'CREDIT_CARD',
  DEBIT_CARD = 'DEBIT_CARD',
  ACH = 'ACH',
  CASH = 'CASH',
  CHECK = 'CHECK',
  MONEY_ORDER = 'MONEY_ORDER',
}

export enum FacilityStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  UNDER_CONSTRUCTION = 'UNDER_CONSTRUCTION',
  TEMPORARILY_CLOSED = 'TEMPORARILY_CLOSED',
}

export interface ManagementCompany {
  id: string;
  name: string;
  website?: string;
  phone?: string;
  email?: string;
  rating?: number;
  facilityCount?: number;
}

export interface FacilitySearchParams {
  location?: string;
  coordinates?: Coordinates;
  radius?: number;
  unitSize?: {
    min?: number;
    max?: number;
  };
  unitType?: UnitType[];
  priceRange?: {
    min?: number;
    max?: number;
  };
  amenities?: AmenityType[];
  securityFeatures?: SecurityType[];
  availableDate?: string;
  sortBy?: FacilitySortOption;
  limit?: number;
  offset?: number;
}

export enum FacilitySortOption {
  DISTANCE = 'DISTANCE',
  PRICE_LOW_TO_HIGH = 'PRICE_LOW_TO_HIGH',
  PRICE_HIGH_TO_LOW = 'PRICE_HIGH_TO_LOW',
  RATING = 'RATING',
  NEWEST = 'NEWEST',
  SIZE = 'SIZE',
  AVAILABILITY = 'AVAILABILITY',
}