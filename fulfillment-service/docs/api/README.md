# fulfillment-service - API Documentation

## Table of Contents
1. [Authentication](#authentication)
2. [Rate Limiting](#rate-limiting)
3. [Error Handling](#error-handling)
4. [Versioning](#versioning)
5. [Endpoints](#endpoints)
6. [Models](#models)
7. [Webhooks](#webhooks)
8. [Deprecation Policy](#deprecation-policy)

## Authentication

All API endpoints require authentication using JWT tokens. Include the token in the `Authorization` header:

```http
GET /api/v1/resource HTTP/1.1
Host: api.example.com
Authorization: Bearer your_jwt_token_here
```

### Obtaining a Token

```http
POST /auth/token HTTP/1.1
Content-Type: application/x-www-form-urlencoded

grant_type=password&username=user&password=pass&client_id=web
```

## Rate Limiting

- **Rate Limit**: 1000 requests per minute per IP
- **Response Headers**:
  - `X-RateLimit-Limit`: Request limit per time window
  - `X-RateLimit-Remaining`: Remaining requests in current window
  - `X-RateLimit-Reset`: Time when the rate limit resets (UTC epoch seconds)

## Error Handling

### Standard Error Response

```json
{
  "timestamp": "2025-06-25T21:23:10.123Z",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found",
  "path": "/api/v1/resource/123",
  "requestId": "a1b2c3d4e5f6g7h8"
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

## Versioning

API versioning is done through the URL path:

```
/api/v1/resource     # Current stable version
/api/v2/resource     # Future version (when available)
```

## Endpoints

### 1. Resource Collection

#### List Resources

```http
GET /api/v1/resources
```

**Query Parameters**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| page | integer | No | Page number (default: 0) |
| size | integer | No | Page size (default: 20, max: 100) |
| sort | string | No | Sort by field (e.g., `name,asc`) |
| filter | string | No | Filter criteria |

**Response**

```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "Example Resource",
      "description": "Example description",
      "createdAt": "2025-06-25T21:23:10.123Z",
      "updatedAt": "2025-06-25T21:23:10.123Z"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 0,
    "pageSize": 20,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "size": 20,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 1,
  "first": true,
  "empty": false
}
```

### 2. Single Resource

#### Get Resource by ID

```http
GET /api/v1/resources/{id}
```

**Path Parameters**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | string | Yes | Resource ID |

**Response**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Example Resource",
  "description": "Example description",
  "createdAt": "2025-06-25T21:23:10.123Z",
  "updatedAt": "2025-06-25T21:23:10.123Z"
}
```

#### Create Resource

```http
POST /api/v1/resources
Content-Type: application/json

{
  "name": "New Resource",
  "description": "New resource description"
}
```

**Request Body**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name | string | Yes | Resource name |
| description | string | No | Resource description |

**Response**

```http
HTTP/1.1 201 Created
Location: /api/v1/resources/550e8400-e29b-41d4-a716-446655440000
```

## Models

### Resource

```json
{
  "id": "string (uuid)",
  "name": "string",
  "description": "string",
  "createdAt": "string (ISO 8601)",
  "updatedAt": "string (ISO 8601)"
}
```

### Error

```json
{
  "timestamp": "string (ISO 8601)",
  "status": "number",
  "error": "string",
  "message": "string",
  "path": "string",
  "requestId": "string"
}
```

## Webhooks

### Available Webhooks

| Event | Description | Payload |
|-------|-------------|---------|
| resource.created | Triggered when a new resource is created | [Resource](#resource) |
| resource.updated | Triggered when a resource is updated | [Resource](#resource) |
| resource.deleted | Triggered when a resource is deleted | `{ "id": "string" }` |

### Webhook Payload Example

```json
{
  "event": "resource.created",
  "timestamp": "2025-06-25T21:23:10.123Z",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Example Resource",
    "description": "Example description",
    "createdAt": "2025-06-25T21:23:10.123Z",
    "updatedAt": "2025-06-25T21:23:10.123Z"
  }
}
```

## Deprecation Policy

1. **Announcement**: Deprecated endpoints will be announced at least 3 months before removal
2. **Documentation**: Deprecated endpoints will be clearly marked in the documentation
3. **Sunset Period**: Deprecated endpoints will continue to work for at least 6 months
4. **Removal**: After the sunset period, deprecated endpoints will be removed in the next major version

### Currently Deprecated Endpoints

| Endpoint | Deprecated In | Removal Version | Alternative |
|----------|---------------|-----------------|-------------|
| /api/v0/resources | v1.0.0 | v2.0.0 | /api/v1/resources |
