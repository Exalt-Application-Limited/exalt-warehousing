const mongoose = require('mongoose');

/**
 * Customer Inventory Item Schema
 * 
 * Represents items stored by customers with detailed tracking information.
 */
const inventoryItemSchema = new mongoose.Schema({
  customerId: {
    type: String,
    required: true,
    index: true
  },
  
  storageUnitId: {
    type: String,
    required: true,
    index: true
  },
  
  itemName: {
    type: String,
    required: true,
    trim: true
  },
  
  description: {
    type: String,
    trim: true
  },
  
  category: {
    type: String,
    required: true,
    enum: ['Furniture', 'Electronics', 'Clothing', 'Documents', 'Artwork', 'Appliances', 'Sports', 'Books', 'Other'],
    index: true
  },
  
  subcategory: {
    type: String,
    trim: true
  },
  
  brand: {
    type: String,
    trim: true
  },
  
  model: {
    type: String,
    trim: true
  },
  
  serialNumber: {
    type: String,
    trim: true,
    sparse: true
  },
  
  purchaseDate: {
    type: Date
  },
  
  purchasePrice: {
    type: Number,
    min: 0
  },
  
  currentValue: {
    type: Number,
    min: 0
  },
  
  condition: {
    type: String,
    required: true,
    enum: ['Excellent', 'Good', 'Fair', 'Poor'],
    default: 'Good'
  },
  
  quantity: {
    type: Number,
    required: true,
    min: 1,
    default: 1
  },
  
  weight: {
    type: Number,
    min: 0
  },
  
  dimensions: {
    length: { type: Number, min: 0 },
    width: { type: Number, min: 0 },
    height: { type: Number, min: 0 },
    unit: { type: String, enum: ['cm', 'inch'], default: 'cm' }
  },
  
  location: {
    zone: String,
    shelf: String,
    position: String,
    notes: String
  },
  
  photos: [{
    url: String,
    caption: String,
    uploadedAt: { type: Date, default: Date.now }
  }],
  
  tags: [String],
  
  isFragile: {
    type: Boolean,
    default: false
  },
  
  isInsured: {
    type: Boolean,
    default: false
  },
  
  insuranceValue: {
    type: Number,
    min: 0
  },
  
  barcode: {
    type: String,
    unique: true,
    sparse: true
  },
  
  qrCode: {
    type: String,
    unique: true,
    sparse: true
  },
  
  status: {
    type: String,
    required: true,
    enum: ['Active', 'Moved', 'Removed', 'Lost', 'Damaged'],
    default: 'Active',
    index: true
  },
  
  lastUpdated: {
    type: Date,
    default: Date.now
  },
  
  createdAt: {
    type: Date,
    default: Date.now
  },
  
  metadata: {
    createdBy: String,
    lastModifiedBy: String,
    source: { type: String, enum: ['Manual', 'Barcode', 'Photo', 'Import'], default: 'Manual' }
  }
}, {
  timestamps: true,
  toJSON: { virtuals: true },
  toObject: { virtuals: true }
});

// Indexes for performance
inventoryItemSchema.index({ customerId: 1, storageUnitId: 1 });
inventoryItemSchema.index({ customerId: 1, category: 1 });
inventoryItemSchema.index({ barcode: 1 }, { sparse: true });
inventoryItemSchema.index({ qrCode: 1 }, { sparse: true });
inventoryItemSchema.index({ tags: 1 });
inventoryItemSchema.index({ status: 1, createdAt: -1 });

// Virtual for total volume
inventoryItemSchema.virtual('volume').get(function() {
  if (this.dimensions.length && this.dimensions.width && this.dimensions.height) {
    return this.dimensions.length * this.dimensions.width * this.dimensions.height;
  }
  return null;
});

// Pre-save middleware
inventoryItemSchema.pre('save', function(next) {
  this.lastUpdated = new Date();
  next();
});

// Static methods
inventoryItemSchema.statics.findByCustomer = function(customerId, filters = {}) {
  return this.find({ customerId, ...filters }).sort({ createdAt: -1 });
};

inventoryItemSchema.statics.findByStorageUnit = function(storageUnitId, filters = {}) {
  return this.find({ storageUnitId, ...filters }).sort({ createdAt: -1 });
};

inventoryItemSchema.statics.searchItems = function(customerId, searchTerm) {
  return this.find({
    customerId,
    $or: [
      { itemName: { $regex: searchTerm, $options: 'i' } },
      { description: { $regex: searchTerm, $options: 'i' } },
      { brand: { $regex: searchTerm, $options: 'i' } },
      { model: { $regex: searchTerm, $options: 'i' } },
      { tags: { $in: [new RegExp(searchTerm, 'i')] } }
    ]
  });
};

module.exports = mongoose.model('InventoryItem', inventoryItemSchema);