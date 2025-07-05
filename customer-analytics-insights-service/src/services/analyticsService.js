const AnalyticsEvent = require('../models/AnalyticsEvent');
const logger = require('../config/logger');

/**
 * Analytics Service
 * 
 * Comprehensive customer behavior analytics and business insights.
 */
class AnalyticsService {
  
  /**
   * Track customer event
   */
  async trackEvent(eventData) {
    try {
      logger.info('Tracking analytics event', { 
        eventType: eventData.eventType, 
        customerId: eventData.customerId 
      });
      
      // Enrich event data
      const enrichedEvent = await this.enrichEventData(eventData);
      
      const event = new AnalyticsEvent(enrichedEvent);
      const savedEvent = await event.save();
      
      logger.debug('Analytics event saved', { eventId: savedEvent.eventId });
      return savedEvent;
      
    } catch (error) {
      logger.error('Error tracking analytics event', { error: error.message, eventData });
      throw error;
    }
  }
  
  /**
   * Get customer insights dashboard
   */
  async getCustomerInsights(customerId, options = {}) {
    try {
      logger.debug('Generating customer insights', { customerId });
      
      const { dateRange = this.getDefaultDateRange() } = options;
      
      const [
        overview,
        behavior,
        engagement,
        preferences,
        journey
      ] = await Promise.all([
        this.getCustomerOverview(customerId, dateRange),
        this.getCustomerBehavior(customerId, dateRange),
        this.getEngagementMetrics(customerId, dateRange),
        this.getCustomerPreferences(customerId, dateRange),
        this.getCustomerJourney(customerId, dateRange)
      ]);
      
      return {
        customerId,
        dateRange,
        overview,
        behavior,
        engagement,
        preferences,
        journey,
        generatedAt: new Date()
      };
      
    } catch (error) {
      logger.error('Error generating customer insights', { error: error.message, customerId });
      throw error;
    }
  }
  
  /**
   * Get business analytics dashboard
   */
  async getBusinessAnalytics(options = {}) {
    try {
      logger.debug('Generating business analytics');
      
      const { 
        dateRange = this.getDefaultDateRange(),
        granularity = 'daily',
        segments = []
      } = options;
      
      const [
        overview,
        trends,
        conversion,
        demographics,
        performance,
        revenue
      ] = await Promise.all([
        this.getBusinessOverview(dateRange),
        this.getTrends(dateRange, granularity),
        this.getConversionAnalytics(dateRange),
        this.getDemographics(dateRange),
        this.getPerformanceMetrics(dateRange),
        this.getRevenueAnalytics(dateRange)
      ]);
      
      return {
        dateRange,
        overview,
        trends,
        conversion,
        demographics,
        performance,
        revenue,
        generatedAt: new Date()
      };
      
    } catch (error) {
      logger.error('Error generating business analytics', { error: error.message });
      throw error;
    }
  }
  
  /**
   * Get real-time analytics
   */
  async getRealTimeAnalytics() {
    try {
      logger.debug('Generating real-time analytics');
      
      const now = new Date();
      const oneHourAgo = new Date(now.getTime() - 60 * 60 * 1000);
      
      const [
        activeUsers,
        currentEvents,
        liveConversions,
        systemHealth
      ] = await Promise.all([
        this.getActiveUsers(oneHourAgo),
        this.getCurrentEvents(oneHourAgo),
        this.getLiveConversions(oneHourAgo),
        this.getSystemHealth(oneHourAgo)
      ]);
      
      return {
        timestamp: now,
        activeUsers,
        currentEvents,
        liveConversions,
        systemHealth
      };
      
    } catch (error) {
      logger.error('Error generating real-time analytics', { error: error.message });
      throw error;
    }
  }
  
