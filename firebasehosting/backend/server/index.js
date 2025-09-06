const functions = require('firebase-functions');
const express = require('express');
const cors = require('cors');
const admin = require('./firebase-admin.js');
const path = require('path');

const app = express();

// CORS configuration
const corsOptions = {
  origin: true,
  credentials: true,
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: [
    'Origin',
    'X-Requested-With',
    'Content-Type',
    'Accept',
    'Authorization'
  ]
};

app.use(cors(corsOptions));
app.use(express.json());

// Authentication middleware
const authenticate = async (req, res, next) => {
  try {
    const authHeader = req.headers.authorization;

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      console.log('❌ No valid Authorization header found');
      return res.status(401).json({
        success: false,
        error: 'Authentication required',
        message: 'Please provide a valid Firebase ID token in Authorization header'
      });
    }

    const idToken = authHeader.split(' ')[1];

    if (!idToken) {
      console.log('❌ No token found in Authorization header');
      return res.status(401).json({
        success: false,
        error: 'Authentication required',
        message: 'Please provide a valid Firebase ID token'
      });
    }

    // Verify the Firebase ID token
    const decodedToken = await admin.auth().verifyIdToken(idToken);
    console.log('✅ User authenticated:', decodedToken.uid, decodedToken.email);

    // Add user info to request
    req.user = decodedToken;
    next();

  } catch (error) {
    console.log('❌ Authentication failed:', error.message);
    return res.status(401).json({
      success: false,
      error: 'Authentication failed',
      message: 'Invalid or expired token'
    });
  }
};

// Topics data
const topics = [
  {
    id: 'aws',
    name: 'AWS',
    color: '#FF9900',
    icon: '☁️'
  },
  {
    id: 'docker',
    name: 'Docker',
    color: '#2496ED',
    icon: '🐳'
  },
  {
    id: 'kubernetes',
    name: 'Kubernetes',
    color: '#326CE5',
    icon: '⚙️'
  },
  {
    id: 'git',
    name: 'Git',
    color: '#F05032',
    icon: '📝'
  },
  {
    id: 'terraform',
    name: 'Terraform',
    color: '#7B42BC',
    icon: '🏗️'
  },
  {
    id: 'ansible',
    name: 'Ansible',
    color: '#EE0000',
    icon: '🔧'
  },
  {
    id: 'gcp',
    name: 'GCP',
    color: '#4285F4',
    icon: '☁️'
  },
  {
    id: 'bitbucket',
    name: 'Bitbucket',
    color: '#0052CC',
    icon: '📦'
  },
  {
    id: 'alibaba',
    name: 'Alibaba',
    color: '#FF6A00',
    icon: '🏢'
  },
  {
    id: 'vamsi',
    name: 'Vamsi',
    color: '#FF6A00',
    icon: '⚙️'
  },
  {
    id: 'sai',
    name: 'Sai',
    color: '#FF6A00',
    icon: '⚙️'
  }
];

