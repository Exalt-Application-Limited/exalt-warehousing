const mongoose = require('mongoose');

/**
 * Analytics Event Schema
 * 
 * Captures customer behavior events for analytics and insights.
 */
const analyticsEventSchema = new mongoose.Schema({
  // Event identification
  eventId: {
    type: String,
    required: true,
    unique: true,
    index: true
  },
  
  // Customer information
  customerId: {
    type: String,
    required: true,
    index: true
  },
  
  customerType: {
    type: String,
    enum: ['Individual', 'Business', 'Premium'],
    default: 'Individual',
    index: true
  },
  
  // Event details
  eventType: {
    type: String,
    required: true,
    enum: [
      'STORAGE_SEARCH',
      'UNIT_VIEW',
      'UNIT_BOOKING',
      'PAYMENT_COMPLETED',
      'INVENTORY_ADDED',
      'INVENTORY_UPDATED',
      'SUPPORT_CONTACTED',
      'APP_OPENED',
      'PAGE_VIEW',
      'SESSION_START',
      'SESSION_END',
      'FEATURE_USED',
      'ERROR_OCCURRED'
    ],
    index: true
  },
  
  eventCategory: {
    type: String,
    required: true,
    enum: ['User_Behavior', 'Business_Event', 'System_Event', 'Error_Event'],
    index: true
  },
  
  // Event context
  sessionId: {
    type: String,
    index: true
  },
  
  timestamp: {
    type: Date,
    default: Date.now,
    index: true
  },
  
  // Geographic data
  location: {
    country: String,
    region: String,
    city: String,
    zipCode: String,
    coordinates: {
      latitude: Number,
      longitude: Number
    }
  },
  
  // Device and platform information
  device: {
    platform: { type: String, enum: ['Web', 'Mobile', 'Tablet', 'Desktop'] },
    browser: String,
    os: String,
    deviceId: String,
    screenResolution: String,
    userAgent: String
  },
  
  // Event properties
  properties: {
    // Storage-related properties
    storageUnitId: String,
    facilityId: String,
    unitSize: String,
    priceRange: String,
    duration: String,
    
    // Inventory-related properties
    inventoryItemId: String,
    itemCategory: String,
    itemValue: Number,
    
    // Navigation properties
    pageUrl: String,
    referrerUrl: String,
    searchQuery: String,
    filterCriteria: mongoose.Schema.Types.Mixed,
    
    // Performance metrics
    loadTime: Number,
    responseTime: Number,
    
    // Business metrics
    revenue: Number,
    conversionStep: String,
    funnelStage: String,
    
    // Custom properties
    customData: mongoose.Schema.Types.Mixed
  },
  
  // Computed metrics
  metrics: {
    sessionDuration: Number,
    pageViewCount: Number,
    clickCount: Number,
    scrollDepth: Number,
    engagementScore: Number,
    satisfactionRating: Number
  },
  
  // A/B testing and personalization
  experiments: [{
    experimentId: String,
    variant: String,
    exposureTime: Date
  }],
  
  // Data quality and debugging
  dataQuality: {
    isValid: { type: Boolean, default: true },
    validationErrors: [String],
    processingTime: Date,
    dataSource: { type: String, enum: ['Web', 'Mobile', 'API', 'Batch'], default: 'API' }
  },
  
  // Privacy and compliance
  privacy: {
    consentGiven: { type: Boolean, default: false },
    dataRetentionExpiry: Date,
    anonymized: { type: Boolean, default: false },
    ipAddressHashed: String
  }
}, {
  timestamps: true,
  toJSON: { virtuals: true },
  toObject: { virtuals: true }
});

// Indexes for analytics queries
analyticsEventSchema.index({ customerId: 1, timestamp: -1 });
analyticsEventSchema.index({ eventType: 1, timestamp: -1 });
analyticsEventSchema.index({ eventCategory: 1, timestamp: -1 });
analyticsEventSchema.index({ 'properties.facilityId': 1, timestamp: -1 });
analyticsEventSchema.index({ sessionId: 1, timestamp: 1 });
analyticsEventSchema.index({ customerType: 1, eventType: 1 });
analyticsEventSchema.index({ 'device.platform': 1, eventType: 1 });
analyticsEventSchema.index({ 'location.country': 1, 'location.region': 1 });

// Time-based partitioning indexes
analyticsEventSchema.index({ 
  timestamp: 1, 
  eventType: 1, 
  customerId: 1 
});

// Pre-save middleware for data enrichment
analyticsEventSchema.pre('save', function(next) {
  // Generate eventId if not provided
  if (!this.eventId) {
    this.eventId = `evt_${Date.now()}_${Math.random().toString(36).substring(2)}`;
  }
  
  // Set data retention expiry (1 year default)
  if (!this.privacy.dataRetentionExpiry) {
    const expiry = new Date();
    expiry.setFullYear(expiry.getFullYear() + 1);
    this.privacy.dataRetentionExpiry = expiry;
  }
  
  next();
});

// Virtual for event age
analyticsEventSchema.virtual('ageInDays').get(function() {
  const now = new Date();
  const diffTime = Math.abs(now - this.timestamp);
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
});

// Static methods for analytics queries
analyticsEventSchema.statics.findByCustomer = function(customerId, dateRange = {}) {
  const query = { customerId };
  
  if (dateRange.from || dateRange.to) {
    query.timestamp = {};
    if (dateRange.from) query.timestamp.$gte = new Date(dateRange.from);
    if (dateRange.to) query.timestamp.$lte = new Date(dateRange.to);
  }
  
  return this.find(query).sort({ timestamp: -1 });
};

analyticsEventSchema.statics.getEventsByType = function(eventType, dateRange = {}) {
  const query = { eventType };
  
  if (dateRange.from || dateRange.to) {
    query.timestamp = {};
    if (dateRange.from) query.timestamp.$gte = new Date(dateRange.from);
    if (dateRange.to) query.timestamp.$lte = new Date(dateRange.to);
  }
  
  return this.find(query).sort({ timestamp: -1 });
};

analyticsEventSchema.statics.getCustomerJourney = function(customerId, sessionId = null) {
  const query = { customerId };
  if (sessionId) query.sessionId = sessionId;
  
  return this.find(query)
    .sort({ timestamp: 1 })
    .select('eventType eventCategory timestamp properties.pageUrl properties.conversionStep sessionId');
};

module.exports = mongoose.model('AnalyticsEvent', analyticsEventSchema);