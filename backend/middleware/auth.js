const admin = require('../firebase-admin');

/**
 * Middleware to authenticate Firebase users
 * This middleware verifies the Firebase ID token in the Authorization header
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

    console.log('🔐 Verifying Firebase ID token...');

    // Verify the Firebase ID token
    const decodedToken = await admin.auth().verifyIdToken(idToken);
    
    // Add user information to the request
    req.user = {
      uid: decodedToken.uid,
      email: decodedToken.email,
      emailVerified: decodedToken.email_verified,
      name: decodedToken.name || 'Unknown',
      picture: decodedToken.picture || null,
      authTime: decodedToken.auth_time,
      issuedAt: decodedToken.iat,
      expiresAt: decodedToken.exp
    };

    console.log(`✅ User authenticated: ${req.user.email} (${req.user.uid})`);
    
    // Continue to the next middleware/route
    next();
    
  } catch (error) {
    console.error('❌ Token verification failed:', error.message);
    
    // Provide specific error messages based on error type
    let errorMessage = 'Token verification failed';
    let statusCode = 401;
    
    if (error.code === 'auth/id-token-expired') {
      errorMessage = 'Token has expired. Please sign in again.';
    } else if (error.code === 'auth/id-token-revoked') {
      errorMessage = 'Token has been revoked. Please sign in again.';
    } else if (error.code === 'auth/invalid-id-token') {
      errorMessage = 'Invalid token provided.';
    } else if (error.code === 'auth/argument-error') {
      errorMessage = 'Invalid token format.';
    }
    
    return res.status(statusCode).json({
      error: 'Authentication failed',
      message: errorMessage,
      details: error.message
    });
  }
};

/**
 * Optional authentication middleware
 * Allows the route to work with or without authentication
 * If authenticated, adds user info. If not, continues without it.
 */
const optionalAuth = async (req, res, next) => {
  try {
    const authHeader = req.headers.authorization;
    
    if (authHeader && authHeader.startsWith('Bearer ')) {
      const idToken = authHeader.split(' ')[1];
      const decodedToken = await admin.auth().verifyIdToken(idToken);
      
      req.user = {
        uid: decodedToken.uid,
        email: decodedToken.email,
        emailVerified: decodedToken.email_verified,
        name: decodedToken.name || 'Unknown'
      };
      
      console.log(`✅ Optional auth: User ${req.user.email} authenticated`);
    } else {
      console.log('ℹ️ Optional auth: No authentication provided');
    }
    
    next();
  } catch (error) {
    console.log('ℹ️ Optional auth: Token verification failed, continuing without auth');
    next();
  }
};

module.exports = {
  authenticate,
  optionalAuth
};
