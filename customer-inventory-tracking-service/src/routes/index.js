const express = require('express');
const inventoryController = require('../controllers/inventoryController');

const router = express.Router();

/**
 * Inventory Tracking Routes
 * 
 * RESTful API endpoints for customer inventory management
 */

// Health check
router.get('/health', inventoryController.healthCheck);

// Create new inventory item
router.post('/inventory/items', inventoryController.createItem);

// Get item by ID
router.get('/inventory/items/:itemId', inventoryController.getItemById);

// Get item by barcode
router.get('/inventory/barcode/:barcode', inventoryController.getItemByBarcode);

// Update inventory item
router.put('/inventory/items/:itemId', inventoryController.updateItem);

// Delete inventory item
router.delete('/inventory/items/:itemId', inventoryController.deleteItem);

// Customer inventory endpoints
router.get('/customers/:customerId/inventory', inventoryController.getCustomerInventory);
router.get('/customers/:customerId/inventory/search', inventoryController.searchItems);
router.get('/customers/:customerId/inventory/stats', inventoryController.getInventoryStats);
router.post('/customers/:customerId/inventory/bulk-import', inventoryController.bulkImportItems);

// Storage unit inventory endpoints
router.get('/storage-units/:storageUnitId/inventory', inventoryController.getStorageUnitInventory);

// API info endpoint
router.get('/', (req, res) => {
    res.json({
        service: 'customer-inventory-tracking-service',
        version: '1.0.0',
        description: 'Customer inventory tracking and management service',
        endpoints: {
            health: 'GET /health',
            items: {
                create: 'POST /inventory/items',
                get: 'GET /inventory/items/:itemId',
                update: 'PUT /inventory/items/:itemId',
                delete: 'DELETE /inventory/items/:itemId',
                byBarcode: 'GET /inventory/barcode/:barcode'
            },
            customers: {
                inventory: 'GET /customers/:customerId/inventory',
                search: 'GET /customers/:customerId/inventory/search?q=searchTerm',
                stats: 'GET /customers/:customerId/inventory/stats',
                bulkImport: 'POST /customers/:customerId/inventory/bulk-import'
            },
            storageUnits: {
                inventory: 'GET /storage-units/:storageUnitId/inventory'
            }
        }
    });
});

module.exports = router;
