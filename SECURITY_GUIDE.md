# 🔒 Security Guide for Cloud Interview Prep API

This guide covers the comprehensive security measures implemented to protect your Google Cloud Function API.

## 🛡️ Security Features Overview

### **Authentication & Authorization**
- ✅ **Firebase Authentication** - ID token verification for all protected routes
- ✅ **API Key Authentication** - Additional layer of security with API keys
- ✅ **Role-based Access Control** - Different permission levels (read, write, admin)
- ✅ **Token Expiration** - Automatic token validation and refresh

### **Rate Limiting & DDoS Protection**
- ✅ **Per-User Rate Limiting** - 100 requests per 15 minutes, 20 burst per minute
- ✅ **Per-IP Rate Limiting** - 50 requests per 15 minutes, 10 burst per minute
- ✅ **Dynamic Rate Adjustment** - Based on user behavior and system load
- ✅ **Burst Protection** - Prevents rapid-fire requests

### **Request Validation & Filtering**
- ✅ **Request Size Limits** - Maximum 1MB per request
- ✅ **Header Validation** - Required headers and suspicious pattern detection
- ✅ **User-Agent Filtering** - Blocks known bots and scrapers
- ✅ **Content-Type Validation** - Ensures proper request formats

### **Network Security**
- ✅ **CORS Configuration** - Restricted to allowed origins only
- ✅ **IP Blocking** - Dynamic IP blacklisting for malicious actors
- ✅ **Security Headers** - XSS protection, CSRF prevention, etc.
- ✅ **HTTPS Enforcement** - All communications encrypted

### **Monitoring & Logging**
- ✅ **Security Event Logging** - All security events logged to Firestore
- ✅ **Real-time Monitoring** - Continuous security monitoring
- ✅ **Alert System** - Automated alerts for suspicious activity
- ✅ **Audit Trail** - Complete request/response logging

## 🔧 Configuration

### **1. CORS Origins**
Update the allowed origins in `backend/functions/index.js`:

```javascript
const allowedOrigins = [
  'https://yourdomain.com',
  'https://www.yourdomain.com',
  'http://localhost:3000', // Development only
  'http://localhost:8080'  // Development only
];
```

### **2. Rate Limits**
Adjust rate limits in `backend/functions/middleware/rateLimiter.js`:

```javascript
this.limits = {
  user: {
    requests: 100,        // requests per window
    window: 15 * 60 * 1000, // 15 minutes
    burst: 20            // max requests in 1 minute
  },
  ip: {
    requests: 50,         // requests per window
    window: 15 * 60 * 1000, // 15 minutes
    burst: 10            // max requests in 1 minute
  }
};
```

### **3. Security Rules**
Configure security rules in Firestore:

```javascript
// Collection: security_rules/document: default
{
  rules: {
    maxRequestSize: 1024 * 1024, // 1MB
    maxConcurrentRequests: 10,
    allowedUserAgents: ['Mozilla/5.0', 'Chrome/', 'Safari/'],
    blockedUserAgents: ['bot', 'crawler', 'spider'],
    suspiciousPatterns: ['sql', 'script', 'union', 'select']
  }
}
```

## 🚀 Deployment

### **1. Initial Security Setup**
```bash
cd backend/functions
node scripts/setup-security.js
```

### **2. Deploy with Security**
```bash
./deploy-secure.sh
```

### **3. Monitor Security**
```bash
node scripts/security-monitor.js
```

## 🔑 API Key Management

### **Creating API Keys**
```bash
# Via API (requires admin authentication)
curl -X POST https://your-function-url/api/admin/create-api-key \
  -H "Authorization: Bearer YOUR_FIREBASE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mobile App Key",
    "permissions": ["read"],
    "expiresInDays": 365
  }'
```

### **Using API Keys**
```bash
# Include API key in requests
curl -X GET https://your-function-url/api/topics \
  -H "Authorization: Bearer YOUR_FIREBASE_TOKEN" \
  -H "X-API-Key: YOUR_API_KEY"
```

