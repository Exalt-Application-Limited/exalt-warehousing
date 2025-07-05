const InventoryItem = require('../models/InventoryItem');
const logger = require('../config/logger');

/**
 * Inventory Service
 * 
 * Business logic for customer inventory tracking operations.
 */
class InventoryService {
  
  /**
   * Create a new inventory item
   */
  async createItem(itemData) {
    try {
      logger.info('Creating inventory item', { customerId: itemData.customerId, itemName: itemData.itemName });
      
      // Generate barcode if not provided
      if (!itemData.barcode) {
        itemData.barcode = this.generateBarcode();
      }
      
      // Generate QR code if not provided
      if (!itemData.qrCode) {
        itemData.qrCode = this.generateQRCode();
      }
      
      const item = new InventoryItem(itemData);
      const savedItem = await item.save();
      
      logger.info('Inventory item created successfully', { 
        itemId: savedItem._id, 
        customerId: savedItem.customerId 
      });
      
      return savedItem;
    } catch (error) {
      logger.error('Error creating inventory item', { error: error.message, itemData });
      throw error;
    }
  }
  
  /**
   * Get item by ID
   */
  async getItemById(itemId) {
    try {
      logger.debug('Retrieving inventory item by ID', { itemId });
      
      const item = await InventoryItem.findById(itemId);
      if (!item) {
        throw new Error(`Inventory item not found with ID: ${itemId}`);
      }
      
      return item;
    } catch (error) {
      logger.error('Error retrieving inventory item', { error: error.message, itemId });
      throw error;
    }
  }
  
  /**
   * Get customer inventory
   */
  async getCustomerInventory(customerId, filters = {}, options = {}) {
    try {
      logger.debug('Retrieving customer inventory', { customerId, filters });
      
      const { page = 1, limit = 20, sortBy = 'createdAt', sortOrder = 'desc' } = options;
      const skip = (page - 1) * limit;
      
      const query = { customerId, ...filters };
      const sort = { [sortBy]: sortOrder === 'desc' ? -1 : 1 };
      
      const [items, totalCount] = await Promise.all([
        InventoryItem.find(query)
          .sort(sort)
          .skip(skip)
          .limit(limit)
          .lean(),
        InventoryItem.countDocuments(query)
      ]);
      
      return {
        items,
        pagination: {
          page,
          limit,
          totalCount,
          totalPages: Math.ceil(totalCount / limit)
        }
      };
    } catch (error) {
      logger.error('Error retrieving customer inventory', { error: error.message, customerId });
      throw error;
    }
  }
  
  /**
   * Get storage unit inventory
   */
  async getStorageUnitInventory(storageUnitId, options = {}) {
    try {
      logger.debug('Retrieving storage unit inventory', { storageUnitId });
      
      const { includeStats = false } = options;
      
      const items = await InventoryItem.findByStorageUnit(storageUnitId);
      
      let stats = null;
      if (includeStats) {
        stats = await this.calculateStorageUnitStats(storageUnitId);
      }
      
      return { items, stats };
    } catch (error) {
      logger.error('Error retrieving storage unit inventory', { error: error.message, storageUnitId });
      throw error;
    }
  }
  
  /**
   * Update inventory item
   */
  async updateItem(itemId, updateData) {
    try {
      logger.info('Updating inventory item', { itemId, updateData });
      
      updateData.lastUpdated = new Date();
      
      const updatedItem = await InventoryItem.findByIdAndUpdate(
        itemId,
        updateData,
        { new: true, runValidators: true }
      );
      
      if (!updatedItem) {
        throw new Error(`Inventory item not found with ID: ${itemId}`);
      }
      
      logger.info('Inventory item updated successfully', { itemId });
      return updatedItem;
    } catch (error) {
      logger.error('Error updating inventory item', { error: error.message, itemId });
      throw error;
    }
  }
  
  /**
   * Delete inventory item
   */
  async deleteItem(itemId) {
    try {
      logger.info('Deleting inventory item', { itemId });
      
      const deletedItem = await InventoryItem.findByIdAndDelete(itemId);
      
      if (!deletedItem) {
        throw new Error(`Inventory item not found with ID: ${itemId}`);
      }
      
      logger.info('Inventory item deleted successfully', { itemId });
      return deletedItem;
    } catch (error) {
      logger.error('Error deleting inventory item', { error: error.message, itemId });
      throw error;
    }
  }
  
