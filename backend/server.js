const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
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

// Get all topics - PUBLIC ROUTE (for testing)
app.get('/api/topics', (req, res) => {
  try {
    console.log(`📚 Topics requested by user (public access)`);
    
    res.json({
      success: true,
      data: topics,
      user: {
        email: 'public@test.com',
        name: 'Public User'
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

// Get specific topic - PUBLIC ROUTE (for testing)
app.get('/api/topics/:topicId', (req, res) => {
  try {
    const { topicId } = req.params;
    const topic = topics.find(t => t.id === topicId.toLowerCase());
    
    if (!topic) {
      return res.status(404).json({
        success: false,
        message: 'Topic not found'
      });
    }
    
    console.log(`📖 Topic ${topicId} requested by user (public access)`);
    
    res.json({
      success: true,
      data: topic,
      user: {
        email: 'public@test.com',
        name: 'Public User'
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

// Get questions for a topic and difficulty - PUBLIC ROUTE (for testing)
app.get('/api/questions/:topicId/:difficulty', (req, res) => {
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
    
    console.log(`❓ Questions for ${topicKey} ${difficultyKey} requested by user (public access)`);
    
    res.json({
      success: true,
      data: {
        topic: topicKey,
        difficulty: difficultyKey,
        questions: questions
      },
      user: {
        email: 'public@test.com',
        name: 'Public User'
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

// Get all questions for a topic (all difficulties) - PUBLIC ROUTE (for testing)
app.get('/api/questions/:topicId', (req, res) => {
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
    
    console.log(`❓ All questions for ${topicKey} requested by user (public access)`);
    
    res.json({
      success: true,
      data: {
        topic: topicKey,
        questions: allQuestions
      },
      user: {
        email: 'public@test.com',
        name: 'Public User'
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
app.get('/api/health', (req, res) => {
  res.json({
    success: true,
    message: 'Cloud Interview Prep Backend API is running',
    timestamp: new Date().toISOString()
  });
});

// Test authentication endpoint - PUBLIC (for testing)
app.get('/api/test-auth', (req, res) => {
  res.json({
    success: true,
    message: 'Public access successful! (No authentication required)',
    user: {
      email: 'public@test.com',
      name: 'Public User',
      uid: 'public-uid',
      emailVerified: true
    },
    timestamp: new Date().toISOString()
  });
});

// Root endpoint
app.get('/', (req, res) => {
  res.json({
    message: 'Cloud Interview Prep Backend API',
    version: '1.0.0',
    endpoints: {
      topics: '/api/topics',
      questions: '/api/questions/:topicId/:difficulty',
      health: '/api/health'
    }
  });
});

// Error handling middleware
app.use((err, req, res, next) => {
  console.error(err.stack);
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

// Start server
app.listen(PORT, '0.0.0.0', () => {
  console.log(`🚀 Cloud Interview Prep Backend API running on port ${PORT}`);
  console.log(`📍 Health check: http://localhost:${PORT}/api/health`);
  console.log(`📍 Emulator access: http://10.0.2.2:${PORT}/api/health`);
  console.log(`📚 Topics: http://localhost:${PORT}/api/topics`);
  console.log(`❓ Questions: http://localhost:${PORT}/api/questions/{topic}/{difficulty}`);
});

module.exports = app;
