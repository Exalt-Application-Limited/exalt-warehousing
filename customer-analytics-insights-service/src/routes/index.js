const express = require('express');
const analyticsController = require('../controllers/analyticsController');

const router = express.Router();

/**
 * Customer Analytics & Insights Routes
 * 
 * Comprehensive analytics API for customer behavior insights and business intelligence
 */

// Health check
router.get('/health', analyticsController.healthCheck);

// Event tracking
router.post('/events', analyticsController.trackEvent);
router.get('/events', analyticsController.getEvents);

// Customer insights
router.get('/customers/:customerId/insights', analyticsController.getCustomerInsights);
router.get('/customers/:customerId/journey', analyticsController.getCustomerJourney);
router.get('/customers/:customerId/predictions', analyticsController.getPredictiveInsights);

// Business analytics
router.get('/business/dashboard', analyticsController.getBusinessAnalytics);
router.get('/business/realtime', analyticsController.getRealTimeAnalytics);
router.get('/business/summary', analyticsController.getAnalyticsSummary);

// Advanced analytics
router.get('/funnel', analyticsController.getFunnelAnalysis);
router.get('/cohort', analyticsController.getCohortAnalysis);

// Data management
router.delete('/cleanup/expired', analyticsController.cleanupExpiredData);

// API info endpoint
router.get('/', (req, res) => {
    res.json({
        service: 'customer-analytics-insights-service',
        version: '1.0.0',
        description: 'Customer behavior analytics and business intelligence service',
        endpoints: {
            health: 'GET /health',
            events: {
                track: 'POST /events',
                query: 'GET /events'
            },
            customers: {
                insights: 'GET /customers/:customerId/insights',
                journey: 'GET /customers/:customerId/journey',
                predictions: 'GET /customers/:customerId/predictions'
            },
            business: {
                dashboard: 'GET /business/dashboard',
                realtime: 'GET /business/realtime',
                summary: 'GET /business/summary'
            },
            advanced: {
                funnel: 'GET /funnel?steps=step1,step2,step3',
                cohort: 'GET /cohort?cohortType=monthly'
            },
            dataManagement: {
                cleanup: 'DELETE /cleanup/expired'
            }
        },
        analytics: {
            capabilities: [
                'Customer Journey Mapping',
                'Behavior Analytics',
                'Predictive Insights',
                'Funnel Analysis',
                'Cohort Analysis',
                'Real-time Dashboards',
                'Business Intelligence',
                'GDPR Compliance'
            ],
            eventTypes: [
                'STORAGE_SEARCH',
                'UNIT_VIEW',
                'UNIT_BOOKING',
                'PAYMENT_COMPLETED',
                'INVENTORY_ADDED',
                'SUPPORT_CONTACTED'
            ]
        }
    });
});

module.exports = router;
