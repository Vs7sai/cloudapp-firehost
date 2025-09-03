#!/bin/bash

# Secure Deployment Script for Google Cloud Functions
# This script deploys the function with enhanced security configurations

set -e

echo "🔒 Deploying Cloud Interview Prep API with Enhanced Security"
echo "=========================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_ID="interviewfire-df24e"
FUNCTION_NAME="api"
REGION="us-central1"
MEMORY="512MB"
TIMEOUT="60s"
MAX_INSTANCES="10"

echo -e "${BLUE}📋 Deployment Configuration:${NC}"
echo "  Project ID: $PROJECT_ID"
echo "  Function Name: $FUNCTION_NAME"
echo "  Region: $REGION"
echo "  Memory: $MEMORY"
echo "  Timeout: $TIMEOUT"
echo "  Max Instances: $MAX_INSTANCES"
echo ""

# Check if Firebase CLI is installed
if ! command -v firebase &> /dev/null; then
    echo -e "${RED}❌ Firebase CLI is not installed. Please install it first.${NC}"
    echo "npm install -g firebase-tools"
    exit 1
fi

# Check if user is logged in
if ! firebase projects:list &> /dev/null; then
    echo -e "${YELLOW}⚠️  Not logged in to Firebase. Please login first.${NC}"
    firebase login
fi

# Set the project
echo -e "${BLUE}🔧 Setting Firebase project...${NC}"
firebase use $PROJECT_ID

# Install dependencies
echo -e "${BLUE}📦 Installing dependencies...${NC}"
cd functions
npm install

# Run security setup
echo -e "${BLUE}🔒 Setting up security configuration...${NC}"
if [ -f "scripts/setup-security.js" ]; then
    node scripts/setup-security.js
    echo -e "${GREEN}✅ Security configuration completed${NC}"
else
    echo -e "${YELLOW}⚠️  Security setup script not found, skipping...${NC}"
fi

# Deploy the function
echo -e "${BLUE}🚀 Deploying Cloud Function...${NC}"
firebase deploy --only functions:$FUNCTION_NAME

# Get the function URL
echo -e "${BLUE}🔗 Getting function URL...${NC}"
FUNCTION_URL=$(firebase functions:config:get --project $PROJECT_ID 2>/dev/null || echo "")
if [ -z "$FUNCTION_URL" ]; then
    FUNCTION_URL="https://$REGION-$PROJECT_ID.cloudfunctions.net/$FUNCTION_NAME"
fi

echo ""
echo -e "${GREEN}🎉 Deployment completed successfully!${NC}"
echo ""
echo -e "${BLUE}📋 Deployment Summary:${NC}"
echo "  Function URL: $FUNCTION_URL"
echo "  Region: $REGION"
echo "  Memory: $MEMORY"
echo "  Timeout: $TIMEOUT"
echo "  Max Instances: $MAX_INSTANCES"
echo ""
echo -e "${BLUE}🔒 Security Features Enabled:${NC}"
echo "  ✅ Firebase Authentication"
echo "  ✅ Rate Limiting"
echo "  ✅ Security Headers"
echo "  ✅ Request Validation"
echo "  ✅ IP Blocking"
echo "  ✅ Suspicious User Detection"
echo "  ✅ API Key Authentication"
echo "  ✅ Enhanced CORS"
echo "  ✅ Security Logging"
echo ""
echo -e "${BLUE}🧪 Test Endpoints:${NC}"
echo "  Health Check: $FUNCTION_URL/api/health"
echo "  Gateway Status: $FUNCTION_URL/api/gateway/status"
echo "  Gateway Info: $FUNCTION_URL/api/gateway/info"
echo "  Test Auth: $FUNCTION_URL/api/test-auth (requires authentication)"
echo ""
echo -e "${YELLOW}⚠️  Security Notes:${NC}"
echo "  - Update CORS origins in index.js for production"
echo "  - Store API keys securely"
echo "  - Monitor security events regularly"
echo "  - Review rate limits based on usage"
echo ""
echo -e "${GREEN}✅ Your secure API is ready!${NC}"
