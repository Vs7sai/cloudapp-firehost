const admin = require('../firebase-admin');

/**
 * Simple authentication middleware - accepts any Firebase ID token format
 */
const authenticate = async (req, res, next) => {
  try {
    // Get the Authorization header
    const authHeader = req.headers.authorization;
    
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      console.log('❌ No valid Authorization header found');
      return res.status(401).json({ 
        error: 'Authentication required',
        message: 'Please provide a valid Firebase ID token in Authorization header'
      });
    }

    // Extract the token (remove 'Bearer ' prefix)
    const idToken = authHeader.split(' ')[1];
    
    if (!idToken) {
      console.log('❌ No token found in Authorization header');
      return res.status(401).json({ 
        error: 'Invalid token format',
        message: 'Token must be provided after Bearer'
      });
    }

    console.log('🔐 Processing authentication token...');

    // For now, accept any token that looks like a Firebase ID token
    // (has dots and is reasonably long)
    if (idToken && idToken.length > 50 && idToken.includes('.')) {
      // Mock user for development
      req.user = {
        uid: 'user-' + Math.random().toString(36).substr(2, 9),
        email: 'user@example.com',
        emailVerified: true,
        name: 'Authenticated User',
        picture: null,
        authTime: Math.floor(Date.now() / 1000),
        issuedAt: Math.floor(Date.now() / 1000),
        expiresAt: Math.floor(Date.now() / 1000) + 3600
      };

      console.log(`✅ User authenticated: ${req.user.email} (${req.user.uid})`);
      next();
    } else {
      console.log('❌ Invalid token format');
      return res.status(401).json({
        error: 'Authentication failed',
        message: 'Invalid token format'
      });
    }
    
  } catch (error) {
    console.error('❌ Authentication error:', error.message);
    return res.status(401).json({
      error: 'Authentication failed',
      message: 'Token verification failed'
    });
  }
};

module.exports = {
  authenticate
};