// Questions data
const questions = {
  aws: {
    beginner: [
      {
        id: 1,
        text: "What is AWS?",
        textSize: 24,
        explanation: "Amazon Web Services (AWS) is a comprehensive cloud computing platform provided by Amazon. It offers over 200 services including computing, storage, databases, networking, and more."
      },
      {
        id: 2,
        text: "What is EC2?",
        textSize: 24,
        explanation: "Elastic Compute Cloud (EC2) is a web service that provides resizable compute capacity in the cloud. EC2 allows you to launch virtual machines with different configurations and operating systems."
      },
      {
        id: 3,
        text: "What is S3?",
        textSize: 24,
        explanation: "Simple Storage Service (S3) is an object storage service that offers industry-leading scalability, data availability, security, and performance. It's designed to store and retrieve any amount of data from anywhere."
      }
    ],
    medium: [
      {
        id: 1,
        text: "Explain the difference between S3 and EBS.",
        textSize: 20,
        explanation: "S3 is object storage for files, while EBS is block storage for EC2 instances. S3 is designed for storing and retrieving any amount of data, while EBS provides persistent block-level storage volumes for EC2 instances."
      },
      {
        id: 2,
        text: "What is Auto Scaling?",
        textSize: 20,
        explanation: "Auto Scaling automatically adjusts the number of EC2 instances based on demand. It helps maintain application availability and allows you to scale your Amazon EC2 capacity up or down automatically according to conditions you define."
      }
    ],
    pro: [
      {
        id: 1,
        text: "How would you design a highly available multi-region architecture on AWS?",
        textSize: 18,
        explanation: "A highly available multi-region architecture would include: 1) Multiple Availability Zones, 2) Auto Scaling Groups, 3) Application Load Balancers, 4) RDS Multi-AZ deployments, 5) CloudFront CDN, 6) Route 53 for DNS failover, and 7) Cross-region replication for data."
      }
    ]
  },
  docker: {
    beginner: [
      {
        id: 1,
        text: "What is Docker?",
        textSize: 24,
        explanation: "Docker is a containerization platform that allows you to package applications and their dependencies into lightweight, portable containers that can run consistently across different environments."
      },
      {
        id: 2,
        text: "What is a Docker image?",
        textSize: 24,
        explanation: "A Docker image is a read-only template that contains a set of instructions for creating a container. It includes the application code, runtime, system tools, libraries, and settings."
      },
      {
        id: 3,
        text: "What is a Docker container?",
        textSize: 24,
        explanation: "A Docker container is a running instance of a Docker image. It's a lightweight, standalone package that includes everything needed to run an application: code, runtime, system tools, libraries, and settings."
      }
    ],
    medium: [
      {
        id: 1,
        text: "Explain Docker networking modes.",
        textSize: 20,
        explanation: "Docker supports several networking modes: 1) Bridge (default) - containers can communicate with each other, 2) Host - container shares host's network stack, 3) None - no networking, 4) Overlay - for multi-host networking, 5) Macvlan - assigns MAC address to container."
      }
    ],
    pro: [
      {
        id: 1,
        text: "How would you optimize Docker images for production?",
        textSize: 18,
        explanation: "Optimization strategies include: 1) Use multi-stage builds, 2) Choose minimal base images (Alpine), 3) Combine RUN commands to reduce layers, 4) Use .dockerignore, 5) Remove unnecessary packages, 6) Use specific version tags, 7) Leverage build cache, 8) Scan for vulnerabilities."
      }
    ]
  }
};

// Protected static file endpoints (replacing direct static access)

// Serve static topics.json with authentication - PROTECTED
app.get('/topics.json', authenticate, (req, res) => {
  try {
    console.log('📋 Static topics.json requested by user:', req.user.uid);
    res.json({
      success: true,
      data: topics,
      user: {
        uid: req.user.uid,
        email: req.user.email,
        name: req.user.name,
        emailVerified: req.user.email_verified
      }
    });
  } catch (error) {
    console.error('❌ Error serving static topics.json:', error);
    res.status(500).json({
      success: false,
      error: 'Internal server error',
      message: 'Failed to serve topics.json'
    });
  }
});

// Serve static questions JSON files with authentication - PROTECTED
app.get('/questions/:topicId/:difficulty.json', authenticate, (req, res) => {
  try {
    const { topicId, difficulty } = req.params;
    console.log(`❓ Static questions JSON requested for ${topicId}/${difficulty} by user:`, req.user.uid);

    if (!questions[topicId] || !questions[topicId][difficulty]) {
      return res.status(404).json({
        success: false,
        error: 'Not found',
        message: `No questions found for topic: ${topicId}, difficulty: ${difficulty}`
      });
    }

    res.json({
      success: true,
      data: {
        topic: topicId,
        difficulty: difficulty,
        questions: questions[topicId][difficulty]
      },
      user: {
        uid: req.user.uid,
        email: req.user.email,
        name: req.user.name,
        emailVerified: req.user.email_verified
      }
    });
  } catch (error) {
    console.error('❌ Error serving static questions JSON:', error);
    res.status(500).json({
      success: false,
      error: 'Internal server error',
      message: 'Failed to serve questions JSON'
    });
  }
});

