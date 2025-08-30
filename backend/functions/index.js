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

// Middleware
app.use(cors({ origin: true }));
app.use(express.json());

// Load data
const loadJSON = (filename) => {
  try {
    const data = fs.readFileSync(path.join(__dirname, 'data', filename), 'utf8');
    return JSON.parse(data);
  } catch (error) {
    console.error(`Error loading ${filename}:`, error);
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

// API Routes

// Get all topics - AUTHENTICATED ROUTE
app.get('/topics', authenticate, (req, res) => {
  try {
    logger.info(`📚 Topics requested by user: ${req.user.email} (${req.user.uid})`);

    res.json({
      success: true,
      data: topics,
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
      message: 'Error fetching topics',
      error: error.message
    });
  }
});

// Get specific topic - AUTHENTICATED ROUTE
app.get('/topics/:topicId', authenticate, (req, res) => {
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
});

// Get questions for a topic and difficulty - AUTHENTICATED ROUTE
app.get('/questions/:topicId/:difficulty', authenticate, (req, res) => {
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
});

// Get all questions for a topic (all difficulties) - AUTHENTICATED ROUTE
app.get('/questions/:topicId', authenticate, (req, res) => {
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
});

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({
    success: true,
    message: 'Cloud Interview Prep Backend API is running on Firebase Functions',
    timestamp: new Date().toISOString()
  });
});

// Test authentication endpoint - AUTHENTICATED (for testing)
app.get('/test-auth', authenticate, (req, res) => {
  res.json({
    success: true,
    message: 'Authentication successful!',
    user: {
      uid: req.user.uid,
      email: req.user.email,
      name: req.user.name,
      emailVerified: req.user.emailVerified,
      authTime: req.user.authTime,
      issuedAt: req.user.issuedAt,
      expiresAt: req.user.expiresAt
    },
    timestamp: new Date().toISOString()
  });
});

// Root endpoint
app.get('/', (req, res) => {
  res.json({
    message: 'Cloud Interview Prep Backend API',
    version: '1.0.0',
    platform: 'Firebase Functions',
    authentication: 'Firebase Authentication Required',
    endpoints: {
      topics: '/topics (requires auth)',
      questions: '/questions/:topicId/:difficulty (requires auth)',
      'test-auth': '/test-auth (requires auth)',
      health: '/health (public)'
    }
  });
});

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