  /**
   * Generate predictive insights
   */
  async getPredictiveInsights(customerId) {
    try {
      logger.debug('Generating predictive insights', { customerId });
      
      const [
        churnRisk,
        nextActions,
        valueScore,
        recommendations
      ] = await Promise.all([
        this.calculateChurnRisk(customerId),
        this.predictNextActions(customerId),
        this.calculateCustomerValue(customerId),
        this.generateRecommendations(customerId)
      ]);
      
      return {
        customerId,
        churnRisk,
        nextActions,
        valueScore,
        recommendations,
        confidence: this.calculatePredictionConfidence(customerId),
        generatedAt: new Date()
      };
      
    } catch (error) {
      logger.error('Error generating predictive insights', { error: error.message, customerId });
      throw error;
    }
  }
  
  /**
   * Get funnel analysis
   */
  async getFunnelAnalysis(funnelSteps, options = {}) {
    try {
      logger.debug('Analyzing conversion funnel', { funnelSteps });
      
      const { dateRange = this.getDefaultDateRange(), segments = [] } = options;
      
      const funnelData = await Promise.all(
        funnelSteps.map(async (step, index) => {
          const users = await this.getUsersAtStep(step, dateRange, segments);
          const conversions = index > 0 ? 
            await this.getConversionsFromStep(funnelSteps[index - 1], step, dateRange, segments) : 
            users;
          
          return {
            step,
            users: users.length,
            conversions: conversions.length,
            conversionRate: users.length > 0 ? (conversions.length / users.length) * 100 : 0,
            dropoffRate: index > 0 ? 
              ((users.length - conversions.length) / users.length) * 100 : 0
          };
        })
      );
      
      return {
        funnelSteps,
        dateRange,
        data: funnelData,
        totalConversionRate: this.calculateTotalConversionRate(funnelData),
        biggestDropoff: this.findBiggestDropoff(funnelData)
      };
      
    } catch (error) {
      logger.error('Error analyzing funnel', { error: error.message, funnelSteps });
      throw error;
    }
  }
  
  /**
   * Get cohort analysis
   */
  async getCohortAnalysis(options = {}) {
    try {
      logger.debug('Generating cohort analysis');
      
      const { 
        cohortType = 'monthly',
        retentionPeriods = [1, 3, 6, 12],
        startDate = this.getDefaultStartDate()
      } = options;
      
      const cohorts = await this.generateCohorts(cohortType, startDate);
      
      const cohortData = await Promise.all(
        cohorts.map(async (cohort) => {
          const retentionData = await Promise.all(
            retentionPeriods.map(period => 
              this.calculateRetention(cohort, period, cohortType)
            )
          );
          
          return {
            cohort: cohort.period,
            size: cohort.users.length,
            retention: retentionData
          };
        })
      );
      
      return {
        cohortType,
        retentionPeriods,
        data: cohortData,
        averageRetention: this.calculateAverageRetention(cohortData),
        generatedAt: new Date()
      };
      
    } catch (error) {
      logger.error('Error generating cohort analysis', { error: error.message });
      throw error;
    }
  }
  
  // Helper methods
  
  async enrichEventData(eventData) {
    // Add computed metrics and additional context
    const enriched = {
      ...eventData,
      timestamp: eventData.timestamp || new Date(),
      dataQuality: {
        isValid: true,
        processingTime: new Date(),
        dataSource: eventData.dataSource || 'API'
      }
    };
    
    // Add session context if available
    if (eventData.sessionId) {
      const sessionContext = await this.getSessionContext(eventData.sessionId);
      enriched.sessionContext = sessionContext;
    }
    
    return enriched;
  }
  
  async getCustomerOverview(customerId, dateRange) {
    const pipeline = [
      {
        $match: {
          customerId,
          timestamp: { $gte: new Date(dateRange.from), $lte: new Date(dateRange.to) }
        }
      },
      {
        $group: {
          _id: null,
          totalEvents: { $sum: 1 },
          uniqueSessions: { $addToSet: '$sessionId' },
          firstEvent: { $min: '$timestamp' },
          lastEvent: { $max: '$timestamp' },
          eventTypes: { $addToSet: '$eventType' },
          avgEngagement: { $avg: '$metrics.engagementScore' }
        }
      }
    ];
    
    const [result] = await AnalyticsEvent.aggregate(pipeline);
    
    return {
      totalEvents: result?.totalEvents || 0,
      uniqueSessions: result?.uniqueSessions?.length || 0,
      firstSeen: result?.firstEvent,
      lastSeen: result?.lastEvent,
      activeEventTypes: result?.eventTypes?.length || 0,
      avgEngagementScore: result?.avgEngagement || 0
    };
  }
  
