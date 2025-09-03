const admin = require('../firebase-admin');

/**
 * Rate Limiter Middleware
 * Implements per-user rate limiting using Firebase Firestore
 */
class RateLimiter {
  constructor() {
    this.db = admin.firestore();
    this.collection = 'rate_limits';
    
    // Rate limit configurations
    this.limits = {
      // Per user limits
      user: {
        requests: 100,        // 100 requests
        window: 15 * 60 * 1000, // per 15 minutes
        burst: 20            // max 20 requests in 1 minute
      },
      // Per IP limits (for unauthenticated requests)
      ip: {
        requests: 50,         // 50 requests
        window: 15 * 60 * 1000, // per 15 minutes
        burst: 10            // max 10 requests in 1 minute
      }
    };
  }

  /**
   * Get rate limit key for user or IP
   */
  getKey(req) {
    if (req.user && req.user.uid) {
      return `user:${req.user.uid}`;
    }
    return `ip:${req.ip || req.connection.remoteAddress}`;
  }

  /**
   * Get rate limit configuration
   */
  getLimitConfig(req) {
    if (req.user && req.user.uid) {
      return this.limits.user;
    }
    return this.limits.ip;
  }

  /**
   * Check if request is within rate limits
   */
  async checkLimit(req) {
    try {
      const key = this.getKey(req);
      const config = this.getLimitConfig(req);
      const now = Date.now();
      const windowStart = now - config.window;
      const burstStart = now - 60000; // 1 minute for burst

      // Get or create rate limit document
      const docRef = this.db.collection(this.collection).doc(key);
      const doc = await docRef.get();
      
      let rateLimitData = {
        requests: [],
        lastRequest: now,
        totalRequests: 0
      };

      if (doc.exists) {
        rateLimitData = doc.data();
      }

      // Clean old requests outside the window
      rateLimitData.requests = rateLimitData.requests.filter(timestamp => timestamp > windowStart);
      
      // Check burst limit (requests in last minute)
      const recentRequests = rateLimitData.requests.filter(timestamp => timestamp > burstStart);
      if (recentRequests.length >= config.burst) {
        return {
          allowed: false,
          reason: 'burst_limit_exceeded',
          retryAfter: Math.ceil((recentRequests[0] + 60000 - now) / 1000),
          limit: config.burst,
          remaining: 0,
          resetTime: recentRequests[0] + 60000
        };
      }

      // Check window limit (requests in time window)
      if (rateLimitData.requests.length >= config.requests) {
        return {
          allowed: false,
          reason: 'rate_limit_exceeded',
          retryAfter: Math.ceil((rateLimitData.requests[0] + config.window - now) / 1000),
          limit: config.requests,
          remaining: 0,
          resetTime: rateLimitData.requests[0] + config.window
        };
      }

      // Add current request
      rateLimitData.requests.push(now);
      rateLimitData.lastRequest = now;
      rateLimitData.totalRequests = (rateLimitData.totalRequests || 0) + 1;

      // Update document
      await docRef.set(rateLimitData, { merge: true });

      return {
        allowed: true,
        limit: config.requests,
        remaining: config.requests - rateLimitData.requests.length,
        resetTime: rateLimitData.requests[0] + config.window,
        burstLimit: config.burst,
        burstRemaining: config.burst - recentRequests.length
      };

    } catch (error) {
      console.error('Rate limiter error:', error);
      // On error, allow the request but log it
      return {
        allowed: true,
        error: 'rate_limiter_error'
      };
    }
  }

  /**
   * Express middleware function
   */
  middleware() {
    return async (req, res, next) => {
      const result = await this.checkLimit(req);
      
      if (!result.allowed) {
        // Set rate limit headers
        res.set({
          'X-RateLimit-Limit': result.limit,
          'X-RateLimit-Remaining': result.remaining,
          'X-RateLimit-Reset': Math.ceil(result.resetTime / 1000),
          'Retry-After': result.retryAfter
        });

        return res.status(429).json({
          success: false,
          error: 'Rate limit exceeded',
          message: result.reason === 'burst_limit_exceeded' 
            ? 'Too many requests in a short time. Please slow down.'
            : 'Rate limit exceeded. Please try again later.',
          retryAfter: result.retryAfter,
          limit: result.limit,
          remaining: result.remaining
        });
      }

      // Set rate limit headers for successful requests
      res.set({
        'X-RateLimit-Limit': result.limit,
        'X-RateLimit-Remaining': result.remaining,
        'X-RateLimit-Reset': Math.ceil(result.resetTime / 1000)
      });

      if (result.burstLimit) {
        res.set({
          'X-RateLimit-Burst-Limit': result.burstLimit,
          'X-RateLimit-Burst-Remaining': result.burstRemaining
        });
      }

      next();
    };
  }
}

// Create singleton instance
const rateLimiter = new RateLimiter();

module.exports = {
  RateLimiter,
  rateLimiter: rateLimiter.middleware()
};
