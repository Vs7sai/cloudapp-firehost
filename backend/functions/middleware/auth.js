const admin = require('../firebase-admin');

/**
 * Firebase authentication middleware - verifies Firebase ID tokens
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

    try {
      // Verify the Firebase ID token
      const decodedToken = await admin.auth().verifyIdToken(idToken);
      
      // Add user info to request
      req.user = {
        uid: decodedToken.uid,
        email: decodedToken.email || 'unknown@example.com',
        emailVerified: decodedToken.email_verified || false,
        name: decodedToken.name || 'Authenticated User',
        picture: decodedToken.picture || null,
        authTime: decodedToken.auth_time || Math.floor(Date.now() / 1000),
        issuedAt: decodedToken.iat || Math.floor(Date.now() / 1000),
        expiresAt: decodedToken.exp || Math.floor(Date.now() / 1000) + 3600
      };

      console.log(`✅ User authenticated: ${req.user.email} (${req.user.uid})`);
      next();
    } catch (verifyError) {
      console.log('❌ Firebase token verification failed:', verifyError.message);
      return res.status(401).json({
        error: 'Authentication failed',
        message: 'Invalid or expired Firebase ID token'
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
