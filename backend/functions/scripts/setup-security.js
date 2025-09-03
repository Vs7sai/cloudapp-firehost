#!/usr/bin/env node

/**
 * Security Setup Script
 * Initializes security collections and configurations in Firestore
 */

const admin = require('firebase-admin');
const path = require('path');

// Initialize Firebase Admin
const serviceAccount = require('../interviewfire-df24e-firebase-adminsdk-fbsvc-f952860de1.json');
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: `https://${serviceAccount.project_id}-default-rtdb.firebaseio.com`
});

const db = admin.firestore();

async function setupSecurity() {
  console.log('🔒 Setting up security configuration...');

  try {
    // 1. Initialize blocked IPs collection
    await db.collection('security').doc('blocked_ips').set({
      ips: [],
      lastUpdated: admin.firestore.FieldValue.serverTimestamp(),
      description: 'List of blocked IP addresses'
    });
    console.log('✅ Blocked IPs collection initialized');

    // 2. Initialize suspicious users collection
    await db.collection('security').doc('suspicious_users').set({
      users: [],
      lastUpdated: admin.firestore.FieldValue.serverTimestamp(),
      description: 'List of suspicious user IDs'
    });
    console.log('✅ Suspicious users collection initialized');

    // 3. Initialize rate limits collection
    await db.collection('rate_limits').doc('config').set({
      userLimits: {
        requests: 100,
        window: 15 * 60 * 1000, // 15 minutes
        burst: 20
      },
      ipLimits: {
        requests: 50,
        window: 15 * 60 * 1000, // 15 minutes
        burst: 10
      },
      lastUpdated: admin.firestore.FieldValue.serverTimestamp()
    });
    console.log('✅ Rate limits configuration initialized');

    // 4. Initialize API keys collection
    await db.collection('api_keys').doc('config').set({
      defaultPermissions: ['read'],
      defaultExpirationDays: 365,
      maxKeysPerUser: 5,
      lastUpdated: admin.firestore.FieldValue.serverTimestamp()
    });
    console.log('✅ API keys configuration initialized');

    // 5. Create security rules collection
    await db.collection('security_rules').doc('default').set({
      rules: {
        maxRequestSize: 1024 * 1024, // 1MB
        maxConcurrentRequests: 10,
        allowedUserAgents: [
          'Mozilla/5.0',
          'Chrome/',
          'Safari/',
          'Firefox/',
          'Edge/'
        ],
        blockedUserAgents: [
          'bot',
          'crawler',
          'spider',
          'scraper',
          'curl',
          'wget'
        ],
        suspiciousPatterns: [
          'sql',
          'script',
          'union',
          'select',
          'drop',
          'delete'
        ]
      },
      lastUpdated: admin.firestore.FieldValue.serverTimestamp()
    });
    console.log('✅ Security rules initialized');

    // 6. Create admin API key
    const adminKey = generateAPIKey();
    await db.collection('api_keys').doc(adminKey).set({
      name: 'Admin API Key',
      permissions: ['admin', 'read', 'write'],
      expiresAt: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000), // 1 year
      isActive: true,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      usageCount: 0,
      createdBy: 'system'
    });
    console.log('✅ Admin API key created');

    console.log('\n🎉 Security setup completed successfully!');
    console.log('\n📋 Summary:');
    console.log('- Blocked IPs collection: ✅');
    console.log('- Suspicious users collection: ✅');
    console.log('- Rate limits configuration: ✅');
    console.log('- API keys configuration: ✅');
    console.log('- Security rules: ✅');
    console.log(`- Admin API key: ${adminKey}`);
    
    console.log('\n🔑 Admin API Key (save this securely):');
    console.log(adminKey);
    console.log('\n⚠️  IMPORTANT: Store this API key securely and never commit it to version control!');

  } catch (error) {
    console.error('❌ Error setting up security:', error);
    process.exit(1);
  }
}

function generateAPIKey() {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = 'cip_admin_';
  
  for (let i = 0; i < 32; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  
  return result;
}

// Run the setup
setupSecurity().then(() => {
  console.log('\n✅ Security setup completed');
  process.exit(0);
}).catch((error) => {
  console.error('❌ Setup failed:', error);
  process.exit(1);
});
