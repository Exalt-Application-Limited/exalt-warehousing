const analyticsService = require('../services/analyticsService');
const logger = require('../config/logger');

/**
 * Analytics Controller
 * 
 * HTTP request handlers for customer analytics and business insights.
 */
class AnalyticsController {
  
  /**
   * Track customer event
   */
  async trackEvent(req, res, next) {
    try {
      logger.info('Tracking analytics event', { 
        eventType: req.body.eventType, 
        customerId: req.body.customerId 
      });
      
      const event = await analyticsService.trackEvent(req.body);
      
      res.status(201).json({
        success: true,
        message: 'Event tracked successfully',
        data: {
          eventId: event.eventId,
          timestamp: event.timestamp
        }
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Get customer insights dashboard
   */
  async getCustomerInsights(req, res, next) {
    try {
      const { customerId } = req.params;
      const { dateRange, includeJourney } = req.query;
      
      logger.debug('Retrieving customer insights', { customerId });
      
      const options = {};
      if (dateRange) {
        const [from, to] = dateRange.split(',');
        options.dateRange = { from: new Date(from), to: new Date(to) };
      }
      if (includeJourney === 'true') {
        options.includeJourney = true;
      }
      
      const insights = await analyticsService.getCustomerInsights(customerId, options);
      
      res.json({
        success: true,
        data: insights
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Get business analytics dashboard
   */
  async getBusinessAnalytics(req, res, next) {
    try {
      const { dateRange, granularity, segments } = req.query;
      
      logger.debug('Retrieving business analytics');
      
      const options = {};
      if (dateRange) {
        const [from, to] = dateRange.split(',');
        options.dateRange = { from: new Date(from), to: new Date(to) };
      }
      if (granularity) options.granularity = granularity;
      if (segments) options.segments = segments.split(',');
      
      const analytics = await analyticsService.getBusinessAnalytics(options);
      
      res.json({
        success: true,
        data: analytics
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Get real-time analytics
   */
  async getRealTimeAnalytics(req, res, next) {
    try {
      logger.debug('Retrieving real-time analytics');
      
      const analytics = await analyticsService.getRealTimeAnalytics();
      
      res.json({
        success: true,
        data: analytics
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Get predictive insights for customer
   */
  async getPredictiveInsights(req, res, next) {
    try {
      const { customerId } = req.params;
      
      logger.debug('Generating predictive insights', { customerId });
      
      const insights = await analyticsService.getPredictiveInsights(customerId);
      
      res.json({
        success: true,
        data: insights
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Get funnel analysis
   */
  async getFunnelAnalysis(req, res, next) {
    try {
      const { steps, dateRange, segments } = req.query;
      
      if (!steps) {
        return res.status(400).json({
          success: false,
          message: 'Funnel steps are required'
        });
      }
      
      logger.debug('Analyzing conversion funnel', { steps });
      
      const funnelSteps = steps.split(',');
      const options = {};
      
      if (dateRange) {
        const [from, to] = dateRange.split(',');
        options.dateRange = { from: new Date(from), to: new Date(to) };
      }
      if (segments) options.segments = segments.split(',');
      
      const analysis = await analyticsService.getFunnelAnalysis(funnelSteps, options);
      
      res.json({
        success: true,
        data: analysis
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Get cohort analysis
   */
  async getCohortAnalysis(req, res, next) {
    try {
      const { cohortType, retentionPeriods, startDate } = req.query;
      
      logger.debug('Generating cohort analysis');
      
      const options = {};
      if (cohortType) options.cohortType = cohortType;
      if (retentionPeriods) {
        options.retentionPeriods = retentionPeriods.split(',').map(Number);
      }
      if (startDate) options.startDate = new Date(startDate);
      
      const analysis = await analyticsService.getCohortAnalysis(options);
      
      res.json({
        success: true,
        data: analysis
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Get customer journey
   */
  async getCustomerJourney(req, res, next) {
    try {
      const { customerId } = req.params;
      const { sessionId, limit = 50 } = req.query;
      
      logger.debug('Retrieving customer journey', { customerId, sessionId });
      
      const AnalyticsEvent = require('../models/AnalyticsEvent');
      const journey = await AnalyticsEvent.getCustomerJourney(customerId, sessionId);
      
      res.json({
        success: true,
        data: {
          customerId,
          sessionId,
          journey: journey.slice(0, limit),
          totalEvents: journey.length
        }
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Get analytics summary
   */
  async getAnalyticsSummary(req, res, next) {
    try {
      const { period = '30d' } = req.query;
      
      logger.debug('Generating analytics summary', { period });
      
      // Parse period (e.g., '7d', '30d', '90d')
      const days = parseInt(period.replace('d', ''));
      const from = new Date();
      from.setDate(from.getDate() - days);
      
      const dateRange = { from, to: new Date() };
      
      const [businessAnalytics, realTimeData] = await Promise.all([
        analyticsService.getBusinessAnalytics({ dateRange }),
        analyticsService.getRealTimeAnalytics()
      ]);
      
      res.json({
        success: true,
        data: {
          period,
          dateRange,
          business: businessAnalytics,
          realTime: realTimeData,
          generatedAt: new Date()
        }
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Get events by criteria
   */
  async getEvents(req, res, next) {
    try {
      const { 
        customerId, 
        eventType, 
        dateRange, 
        page = 1, 
        limit = 50,
        sortBy = 'timestamp',
        sortOrder = 'desc'
      } = req.query;
      
      logger.debug('Retrieving analytics events', { customerId, eventType });
      
      const AnalyticsEvent = require('../models/AnalyticsEvent');
      const query = {};
      
      if (customerId) query.customerId = customerId;
      if (eventType) query.eventType = eventType;
      
      if (dateRange) {
        const [from, to] = dateRange.split(',');
        query.timestamp = { 
          $gte: new Date(from), 
          $lte: new Date(to) 
        };
      }
      
      const skip = (page - 1) * limit;
      const sort = { [sortBy]: sortOrder === 'desc' ? -1 : 1 };
      
      const [events, totalCount] = await Promise.all([
        AnalyticsEvent.find(query)
          .sort(sort)
          .skip(skip)
          .limit(parseInt(limit))
          .lean(),
        AnalyticsEvent.countDocuments(query)
      ]);
      
      res.json({
        success: true,
        data: events,
        pagination: {
          page: parseInt(page),
          limit: parseInt(limit),
          totalCount,
          totalPages: Math.ceil(totalCount / limit)
        }
      });
    } catch (error) {
      next(error);
    }
  }
  
  /**
   * Delete old analytics data (GDPR compliance)
   */
  async cleanupExpiredData(req, res, next) {
    try {
      logger.info('Starting analytics data cleanup');
      
      const AnalyticsEvent = require('../models/AnalyticsEvent');
      const now = new Date();
      
      const result = await AnalyticsEvent.deleteMany({
        'privacy.dataRetentionExpiry': { $lt: now }
      });
      
      logger.info('Analytics data cleanup completed', { 
        deletedCount: result.deletedCount 
      });
      
      res.json({
        success: true,
        message: 'Data cleanup completed',
        data: {
          deletedRecords: result.deletedCount,
          cleanupTime: new Date()
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
    try {
      const AnalyticsEvent = require('../models/AnalyticsEvent');
      
      // Check database connectivity
      const eventCount = await AnalyticsEvent.countDocuments();
      
      res.json({
        success: true,
        message: 'Customer Analytics Insights Service is running',
        timestamp: new Date().toISOString(),
        version: process.env.npm_package_version || '1.0.0',
        status: {
          database: 'connected',
          totalEvents: eventCount
        }
      });
    } catch (error) {
      res.status(503).json({
        success: false,
        message: 'Service health check failed',
        error: error.message
      });
    }
  }
}

module.exports = new AnalyticsController();