const { rateLimiter } = require('./rateLimiter');
const { securityMiddleware, securityInstance } = require('./security');
const { authenticate } = require('./auth');

/**
 * API Gateway Middleware
 * Combines all middleware components for a comprehensive API gateway
 */
class APIGateway {
  constructor() {
    this.middlewareStack = [];
    this.setupMiddleware();
  }

  /**
   * Setup middleware stack
   */
  setupMiddleware() {
    // 1. Security middleware (first - blocks malicious requests early)
    this.middlewareStack.push(securityMiddleware);

    // 2. Rate limiting middleware
    this.middlewareStack.push(rateLimiter);

    // 3. Authentication middleware (for protected routes)
    this.middlewareStack.push(authenticate);
  }

  /**
   * Apply middleware to Express app
   */
  applyMiddleware(app) {
    this.middlewareStack.forEach(middleware => {
      app.use(middleware);
    });
  }

  /**
   * Create protected route handler
   */
  createProtectedRoute(routeHandler) {
    return async (req, res, next) => {
      try {
        // All middleware has already been applied
        // Just execute the route handler
        await routeHandler(req, res, next);
      } catch (error) {
        console.error('Protected route error:', error);
        
        // Log security event for errors
        await securityInstance.logSecurityEvent(req, 'route_error', error.message);
        
        res.status(500).json({
          success: false,
          error: 'Internal server error',
          message: 'An unexpected error occurred'
        });
      }
    };
  }

  /**
   * Create public route handler (no authentication required)
   */
  createPublicRoute(routeHandler) {
    return async (req, res, next) => {
      try {
        // Apply security and rate limiting but skip authentication
        await securityMiddleware(req, res, async () => {
          await rateLimiter(req, res, async () => {
            await routeHandler(req, res, next);
          });
        });
      } catch (error) {
        console.error('Public route error:', error);
        
        // Log security event for errors
        await securityInstance.logSecurityEvent(req, 'public_route_error', error.message);
        
        res.status(500).json({
          success: false,
          error: 'Internal server error',
          message: 'An unexpected error occurred'
        });
      }
    };
  }

  /**
   * Get API Gateway status
   */
  async getStatus() {
    return {
      status: 'active',
      middleware: {
        security: 'enabled',
        rateLimiting: 'enabled',
        authentication: 'enabled'
      },
      security: {
        blockedIPs: securityInstance.blockedIPs.size,
        suspiciousUsers: securityInstance.suspiciousUsers.size
      },
      timestamp: new Date().toISOString()
    };
  }

  /**
   * Health check endpoint
   */
  healthCheck() {
    return async (req, res) => {
      try {
        const status = await this.getStatus();
        res.json({
          success: true,
          message: 'API Gateway is healthy',
          ...status
        });
      } catch (error) {
        res.status(500).json({
          success: false,
          error: 'Health check failed',
          message: error.message
        });
      }
    };
  }

  /**
   * API Gateway info endpoint
   */
  gatewayInfo() {
    return async (req, res) => {
      try {
        const status = await this.getStatus();
        res.json({
          success: true,
          message: 'Cloud Interview Prep API Gateway',
          version: '1.0.0',
          features: [
            'Firebase Authentication',
            'Rate Limiting',
            'Security Headers',
            'Request Validation',
            'IP Blocking',
            'Suspicious User Detection'
          ],
          endpoints: {
            topics: '/api/topics (protected)',
            questions: '/api/questions/:topicId/:difficulty (protected)',
            health: '/api/health (public)',
            'gateway-status': '/api/gateway/status (public)'
          },
          rateLimits: {
            authenticated: '100 requests per 15 minutes, 20 burst per minute',
            unauthenticated: '50 requests per 15 minutes, 10 burst per minute'
          },
          ...status
        });
      } catch (error) {
        res.status(500).json({
          success: false,
          error: 'Gateway info failed',
          message: error.message
        });
      }
    };
  }
}

// Create singleton instance
const apiGateway = new APIGateway();

module.exports = {
  APIGateway,
  apiGateway
};
