const mongoose = require('mongoose');
const logger = require('./logger');

/**
 * Database Configuration
 * 
 * MongoDB connection setup with retry logic and monitoring.
 */

const connectDatabase = async () => {
    try {
        const mongoUri = process.env.MONGODB_URI || 'mongodb://localhost:27017/customer-inventory-tracking';
        
        const options = {
            useNewUrlParser: true,
            useUnifiedTopology: true,
            maxPoolSize: 10,
            serverSelectionTimeoutMS: 5000,
            socketTimeoutMS: 45000,
            family: 4
        };

        logger.info('Connecting to MongoDB...', { uri: mongoUri.replace(/\/\/.*@/, '//***:***@') });
        
        await mongoose.connect(mongoUri, options);
        
        logger.info('MongoDB connected successfully');
        
        // Connection event listeners
        mongoose.connection.on('connected', () => {
            logger.info('MongoDB connection established');
        });
        
        mongoose.connection.on('error', (err) => {
            logger.error('MongoDB connection error:', err);
        });
        
        mongoose.connection.on('disconnected', () => {
            logger.warn('MongoDB disconnected');
        });
        
        mongoose.connection.on('reconnected', () => {
            logger.info('MongoDB reconnected');
        });
        
        // Graceful shutdown
        process.on('SIGINT', async () => {
            try {
                await mongoose.connection.close();
                logger.info('MongoDB connection closed through app termination');
                process.exit(0);
            } catch (error) {
                logger.error('Error during MongoDB disconnection:', error);
                process.exit(1);
            }
        });
        
    } catch (error) {
        logger.error('MongoDB connection failed:', error);
        
        // Retry connection after 5 seconds
        setTimeout(() => {
            logger.info('Retrying MongoDB connection...');
            connectDatabase();
        }, 5000);
    }
};

module.exports = { connectDatabase };