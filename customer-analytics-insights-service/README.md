# Customer behavior analytics and insights service

## Overview
Customer behavior analytics and insights service - Node.js microservice for the Gogidix Customer Marketplace.

## Features
- Express.js REST API
- MongoDB integration
- Redis caching support
- Kafka messaging
- Comprehensive error handling
- Security middleware (Helmet, CORS, Rate limiting)
- Structured logging with Winston
- Health checks and monitoring
- Docker support

## Quick Start

### Development
```bash
npm install
cp .env.example .env
npm run dev
```

### Production
```bash
npm start
```

### Testing
```bash
npm test
npm run test:watch
```

### Docker
```bash
npm run docker:build
npm run docker:run
```

## API Endpoints
- `GET /health` - Health check
- `GET /api/v1` - API information

## Environment Variables
See `.env.example` for all configuration options.

## Architecture
Built following microservices best practices with:
- Clean architecture separation
- Comprehensive error handling
- Security-first approach
- Observability and monitoring
- Graceful shutdown handling
