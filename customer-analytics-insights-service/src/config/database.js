const mongoose = require('mongoose');
const logger = require('./logger');

/**
 * Database Configuration
 * 
 * MongoDB connection setup with analytics-specific optimizations.
 */

const connectDatabase = async () => {
    try {
        const mongoUri = process.env.MONGODB_URI || 'mongodb://localhost:27017/customer-analytics-insights';
        
        const options = {
            useNewUrlParser: true,
            useUnifiedTopology: true,
            maxPoolSize: 15, // Increased pool size for analytics workload
            serverSelectionTimeoutMS: 5000,
            socketTimeoutMS: 45000,
            family: 4,
            // Analytics-specific optimizations
            bufferMaxEntries: 0,
            bufferCommands: false,
            maxIdleTimeMS: 30000,
            writeConcern: {
                w: 1,
                j: false // Disable journaling for faster writes (analytics data)
            }
        };

        logger.info('Connecting to MongoDB (Analytics)...', { 
            uri: mongoUri.replace(/\/\/.*@/, '//***:***@') 
        });
        
        await mongoose.connect(mongoUri, options);
        
        logger.info('MongoDB connected successfully for analytics service');
        
        // Connection event listeners
        mongoose.connection.on('connected', () => {
            logger.info('MongoDB analytics connection established');
        });
        
        mongoose.connection.on('error', (err) => {
            logger.error('MongoDB analytics connection error:', err);
        });
        
        mongoose.connection.on('disconnected', () => {
            logger.warn('MongoDB analytics disconnected');
        });
        
        mongoose.connection.on('reconnected', () => {
            logger.info('MongoDB analytics reconnected');
        });
        
        // Set up analytics-specific indexes and configurations
        await setupAnalyticsIndexes();
        
        // Graceful shutdown
        process.on('SIGINT', async () => {
            try {
                await mongoose.connection.close();
                logger.info('MongoDB analytics connection closed through app termination');
                process.exit(0);
            } catch (error) {
                logger.error('Error during MongoDB disconnection:', error);
                process.exit(1);
            }
        });
        
    } catch (error) {
        logger.error('MongoDB analytics connection failed:', error);
        
        // Retry connection after 5 seconds
        setTimeout(() => {
            logger.info('Retrying MongoDB analytics connection...');
            connectDatabase();
        }, 5000);
    }
};

/**
 * Setup analytics-specific database indexes and configurations
 */
const setupAnalyticsIndexes = async () => {
    try {
        const db = mongoose.connection.db;
        
        // Set read preference for analytics queries
        mongoose.connection.db.readPreference = 'secondaryPreferred';
        
        // Create TTL index for automatic data cleanup
        await db.collection('analyticsevents').createIndex(
            { 'privacy.dataRetentionExpiry': 1 },
            { expireAfterSeconds: 0 }
        );
        
        logger.info('Analytics database indexes created successfully');
        
    } catch (error) {
        logger.error('Error setting up analytics indexes:', error);
    }
};

/**
 * Get database statistics for monitoring
 */
const getDatabaseStats = async () => {
    try {
        const db = mongoose.connection.db;
        const stats = await db.stats();
        
        return {
            collections: stats.collections,
            dataSize: stats.dataSize,
            indexSize: stats.indexSize,
            objects: stats.objects,
            avgObjSize: stats.avgObjSize
        };
    } catch (error) {
        logger.error('Error getting database statistics:', error);
        return null;
    }
};

module.exports = { 
    connectDatabase, 
    setupAnalyticsIndexes,
    getDatabaseStats 
};