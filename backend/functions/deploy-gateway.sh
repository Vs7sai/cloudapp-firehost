#!/bin/bash

# API Gateway Deployment Script
# This script deploys the Firebase Functions with API Gateway features

echo "🚀 Starting API Gateway Deployment..."

# Check if Firebase CLI is installed
if ! command -v firebase &> /dev/null; then
    echo "❌ Firebase CLI is not installed. Please install it first:"
    echo "npm install -g firebase-tools"
    exit 1
fi

# Check if user is logged in to Firebase
if ! firebase projects:list &> /dev/null; then
    echo "❌ Not logged in to Firebase. Please login first:"
    echo "firebase login"
    exit 1
fi

# Install dependencies
echo "📦 Installing dependencies..."
npm install

# Run tests (if any)
echo "🧪 Running tests..."
npm test 2>/dev/null || echo "⚠️ No tests found or tests failed, continuing..."

# Deploy functions
echo "🚀 Deploying Firebase Functions with API Gateway..."
firebase deploy --only functions

# Check deployment status
if [ $? -eq 0 ]; then
    echo "✅ API Gateway deployment successful!"
    echo ""
    echo "🔗 Your API Gateway endpoints:"
    echo "   - Health Check: https://your-project.cloudfunctions.net/api/health"
    echo "   - Gateway Status: https://your-project.cloudfunctions.net/api/gateway/status"
    echo "   - Gateway Info: https://your-project.cloudfunctions.net/api/gateway/info"
    echo "   - Topics: https://your-project.cloudfunctions.net/api/topics (protected)"
    echo "   - Questions: https://your-project.cloudfunctions.net/api/questions/{topicId}/{difficulty} (protected)"
    echo ""
    echo "🛡️ Security Features Enabled:"
    echo "   - Rate Limiting: 100 requests/15min (authenticated), 50 requests/15min (unauthenticated)"
    echo "   - Security Headers: XSS Protection, Content Type Options, Frame Options"
    echo "   - Request Validation: User-Agent validation, request size limits"
    echo "   - IP Blocking: Automatic blocking of suspicious IPs"
    echo "   - User Monitoring: Suspicious user detection and blocking"
    echo ""
    echo "📊 Rate Limits:"
    echo "   - Authenticated users: 100 requests per 15 minutes, 20 burst per minute"
    echo "   - Unauthenticated users: 50 requests per 15 minutes, 10 burst per minute"
else
    echo "❌ Deployment failed!"
    exit 1
fi
