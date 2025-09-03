# API Gateway Documentation

## Overview

The Cloud Interview Prep API Gateway provides a secure, rate-limited, and monitored interface to your Firebase Cloud Functions. It implements enterprise-grade security features and rate limiting to protect your backend services.

## Features

### 🛡️ Security Features
- **Firebase Authentication**: All protected endpoints require valid Firebase ID tokens
- **Rate Limiting**: Per-user and per-IP rate limiting with burst protection
- **Security Headers**: XSS protection, content type validation, frame options
- **Request Validation**: User-Agent validation, request size limits
- **IP Blocking**: Automatic blocking of suspicious IP addresses
- **User Monitoring**: Suspicious user detection and blocking
- **Security Logging**: Comprehensive logging of security events

### 📊 Rate Limiting
- **Authenticated Users**: 100 requests per 15 minutes, 20 burst per minute
- **Unauthenticated Users**: 50 requests per 15 minutes, 10 burst per minute
- **Headers**: Rate limit information included in response headers

### 🔍 Monitoring
- **Health Checks**: Real-time health monitoring
- **Gateway Status**: API Gateway operational status
- **Security Events**: Logged to Firestore for analysis

## API Endpoints

### Public Endpoints (No Authentication Required)

#### Health Check
```
GET /api/health
```
Returns server health status and uptime information.

#### Gateway Status
```
GET /api/gateway/status
```
Returns API Gateway operational status and security metrics.

#### Gateway Info
```
GET /api/gateway/info
```
Returns comprehensive API Gateway information, features, and rate limits.

#### API Information
```
GET /
```
Returns general API information and available endpoints.

### Protected Endpoints (Authentication Required)

#### Get All Topics
```
GET /api/topics
Authorization: Bearer <firebase_id_token>
```
Returns all available interview topics.

#### Get Specific Topic
```
GET /api/topics/{topicId}
Authorization: Bearer <firebase_id_token>
```
Returns details for a specific topic.

#### Get Questions by Difficulty
```
GET /api/questions/{topicId}/{difficulty}
Authorization: Bearer <firebase_id_token>
```
Returns questions for a specific topic and difficulty level.

#### Get All Questions for Topic
```
GET /api/questions/{topicId}
Authorization: Bearer <firebase_id_token>
```
Returns all questions for a topic across all difficulty levels.

#### Test Authentication
```
GET /api/test-auth
Authorization: Bearer <firebase_id_token>
```
Tests authentication and returns user information.

## Rate Limiting

### Headers
All responses include rate limiting headers:
- `X-RateLimit-Limit`: Maximum requests allowed
- `X-RateLimit-Remaining`: Requests remaining in current window
- `X-RateLimit-Reset`: Unix timestamp when the rate limit resets
- `X-RateLimit-Burst-Limit`: Maximum burst requests allowed
- `X-RateLimit-Burst-Remaining`: Burst requests remaining

### Rate Limit Exceeded Response
When rate limits are exceeded, the API returns:
```json
{
  "success": false,
  "error": "Rate limit exceeded",
  "message": "Too many requests. Please try again later.",
  "retryAfter": 300,
  "limit": 100,
  "remaining": 0
}
```

## Security

### Authentication
All protected endpoints require a valid Firebase ID token in the Authorization header:
```
Authorization: Bearer <firebase_id_token>
```

### Security Headers
All responses include security headers:
- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `X-XSS-Protection: 1; mode=block`
- `Strict-Transport-Security: max-age=31536000; includeSubDomains`
- `Referrer-Policy: strict-origin-when-cross-origin`
- `Content-Security-Policy: default-src 'self'`

### Request Validation
- **User-Agent**: Required header, blocks suspicious patterns
- **Request Size**: Maximum 1MB per request
- **Content-Type**: Validates request content types

## Error Handling

### Authentication Errors
```json
{
  "success": false,
  "error": "Authentication required",
  "message": "Please provide a valid Firebase ID token"
}
```

### Rate Limit Errors
```json
{
  "success": false,
  "error": "Rate limit exceeded",
  "message": "Too many requests. Please try again later.",
  "retryAfter": 300
}
```

### Security Errors
```json
{
  "success": false,
  "error": "Access denied",
  "message": "Your IP address has been blocked"
}
```

## Deployment

### Prerequisites
- Firebase CLI installed
- Firebase project configured
- Node.js 22+ installed

### Deploy API Gateway
```bash
cd backend/functions
./deploy-gateway.sh
```

### Manual Deployment
```bash
cd backend/functions
npm install
firebase deploy --only functions
```

## Monitoring

### Firestore Collections
- `rate_limits`: Rate limiting data per user/IP
- `security`: Blocked IPs and suspicious users
- `security_events`: Security event logs

### Logs
All security events and errors are logged to Firebase Functions logs and Firestore.

## Configuration

### Rate Limiting Configuration
Edit `backend/functions/middleware/rateLimiter.js`:
```javascript
this.limits = {
  user: {
    requests: 100,        // requests per window
    window: 15 * 60 * 1000, // window in milliseconds
    burst: 20            // burst limit per minute
  },
  ip: {
    requests: 50,
    window: 15 * 60 * 1000,
    burst: 10
  }
};
```

### Security Configuration
Edit `backend/functions/middleware/security.js` to customize:
- Blocked IP patterns
- Suspicious user detection
- Request validation rules

## Android Client Integration

### Updated ApiClient
The Android client has been updated to:
- Handle rate limiting responses
- Include proper headers
- Monitor rate limit headers
- Handle authentication errors gracefully

### Usage Example
```kotlin
// Create API client with authentication
val retrofit = ApiClient.createRetrofit(context)
val apiService = retrofit.create(CloudInterviewApiService::class.java)

// Refresh Firebase token before making requests
ApiClient.refreshFirebaseToken { token ->
    if (token != null) {
        // Make authenticated requests
        apiService.getTopics().enqueue(callback)
    }
}
```

## Troubleshooting

### Common Issues

1. **Rate Limit Exceeded**
   - Check rate limit headers in response
   - Implement exponential backoff
   - Consider caching responses

2. **Authentication Errors**
   - Ensure Firebase ID token is valid
   - Check token expiration
   - Refresh token if needed

3. **Security Blocking**
   - Check Firestore security collections
   - Review security event logs
   - Contact administrator if IP is blocked

### Debug Mode
Enable debug logging by setting environment variable:
```bash
export DEBUG=api-gateway:*
```

## Support

For issues or questions:
1. Check Firebase Functions logs
2. Review Firestore security collections
3. Check rate limiting data
4. Contact development team

---

**Version**: 2.0.0  
**Last Updated**: $(date)  
**Platform**: Firebase Functions with API Gateway
