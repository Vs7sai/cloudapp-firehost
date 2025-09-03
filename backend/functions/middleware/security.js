const admin = require('../firebase-admin');

/**
 * Security Middleware
 * Implements various security measures for API endpoints
 */
class SecurityMiddleware {
  constructor() {
    this.db = admin.firestore();
    this.blockedIPs = new Set();
    this.suspiciousUsers = new Set();
    
    // Load blocked IPs and suspicious users from Firestore
    this.loadSecurityData();
  }

  /**
   * Load security data from Firestore
   */
  async loadSecurityData() {
    try {
      // Load blocked IPs
      const blockedIPsSnapshot = await this.db.collection('security').doc('blocked_ips').get();
      if (blockedIPsSnapshot.exists) {
        const data = blockedIPsSnapshot.data();
        this.blockedIPs = new Set(data.ips || []);
      }

      // Load suspicious users
      const suspiciousUsersSnapshot = await this.db.collection('security').doc('suspicious_users').get();
      if (suspiciousUsersSnapshot.exists) {
        const data = suspiciousUsersSnapshot.data();
        this.suspiciousUsers = new Set(data.users || []);
      }

      console.log(`🔒 Security data loaded: ${this.blockedIPs.size} blocked IPs, ${this.suspiciousUsers.size} suspicious users`);
    } catch (error) {
      console.error('Error loading security data:', error);
    }
  }

  /**
   * Check if IP is blocked
   */
  isIPBlocked(req) {
    const ip = req.ip || req.connection.remoteAddress;
    return this.blockedIPs.has(ip);
  }

  /**
   * Check if user is suspicious
   */
  isUserSuspicious(req) {
    if (!req.user || !req.user.uid) return false;
    return this.suspiciousUsers.has(req.user.uid);
  }

  /**
   * Validate request headers
   */
  validateHeaders(req) {
    const userAgent = req.get('User-Agent');
    const origin = req.get('Origin');
    const referer = req.get('Referer');

    // Check for suspicious user agents
    const suspiciousPatterns = [
      /bot/i,
      /crawler/i,
      /spider/i,
      /scraper/i,
      /curl/i,
      /wget/i
    ];

    if (userAgent && suspiciousPatterns.some(pattern => pattern.test(userAgent))) {
      return {
        valid: false,
        reason: 'suspicious_user_agent',
        details: 'Request appears to be from a bot or automated tool'
      };
    }

    // Check for missing or suspicious headers
    if (!userAgent) {
      return {
        valid: false,
        reason: 'missing_user_agent',
        details: 'User-Agent header is required'
      };
    }

    return { valid: true };
  }

  /**
   * Validate request size
   */
  validateRequestSize(req) {
    const contentLength = parseInt(req.get('Content-Length') || '0');
    const maxSize = 1024 * 1024; // 1MB

    if (contentLength > maxSize) {
      return {
        valid: false,
        reason: 'request_too_large',
        details: `Request size ${contentLength} exceeds maximum allowed size ${maxSize}`
      };
    }

    return { valid: true };
  }

  /**
   * Log security event
   */
  async logSecurityEvent(req, event, details) {
    try {
      const securityEvent = {
        timestamp: admin.firestore.FieldValue.serverTimestamp(),
        ip: req.ip || req.connection.remoteAddress,
        userAgent: req.get('User-Agent'),
        user: req.user ? {
          uid: req.user.uid,
          email: req.user.email
        } : null,
        event: event,
        details: details,
        url: req.originalUrl,
        method: req.method,
        headers: {
          origin: req.get('Origin'),
          referer: req.get('Referer'),
          contentType: req.get('Content-Type')
        }
      };

      await this.db.collection('security_events').add(securityEvent);
      console.log(`🚨 Security event logged: ${event} - ${details}`);
    } catch (error) {
      console.error('Error logging security event:', error);
    }
  }

  /**
   * Block IP address
   */
  async blockIP(ip, reason) {
    try {
      this.blockedIPs.add(ip);
      await this.db.collection('security').doc('blocked_ips').set({
        ips: Array.from(this.blockedIPs),
        lastUpdated: admin.firestore.FieldValue.serverTimestamp()
      });
      console.log(`🚫 IP blocked: ${ip} - ${reason}`);
    } catch (error) {
      console.error('Error blocking IP:', error);
    }
  }

  /**
   * Mark user as suspicious
   */
  async markUserSuspicious(uid, reason) {
    try {
      this.suspiciousUsers.add(uid);
      await this.db.collection('security').doc('suspicious_users').set({
        users: Array.from(this.suspiciousUsers),
        lastUpdated: admin.firestore.FieldValue.serverTimestamp()
      });
      console.log(`⚠️ User marked suspicious: ${uid} - ${reason}`);
    } catch (error) {
      console.error('Error marking user suspicious:', error);
    }
  }

  /**
   * Main security middleware
   */
  middleware() {
    return async (req, res, next) => {
      try {
        // Check if IP is blocked
        if (this.isIPBlocked(req)) {
          await this.logSecurityEvent(req, 'blocked_ip_access', 'Attempted access from blocked IP');
          return res.status(403).json({
            success: false,
            error: 'Access denied',
            message: 'Your IP address has been blocked'
          });
        }

        // Check if user is suspicious
        if (this.isUserSuspicious(req)) {
          await this.logSecurityEvent(req, 'suspicious_user_access', 'Attempted access by suspicious user');
          return res.status(403).json({
            success: false,
            error: 'Access denied',
            message: 'Your account has been flagged for suspicious activity'
          });
        }

        // Validate headers
        const headerValidation = this.validateHeaders(req);
        if (!headerValidation.valid) {
          await this.logSecurityEvent(req, 'invalid_headers', headerValidation.details);
          return res.status(400).json({
            success: false,
            error: 'Invalid request',
            message: headerValidation.details
          });
        }

        // Validate request size
        const sizeValidation = this.validateRequestSize(req);
        if (!sizeValidation.valid) {
          await this.logSecurityEvent(req, 'request_too_large', sizeValidation.details);
          return res.status(413).json({
            success: false,
            error: 'Request too large',
            message: 'Request size exceeds maximum allowed limit'
          });
        }

        // Add security headers
        res.set({
          'X-Content-Type-Options': 'nosniff',
          'X-Frame-Options': 'DENY',
          'X-XSS-Protection': '1; mode=block',
          'Strict-Transport-Security': 'max-age=31536000; includeSubDomains',
          'Referrer-Policy': 'strict-origin-when-cross-origin',
          'Content-Security-Policy': "default-src 'self'"
        });

        next();
      } catch (error) {
        console.error('Security middleware error:', error);
        await this.logSecurityEvent(req, 'security_middleware_error', error.message);
        next(); // Continue on error to avoid breaking the app
      }
    };
  }
}

// Create singleton instance
const securityMiddleware = new SecurityMiddleware();

module.exports = {
  SecurityMiddleware,
  securityMiddleware: securityMiddleware.middleware(),
  securityInstance: securityMiddleware
};
