/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const {setGlobalOptions} = require("firebase-functions");
const {onRequest} = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");
const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');
const { authenticate } = require('./middleware/auth');
const { apiGateway } = require('./middleware/apiGateway');

// For cost control, you can set the maximum number of containers that can be
// running at the same time. This helps mitigate the impact of unexpected
// traffic spikes by instead downgrading performance. This limit is a
// per-function limit. You can override the limit for each function using the
// `maxInstances` option in the function's options, e.g.
// `onRequest({ maxInstances: 5 }, (req, res) => { ... })`.
// NOTE: setGlobalOptions does not apply to functions using the v1 API. V1
// functions should each use functions.runWith({ maxInstances: 10 }) instead.
// In the v1 API, each function can only serve one request per container, so
// this will be the maximum concurrent request count.
setGlobalOptions({ maxInstances: 10 });

// Create Express app
const app = express();

// Basic middleware
app.use(cors({ origin: true }));
app.use(express.json({ limit: '1mb' })); // Limit request size

// Apply API Gateway middleware
apiGateway.applyMiddleware(app);

// Load data
const loadJSON = (filename) => {
  try {
    const filepath = path.join(__dirname, 'data', filename);
    console.log(`🔍 Attempting to load file: ${filepath}`);
    console.log(`🔍 __dirname: ${__dirname}`);
    console.log(`🔍 Current working directory: ${process.cwd()}`);
    
    if (!fs.existsSync(filepath)) {
      console.error(`❌ File does not exist: ${filepath}`);
      return null;
    }
    
    const data = fs.readFileSync(filepath, 'utf8');
    console.log(`✅ File loaded successfully: ${filename}`);
    console.log(`📊 Data length: ${data.length} characters`);
    
    const parsed = JSON.parse(data);
    console.log(`✅ JSON parsed successfully, topics count: ${Array.isArray(parsed) ? parsed.length : 'not an array'}`);
    return parsed;
  } catch (error) {
    console.error(`❌ Error loading ${filename}:`, error);
    return null;
  }
};

const loadQuestions = (topic, difficulty) => {
  try {
    const filename = `${difficulty}.js`;
    const filepath = path.join(__dirname, 'data', 'questions', topic, filename);
    delete require.cache[require.resolve(filepath)]; // Clear cache for hot reload
    return require(filepath);
  } catch (error) {
    console.error(`Error loading questions for ${topic}/${difficulty}:`, error);
    return [];
  }
};

const topics = loadJSON('topics.json');
console.log(`🚀 Topics loaded at startup: ${topics ? 'SUCCESS' : 'FAILED'}`);
if (topics) {
  console.log(`📚 Topics count: ${topics.length}`);
  console.log(`📚 First topic: ${JSON.stringify(topics[0])}`);
}

// API Routes

// Get all topics - PROTECTED ROUTE (via API Gateway)
app.get('/api/topics', apiGateway.createProtectedRoute((req, res) => {
  try {
    logger.info(`📚 Topics requested by user: ${req.user.email} (${req.user.uid})`);
    
    console.log(`🔍 Topics variable: ${topics ? 'EXISTS' : 'NULL'}`);
    if (topics) {
      console.log(`📊 Topics type: ${typeof topics}`);
      console.log(`📊 Topics length: ${Array.isArray(topics) ? topics.length : 'not an array'}`);
      console.log(`📊 Topics preview: ${JSON.stringify(topics).substring(0, 200)}...`);
    }

    const response = {
      success: true,
      data: topics,
      user: {
        uid: req.user.uid,
        email: req.user.email,
        name: req.user.name,
        emailVerified: req.user.emailVerified
      }
    };
    
    console.log(`📤 Sending response: ${JSON.stringify(response).substring(0, 300)}...`);
    
    res.json(response);
  } catch (error) {
    console.error(`❌ Error in topics endpoint:`, error);
    res.status(500).json({
      success: false,
      message: 'Error fetching topics',
      error: error.message
    });
  }
}));

// Get specific topic - PROTECTED ROUTE (via API Gateway)
app.get('/api/topics/:topicId', apiGateway.createProtectedRoute((req, res) => {
  try {
    const { topicId } = req.params;
    const topic = topics.find(t => t.id === topicId.toLowerCase());

    if (!topic) {
      return res.status(404).json({
        success: false,
        message: 'Topic not found'
      });
    }

    logger.info(`📖 Topic ${topicId} requested by user: ${req.user.email} (${req.user.uid})`);

    res.json({
      success: true,
      data: topic,
      user: {
        uid: req.user.uid,
        email: req.user.email,
        name: req.user.name,
        emailVerified: req.user.emailVerified
      }
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: 'Error fetching topic',
      error: error.message
    });
  }
}));

