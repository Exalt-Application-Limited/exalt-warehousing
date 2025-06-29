# Warehousing Staging Service API Documentation

## Overview
This document provides comprehensive documentation for the Warehousing Staging Service API. The API follows RESTful principles and uses JSON for request/response payloads.

## Base URL
```
https://api.staging.warehousing.example.com/v1
```

## Authentication
All API endpoints require authentication using JWT (JSON Web Tokens). Include the token in the `Authorization` header:

```
Authorization: Bearer <your_jwt_token>
```

## Rate Limiting
- **Rate Limit**: 1000 requests per minute per IP
- **Headers**:
  - `X-RateLimit-Limit`: Request limit per time window
  - `X-RateLimit-Remaining`: Remaining requests in current window
  - `X-RateLimit-Reset`: Time when the rate limit resets (UTC epoch seconds)

## Error Handling
### Standard Error Response
```json
{
  "timestamp": "2025-06-26T00:10:15.123Z",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found",
  "path": "/api/v1/resource/123"
}
```

### Common Error Codes
| Status Code | Description |
|-------------|-------------|
| 400 | Bad Request - Invalid request format |
| 401 | Unauthorized - Authentication required |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 429 | Too Many Requests - Rate limit exceeded |
| 500 | Internal Server Error - Server error |

## API Endpoints

### 1. Environment Management

#### List All Staging Environments
```http
GET /api/v1/environments
```

**Query Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| status | string | No | Filter by status (active, inactive) |
| page | integer | No | Page number (default: 0) |
| size | integer | No | Page size (default: 20, max: 100) |

**Response**
```json
{
  "content": [
    {
      "id": "env-123",
      "name": "staging-1",
      "status": "active",
      "createdAt": "2025-06-25T10:30:00Z",
      "updatedAt": "2025-06-25T10:30:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 2. Deployment Management

#### Create New Deployment
```http
POST /api/v1/deployments
Content-Type: application/json

{
  "environmentId": "env-123",
  "version": "1.0.0",
  "notes": "Initial deployment"
}
```

**Request Body**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| environmentId | string | Yes | Target environment ID |
| version | string | Yes | Version to deploy |
| notes | string | No | Deployment notes |

**Response**
```http
HTTP/1.1 201 Created
Location: /api/v1/deployments/dep-123
```

#### Get Deployment Status
```http
GET /api/v1/deployments/{deploymentId}
```

**Path Parameters**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| deploymentId | string | Yes | Deployment ID |

**Response**
```json
{
  "id": "dep-123",
  "environmentId": "env-123",
  "version": "1.0.0",
  "status": "in_progress",
  "startedAt": "2025-06-26T10:30:00Z",
  "completedAt": null,
  "logs": [
    {
      "timestamp": "2025-06-26T10:30:05Z",
      "level": "INFO",
      "message": "Deployment started"
    }
  ]
}
```

## Webhooks

### Available Webhooks

| Event | Description | Payload |
|-------|-------------|---------|
| deployment.started | Triggered when deployment starts | [Deployment](#get-deployment-status) |
| deployment.completed | Triggered when deployment completes | [Deployment](#get-deployment-status) |
| deployment.failed | Triggered when deployment fails | [Deployment](#get-deployment-status) with error details |

### Webhook Payload Example
```json
{
  "event": "deployment.completed",
  "timestamp": "2025-06-26T10:35:00Z",
  "data": {
    "id": "dep-123",
    "environmentId": "env-123",
    "version": "1.0.0",
    "status": "completed",
    "startedAt": "2025-06-26T10:30:00Z",
    "completedAt": "2025-06-26T10:35:00Z"
  }
}
```

## Versioning
API versioning is done through the URL path (e.g., `/api/v1/...`). Breaking changes will be introduced in new API versions with appropriate deprecation notices.

## Deprecation Policy
1. Endpoints will be marked as deprecated at least 3 months before removal
2. Deprecated endpoints will continue to function during the deprecation period
3. Clients should update to use the new endpoints before the removal date

## Support
For API support, please contact [support email] or open an issue in our [issue tracker].