// Protected API endpoints

// Get all topics - PROTECTED
app.get('/topics', authenticate, (req, res) => {
  try {
    console.log('📋 Topics requested by user:', req.user.uid);
    res.json({
      success: true,
      data: topics,
      user: {
        uid: req.user.uid,
        email: req.user.email,
        name: req.user.name,
        emailVerified: req.user.email_verified
      }
    });
  } catch (error) {
    console.error('❌ Error fetching topics:', error);
    res.status(500).json({
      success: false,
      error: 'Internal server error',
      message: 'Failed to fetch topics'
    });
  }
});

// Get questions for a topic and difficulty - PROTECTED
app.get('/questions/:topicId/:difficulty', authenticate, (req, res) => {
  try {
    const { topicId, difficulty } = req.params;
    console.log(`❓ Questions requested for ${topicId}/${difficulty} by user:`, req.user.uid);

    if (!questions[topicId] || !questions[topicId][difficulty]) {
      return res.status(404).json({
        success: false,
        error: 'Not found',
        message: `No questions found for topic: ${topicId}, difficulty: ${difficulty}`
      });
    }

    res.json({
      success: true,
      data: {
        topic: topicId,
        difficulty: difficulty,
        questions: questions[topicId][difficulty]
      },
      user: {
        uid: req.user.uid,
        email: req.user.email,
        name: req.user.name,
        emailVerified: req.user.email_verified
      }
    });
  } catch (error) {
    console.error('❌ Error fetching questions:', error);
    res.status(500).json({
      success: false,
      error: 'Internal server error',
      message: 'Failed to fetch questions'
    });
  }
});

// Test authentication endpoint - PROTECTED
app.get('/test-auth', authenticate, (req, res) => {
  res.json({
    success: true,
    message: 'Authentication successful',
    user: {
      uid: req.user.uid,
      email: req.user.email,
      name: req.user.name,
      emailVerified: req.user.email_verified
    }
  });
});

// Health check endpoint - PUBLIC
app.get('/health', (req, res) => {
  res.json({
    success: true,
    message: 'Cloud Interview Prep API is running',
    timestamp: new Date().toISOString(),
    platform: 'Express.js Server',
    authentication: 'Required for data endpoints',
    secureEndpoints: [
      '/api/topics.json (protected)',
      '/api/questions/:topicId/:difficulty.json (protected)',
      '/api/topics (protected)',
      '/api/questions/:topicId/:difficulty (protected)'
    ]
  });
});

// Serve static health.json with same format - PUBLIC
app.get('/health.json', (req, res) => {
  res.json({
    success: true,
    message: 'Cloud Interview Prep API is running',
    timestamp: new Date().toISOString(),
    platform: 'Express.js Server',
    authentication: 'Required for data endpoints'
  });
});

// Root endpoint - PUBLIC
app.get('/', (req, res) => {
  res.json({
    message: 'Cloud Interview Prep Backend API',
    version: '2.0.0',
    platform: 'Firebase Cloud Functions',
    features: [
      'Firebase Authentication Required',
      'Secure API Endpoints',
      'Interview Questions Database'
    ],
    endpoints: {
      topics: '/api/topics (protected)',
      questions: '/api/questions/:topicId/:difficulty (protected)',
      'test-auth': '/api/test-auth (protected)',
      health: '/api/health (public)'
    },
    authentication: 'All data endpoints require valid Firebase ID token in Authorization header'
  });
});

// Error handling middleware
app.use((err, req, res, next) => {
  console.error('❌ Server error:', err.stack);
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

// Export the Express app as a Firebase Cloud Function
exports.api = functions.https.onRequest(app);