// Get questions for a topic and difficulty - PROTECTED ROUTE (via API Gateway)
app.get('/api/questions/:topicId/:difficulty', apiGateway.createProtectedRoute((req, res) => {
  try {
    const { topicId, difficulty } = req.params;
    const topicKey = topicId.toLowerCase();
    const difficultyKey = difficulty.toLowerCase();

    // Load questions from individual file
    const questions = loadQuestions(topicKey, difficultyKey);

    if (!questions || questions.length === 0) {
      return res.status(404).json({
        success: false,
        message: `Questions not found for ${topicKey} ${difficultyKey}`
      });
    }

    logger.info(`❓ Questions for ${topicKey} ${difficultyKey} requested by user: ${req.user.email} (${req.user.uid})`);

    res.json({
      success: true,
      data: {
        topic: topicKey,
        difficulty: difficultyKey,
        questions: questions
      },
      user: {
        uid: req.user.uid,
        email: req.user.email,
        name: req.user.name,
        emailVerified: req.user.emailVerified
      }
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: 'Error fetching questions',
      error: error.message
    });
  }
}));

// Get all questions for a topic (all difficulties) - PROTECTED ROUTE (via API Gateway)
app.get('/api/questions/:topicId', apiGateway.createProtectedRoute((req, res) => {
  try {
    const { topicId } = req.params;
    const topicKey = topicId.toLowerCase();

    // Load questions from individual files for all difficulties
    const difficulties = ['beginner', 'medium', 'pro'];
    const allQuestions = {};

    difficulties.forEach(difficulty => {
      const questions = loadQuestions(topicKey, difficulty);
      if (questions && questions.length > 0) {
        allQuestions[difficulty] = questions;
      }
    });

    if (Object.keys(allQuestions).length === 0) {
      return res.status(404).json({
        success: false,
        message: 'Topic not found or no questions available'
      });
    }

    logger.info(`❓ All questions for ${topicKey} requested by user: ${req.user.email} (${req.user.uid})`);

    res.json({
      success: true,
      data: {
        topic: topicKey,
        questions: allQuestions
      },
      user: {
        uid: req.user.uid,
        email: req.user.email,
        name: req.user.name,
        emailVerified: req.user.emailVerified
      }
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: 'Error fetching questions',
      error: error.message
    });
  }
}));

// Health check endpoint - PUBLIC ROUTE
app.get('/api/health', apiGateway.createPublicRoute((req, res) => {
  res.json({
    success: true,
    message: 'Backend server is running',
    timestamp: new Date().toISOString(),
    uptime: process.uptime()
  });
}));

// API Gateway status endpoint - PUBLIC ROUTE
app.get('/api/gateway/status', apiGateway.healthCheck());

// API Gateway info endpoint - PUBLIC ROUTE
app.get('/api/gateway/info', apiGateway.gatewayInfo());

// Test authentication endpoint - PROTECTED ROUTE (via API Gateway)
app.get('/api/test-auth', apiGateway.createProtectedRoute((req, res) => {
  res.json({
    success: true,
    message: 'Authentication successful',
    user: {
      uid: req.user.uid,
      email: req.user.email,
      name: req.user.name,
      emailVerified: req.user.emailVerified
    }
  });
}));

// Root endpoint - PUBLIC ROUTE
app.get('/', apiGateway.createPublicRoute((req, res) => {
  res.json({
    message: 'Cloud Interview Prep Backend API',
    version: '2.0.0',
    platform: 'Firebase Functions with API Gateway',
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
      'test-auth': '/api/test-auth (protected)',
      health: '/api/health (public)',
      'gateway-status': '/api/gateway/status (public)',
      'gateway-info': '/api/gateway/info (public)'
    },
    rateLimits: {
      authenticated: '100 requests per 15 minutes, 20 burst per minute',
      unauthenticated: '50 requests per 15 minutes, 10 burst per minute'
    }
  });
}));

// Error handling middleware
app.use((err, req, res, next) => {
  logger.error(err.stack);
  res.status(500).json({
    success: false,
    message: 'Something went wrong!',
    error: process.env.NODE_ENV === 'development' ? err.message : 'Internal server error'
  });
});

// 404 handler
app.use((req, res) => {
  res.status(404).json({
    success: false,
    message: 'Endpoint not found'
  });
});

// Export the Express app as a Firebase Function
exports.api = onRequest(app);
