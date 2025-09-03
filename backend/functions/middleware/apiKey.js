const admin = require('../firebase-admin');

/**
 * API Key Authentication Middleware
 * Provides an additional layer of security with API keys
 */
class APIKeyAuth {
  constructor() {
    this.db = admin.firestore();
    this.apiKeys = new Map();
    this.loadAPIKeys();
  }

  /**
   * Load API keys from Firestore
   */
  async loadAPIKeys() {
    try {
      const snapshot = await this.db.collection('api_keys').get();
      this.apiKeys.clear();
      
      snapshot.forEach(doc => {
        const data = doc.data();
        this.apiKeys.set(data.key, {
          id: doc.id,
          name: data.name,
          permissions: data.permissions || ['read'],
          rateLimit: data.rateLimit || 1000, // requests per hour
          expiresAt: data.expiresAt,
          isActive: data.isActive !== false,
          createdAt: data.createdAt,
          lastUsed: data.lastUsed
        });
      });
      
      console.log(`🔑 Loaded ${this.apiKeys.size} API keys`);
    } catch (error) {
      console.error('Error loading API keys:', error);
    }
  }

  /**
   * Validate API key
   */
  async validateAPIKey(apiKey) {
    if (!apiKey) return { valid: false, reason: 'missing_api_key' };
    
    const keyData = this.apiKeys.get(apiKey);
    if (!keyData) return { valid: false, reason: 'invalid_api_key' };
    
    if (!keyData.isActive) return { valid: false, reason: 'inactive_api_key' };
    
    if (keyData.expiresAt && new Date() > keyData.expiresAt.toDate()) {
      return { valid: false, reason: 'expired_api_key' };
    }
    
    return { valid: true, keyData };
  }

  /**
   * Check API key permissions
   */
  hasPermission(keyData, requiredPermission) {
    return keyData.permissions.includes(requiredPermission) || 
           keyData.permissions.includes('admin');
  }

  /**
   * Update API key usage
   */
  async updateUsage(apiKey) {
    try {
      await this.db.collection('api_keys').doc(apiKey).update({
        lastUsed: admin.firestore.FieldValue.serverTimestamp(),
        usageCount: admin.firestore.FieldValue.increment(1)
      });
    } catch (error) {
      console.error('Error updating API key usage:', error);
    }
  }

  /**
   * Express middleware
   */
  middleware(requiredPermission = 'read') {
    return async (req, res, next) => {
      try {
        // Get API key from header
        const apiKey = req.headers['x-api-key'] || req.headers['x-apikey'];
        
        const validation = await this.validateAPIKey(apiKey);
        if (!validation.valid) {
          return res.status(401).json({
            success: false,
            error: 'API Key Authentication Failed',
            message: this.getErrorMessage(validation.reason)
          });
        }

        // Check permissions
        if (!this.hasPermission(validation.keyData, requiredPermission)) {
          return res.status(403).json({
            success: false,
            error: 'Insufficient Permissions',
            message: `API key does not have ${requiredPermission} permission`
          });
        }

        // Add API key info to request
        req.apiKey = {
          id: validation.keyData.id,
          name: validation.keyData.name,
          permissions: validation.keyData.permissions
        };

        // Update usage
        await this.updateUsage(apiKey);

        next();
      } catch (error) {
        console.error('API Key middleware error:', error);
        return res.status(500).json({
          success: false,
          error: 'API Key validation failed',
          message: 'Internal server error'
        });
      }
    };
  }

  /**
   * Get error message for validation failure
   */
  getErrorMessage(reason) {
    const messages = {
      'missing_api_key': 'API key is required. Include X-API-Key header.',
      'invalid_api_key': 'Invalid API key provided.',
      'inactive_api_key': 'API key is inactive.',
      'expired_api_key': 'API key has expired.'
    };
    return messages[reason] || 'API key validation failed';
  }

  /**
   * Create new API key
   */
  async createAPIKey(name, permissions = ['read'], expiresInDays = 365) {
    try {
      const apiKey = this.generateAPIKey();
      const expiresAt = new Date();
      expiresAt.setDate(expiresAt.getDate() + expiresInDays);

      await this.db.collection('api_keys').doc(apiKey).set({
        name,
        permissions,
        expiresAt,
        isActive: true,
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        usageCount: 0
      });

      // Reload API keys
      await this.loadAPIKeys();

      return {
        success: true,
        apiKey,
        expiresAt: expiresAt.toISOString()
      };
    } catch (error) {
      console.error('Error creating API key:', error);
      return {
        success: false,
        error: error.message
      };
    }
  }

  /**
   * Generate secure API key
   */
  generateAPIKey() {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = 'cip_'; // Cloud Interview Prep prefix
    
    for (let i = 0; i < 32; i++) {
      result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    
    return result;
  }
}

// Create singleton instance
const apiKeyAuth = new APIKeyAuth();

module.exports = {
  APIKeyAuth,
  apiKeyAuth
};