  /**
   * Search inventory items
   */
  async searchItems(customerId, searchTerm, options = {}) {
    try {
      logger.debug('Searching inventory items', { customerId, searchTerm });
      
      const { page = 1, limit = 20 } = options;
      const skip = (page - 1) * limit;
      
      const items = await InventoryItem.searchItems(customerId, searchTerm)
        .skip(skip)
        .limit(limit)
        .lean();
      
      const totalCount = await InventoryItem.searchItems(customerId, searchTerm).countDocuments();
      
      return {
        items,
        pagination: {
          page,
          limit,
          totalCount,
          totalPages: Math.ceil(totalCount / limit)
        }
      };
    } catch (error) {
      logger.error('Error searching inventory items', { error: error.message, customerId, searchTerm });
      throw error;
    }
  }
  
  /**
   * Get inventory statistics
   */
  async getInventoryStats(customerId) {
    try {
      logger.debug('Calculating inventory statistics', { customerId });
      
      const pipeline = [
        { $match: { customerId } },
        {
          $group: {
            _id: null,
            totalItems: { $sum: '$quantity' },
            totalValue: { $sum: '$currentValue' },
            averageValue: { $avg: '$currentValue' },
            categoryBreakdown: {
              $push: {
                category: '$category',
                quantity: '$quantity',
                value: '$currentValue'
              }
            }
          }
        }
      ];
      
      const [stats] = await InventoryItem.aggregate(pipeline);
      
      // Category statistics
      const categoryStats = await InventoryItem.aggregate([
        { $match: { customerId } },
        {
          $group: {
            _id: '$category',
            count: { $sum: 1 },
            totalQuantity: { $sum: '$quantity' },
            totalValue: { $sum: '$currentValue' }
          }
        }
      ]);
      
      // Status breakdown
      const statusStats = await InventoryItem.aggregate([
        { $match: { customerId } },
        {
          $group: {
            _id: '$status',
            count: { $sum: 1 }
          }
        }
      ]);
      
      return {
        overview: stats || { totalItems: 0, totalValue: 0, averageValue: 0 },
        categoryBreakdown: categoryStats,
        statusBreakdown: statusStats
      };
    } catch (error) {
      logger.error('Error calculating inventory statistics', { error: error.message, customerId });
      throw error;
    }
  }
  
  /**
   * Get items by barcode
   */
  async getItemByBarcode(barcode) {
    try {
      logger.debug('Retrieving item by barcode', { barcode });
      
      const item = await InventoryItem.findOne({ barcode });
      if (!item) {
        throw new Error(`Item not found with barcode: ${barcode}`);
      }
      
      return item;
    } catch (error) {
      logger.error('Error retrieving item by barcode', { error: error.message, barcode });
      throw error;
    }
  }
  
  /**
   * Bulk import items
   */
  async bulkImportItems(customerId, items) {
    try {
      logger.info('Bulk importing inventory items', { customerId, count: items.length });
      
      const processedItems = items.map(item => ({
        ...item,
        customerId,
        barcode: item.barcode || this.generateBarcode(),
        qrCode: item.qrCode || this.generateQRCode(),
        metadata: {
          ...item.metadata,
          source: 'Import'
        }
      }));
      
      const result = await InventoryItem.insertMany(processedItems, { ordered: false });
      
      logger.info('Bulk import completed', { 
        customerId, 
        imported: result.length, 
        requested: items.length 
      });
      
      return result;
    } catch (error) {
      logger.error('Error bulk importing items', { error: error.message, customerId });
      throw error;
    }
  }
  
  /**
   * Calculate storage unit statistics
   */
  async calculateStorageUnitStats(storageUnitId) {
    try {
      const pipeline = [
        { $match: { storageUnitId } },
        {
          $group: {
            _id: null,
            totalItems: { $sum: '$quantity' },
            totalValue: { $sum: '$currentValue' },
            totalWeight: { $sum: '$weight' },
            fragileItems: {
              $sum: { $cond: ['$isFragile', '$quantity', 0] }
            },
            insuredItems: {
              $sum: { $cond: ['$isInsured', '$quantity', 0] }
            }
          }
        }
      ];
      
      const [stats] = await InventoryItem.aggregate(pipeline);
      return stats || { 
        totalItems: 0, 
        totalValue: 0, 
        totalWeight: 0, 
        fragileItems: 0, 
        insuredItems: 0 
      };
    } catch (error) {
      logger.error('Error calculating storage unit stats', { error: error.message, storageUnitId });
      throw error;
    }
  }
  
  /**
   * Generate unique barcode
   */
  generateBarcode() {
    const timestamp = Date.now().toString();
    const random = Math.random().toString(36).substring(2, 8);
    return `INV${timestamp}${random}`.toUpperCase();
  }
  
  /**
   * Generate unique QR code
   */
  generateQRCode() {
    const timestamp = Date.now().toString();
    const random = Math.random().toString(36).substring(2, 10);
    return `QR${timestamp}${random}`.toUpperCase();
  }
}

module.exports = new InventoryService();