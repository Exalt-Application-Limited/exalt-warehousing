const inventoryService = require('../services/inventoryService');
const logger = require('../config/logger');

/**
 * Inventory Controller
 * 
 * HTTP request handlers for inventory operations.
 */
class InventoryController {
  
  /**
   * Create new inventory item
   */
  async createItem(req, res, next) {
    try {
      logger.info('Creating inventory item', { 
        customerId: req.body.customerId, 
        itemName: req.body.itemName 
      });
      
      const item = await inventoryService.createItem(req.body);
      
      res.status(201).json({
        success: true,
        message: 'Inventory item created successfully',
        data: item
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Get inventory item by ID
   */
  async getItemById(req, res, next) {
    try {
      const { itemId } = req.params;
      
      logger.debug('Retrieving inventory item', { itemId });
      
      const item = await inventoryService.getItemById(itemId);
      
      res.json({
        success: true,
        data: item
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Get customer inventory
   */
  async getCustomerInventory(req, res, next) {
    try {
      const { customerId } = req.params;
      const { category, status, page, limit, sortBy, sortOrder } = req.query;
      
      logger.debug('Retrieving customer inventory', { customerId });
      
      const filters = {};
      if (category) filters.category = category;
      if (status) filters.status = status;
      
      const options = { page, limit, sortBy, sortOrder };
      
      const result = await inventoryService.getCustomerInventory(customerId, filters, options);
      
      res.json({
        success: true,
        data: result.items,
        pagination: result.pagination
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Get storage unit inventory
   */
  async getStorageUnitInventory(req, res, next) {
    try {
      const { storageUnitId } = req.params;
      const { includeStats } = req.query;
      
      logger.debug('Retrieving storage unit inventory', { storageUnitId });
      
      const options = { includeStats: includeStats === 'true' };
      const result = await inventoryService.getStorageUnitInventory(storageUnitId, options);
      
      res.json({
        success: true,
        data: result
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Update inventory item
   */
  async updateItem(req, res, next) {
    try {
      const { itemId } = req.params;
      
      logger.info('Updating inventory item', { itemId });
      
      const updatedItem = await inventoryService.updateItem(itemId, req.body);
      
      res.json({
        success: true,
        message: 'Inventory item updated successfully',
        data: updatedItem
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Delete inventory item
   */
  async deleteItem(req, res, next) {
    try {
      const { itemId } = req.params;
      
      logger.info('Deleting inventory item', { itemId });
      
      await inventoryService.deleteItem(itemId);
      
      res.json({
        success: true,
        message: 'Inventory item deleted successfully'
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Search inventory items
   */
  async searchItems(req, res, next) {
    try {
      const { customerId } = req.params;
      const { q: searchTerm, page, limit } = req.query;
      
      logger.debug('Searching inventory items', { customerId, searchTerm });
      
      if (!searchTerm) {
        return res.status(400).json({
          success: false,
          message: 'Search term is required'
        });
      }
      
      const options = { page, limit };
      const result = await inventoryService.searchItems(customerId, searchTerm, options);
      
      res.json({
        success: true,
        data: result.items,
        pagination: result.pagination
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Get inventory statistics
   */
  async getInventoryStats(req, res, next) {
    try {
      const { customerId } = req.params;
      
      logger.debug('Retrieving inventory statistics', { customerId });
      
      const stats = await inventoryService.getInventoryStats(customerId);
      
      res.json({
        success: true,
        data: stats
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Get item by barcode
   */
  async getItemByBarcode(req, res, next) {
    try {
      const { barcode } = req.params;
      
      logger.debug('Retrieving item by barcode', { barcode });
      
      const item = await inventoryService.getItemByBarcode(barcode);
      
      res.json({
        success: true,
        data: item
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Bulk import items
   */
  async bulkImportItems(req, res, next) {
    try {
      const { customerId } = req.params;
      const { items } = req.body;
      
      logger.info('Bulk importing items', { customerId, count: items?.length });
      
      if (!items || !Array.isArray(items) || items.length === 0) {
        return res.status(400).json({
          success: false,
          message: 'Items array is required and must not be empty'
        });
      }
      
      const result = await inventoryService.bulkImportItems(customerId, items);
      
      res.status(201).json({
        success: true,
        message: `Successfully imported ${result.length} items`,
        data: {
          imported: result.length,
          items: result
        }
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Health check
   */
  async healthCheck(req, res) {
    res.json({
      success: true,
      message: 'Customer Inventory Tracking Service is running',
      timestamp: new Date().toISOString(),
      version: process.env.npm_package_version || '1.0.0'
    });
  }
}

module.exports = new InventoryController();