## 📊 Monitoring & Alerts

### **Security Events**
All security events are logged to the `security_events` collection in Firestore:

- `authentication_failed` - Failed login attempts
- `rate_limit_exceeded` - Rate limit violations
- `blocked_ip_access` - Access attempts from blocked IPs
- `suspicious_user_access` - Access by flagged users
- `invalid_headers` - Requests with invalid headers
- `request_too_large` - Oversized requests

### **Security Alerts**
Alerts are generated for:
- High failed authentication attempts (>5 per minute)
- Rate limit violations (>10 per minute)
- Suspicious activity patterns (>3 per minute)
- Blocked IP access attempts

### **Monitoring Dashboard**
Access security stats via:
```bash
# Get current security statistics
curl https://your-function-url/api/gateway/status
```

## 🚨 Incident Response

### **Blocking Malicious IPs**
```javascript
// Via Firestore console or script
await db.collection('security').doc('blocked_ips').update({
  ips: admin.firestore.FieldValue.arrayUnion('MALICIOUS_IP')
});
```

### **Flagging Suspicious Users**
```javascript
// Via Firestore console or script
await db.collection('security').doc('suspicious_users').update({
  users: admin.firestore.FieldValue.arrayUnion('USER_UID')
});
```

### **Emergency API Key Revocation**
```javascript
// Deactivate API key
await db.collection('api_keys').doc('API_KEY_ID').update({
  isActive: false,
  revokedAt: admin.firestore.FieldValue.serverTimestamp(),
  revokedReason: 'Security incident'
});
```

## 🔍 Security Testing

### **1. Authentication Testing**
```bash
# Test without token (should fail)
curl https://your-function-url/api/topics

# Test with invalid token (should fail)
curl https://your-function-url/api/topics \
  -H "Authorization: Bearer invalid_token"

# Test with valid token (should succeed)
curl https://your-function-url/api/topics \
  -H "Authorization: Bearer VALID_FIREBASE_TOKEN"
```

### **2. Rate Limiting Testing**
```bash
# Rapid requests to test rate limiting
for i in {1..25}; do
  curl https://your-function-url/api/health
done
```

### **3. CORS Testing**
```bash
# Test from unauthorized origin
curl -H "Origin: https://malicious-site.com" \
  https://your-function-url/api/health
```

## 📋 Security Checklist

### **Pre-Deployment**
- [ ] Update CORS origins for production
- [ ] Configure rate limits based on expected traffic
- [ ] Set up monitoring and alerting
- [ ] Test all security features
- [ ] Review and update security rules

### **Post-Deployment**
- [ ] Monitor security events daily
- [ ] Review rate limit effectiveness
- [ ] Update blocked IPs as needed
- [ ] Rotate API keys regularly
- [ ] Review and update security rules monthly

### **Ongoing Maintenance**
- [ ] Monitor security logs weekly
- [ ] Update dependencies monthly
- [ ] Review and test security features quarterly
- [ ] Conduct security audits annually

## 🆘 Emergency Contacts

### **Security Incidents**
1. **Immediate Response**: Block malicious IPs/users
2. **Investigation**: Review security logs
3. **Documentation**: Log incident details
4. **Recovery**: Restore normal operations
5. **Prevention**: Update security rules

### **Support Resources**
- Firebase Console: https://console.firebase.google.com
- Google Cloud Console: https://console.cloud.google.com
- Security Documentation: This guide
- Monitoring Scripts: `backend/functions/scripts/`

## 📚 Additional Resources

- [Firebase Security Rules](https://firebase.google.com/docs/rules)
- [Google Cloud Security](https://cloud.google.com/security)
- [OWASP API Security](https://owasp.org/www-project-api-security/)
- [Rate Limiting Best Practices](https://cloud.google.com/architecture/rate-limiting-strategies-techniques)

---

**Remember**: Security is an ongoing process. Regularly review, test, and update your security measures to stay protected against evolving threats.