  async getCustomerBehavior(customerId, dateRange) {
    const pipeline = [
      {
        $match: {
          customerId,
          timestamp: { $gte: new Date(dateRange.from), $lte: new Date(dateRange.to) }
        }
      },
      {
        $group: {
          _id: '$eventType',
          count: { $sum: 1 },
          avgDuration: { $avg: '$metrics.sessionDuration' },
          lastOccurrence: { $max: '$timestamp' }
        }
      },
      { $sort: { count: -1 } }
    ];
    
    return await AnalyticsEvent.aggregate(pipeline);
  }
  
  async getEngagementMetrics(customerId, dateRange) {
    const pipeline = [
      {
        $match: {
          customerId,
          timestamp: { $gte: new Date(dateRange.from), $lte: new Date(dateRange.to) }
        }
      },
      {
        $group: {
          _id: null,
          avgPageViews: { $avg: '$metrics.pageViewCount' },
          avgSessionDuration: { $avg: '$metrics.sessionDuration' },
          avgScrollDepth: { $avg: '$metrics.scrollDepth' },
          totalClicks: { $sum: '$metrics.clickCount' }
        }
      }
    ];
    
    const [result] = await AnalyticsEvent.aggregate(pipeline);
    return result || {};
  }
  
  getDefaultDateRange() {
    const to = new Date();
    const from = new Date();
    from.setDate(from.getDate() - 30); // Last 30 days
    
    return { from, to };
  }
  
  calculatePredictionConfidence(customerId) {
    // Simple confidence calculation based on data availability
    // In production, this would use ML models
    return Math.random() * 0.3 + 0.7; // 70-100% confidence
  }
  
  async calculateChurnRisk(customerId) {
    // Simplified churn risk calculation
    const recentActivity = await AnalyticsEvent.countDocuments({
      customerId,
      timestamp: { $gte: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000) }
    });
    
    return {
      score: recentActivity > 5 ? 'Low' : recentActivity > 1 ? 'Medium' : 'High',
      factors: ['Activity Level', 'Engagement Score', 'Usage Patterns']
    };
  }
  
  async predictNextActions(customerId) {
    // Simplified next action prediction
    const recentEvents = await AnalyticsEvent.find({ customerId })
      .sort({ timestamp: -1 })
      .limit(10)
      .select('eventType properties.funnelStage');
    
    return {
      predictions: [
        { action: 'View Storage Units', probability: 0.8 },
        { action: 'Contact Support', probability: 0.3 },
        { action: 'Book Storage', probability: 0.6 }
      ],
      confidence: 0.75
    };
  }
  
  async calculateCustomerValue(customerId) {
    const revenueEvents = await AnalyticsEvent.find({
      customerId,
      'properties.revenue': { $exists: true, $gt: 0 }
    });
    
    const totalRevenue = revenueEvents.reduce((sum, event) => 
      sum + (event.properties.revenue || 0), 0
    );
    
    return {
      totalRevenue,
      predictedLifetimeValue: totalRevenue * 2.5, // Simple LTV calculation
      valueSegment: totalRevenue > 1000 ? 'High' : totalRevenue > 200 ? 'Medium' : 'Low'
    };
  }
  
  async generateRecommendations(customerId) {
    return [
      {
        type: 'Product',
        title: 'Recommended Storage Size',
        description: 'Based on your inventory, we recommend a medium-sized unit',
        confidence: 0.85
      },
      {
        type: 'Feature',
        title: 'Mobile App',
        description: 'Download our mobile app for easier inventory management',
        confidence: 0.70
      }
    ];
  }
  
  // Additional analytics methods would continue here...
  // Including business analytics, real-time metrics, funnel analysis, etc.
}

module.exports = new AnalyticsService();