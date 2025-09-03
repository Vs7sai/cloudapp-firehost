#!/usr/bin/env node

/**
 * Security Monitoring Script
 * Monitors security events and provides alerts
 */

const admin = require('firebase-admin');
const serviceAccount = require('../interviewfire-df24e-firebase-adminsdk-fbsvc-f952860de1.json');

// Initialize Firebase Admin
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: `https://${serviceAccount.project_id}-default-rtdb.firebaseio.com`
});

const db = admin.firestore();

class SecurityMonitor {
  constructor() {
    this.alertThresholds = {
      failedAuthAttempts: 5, // per minute
      rateLimitViolations: 10, // per minute
      suspiciousRequests: 3, // per minute
      blockedIPs: 1 // per minute
    };
  }

  async monitorSecurityEvents() {
    console.log('🔍 Starting security monitoring...');
    
    try {
      // Get recent security events (last 5 minutes)
      const fiveMinutesAgo = new Date(Date.now() - 5 * 60 * 1000);
      const eventsSnapshot = await db.collection('security_events')
        .where('timestamp', '>=', fiveMinutesAgo)
        .orderBy('timestamp', 'desc')
        .limit(100)
        .get();

      const events = [];
      eventsSnapshot.forEach(doc => {
        events.push({ id: doc.id, ...doc.data() });
      });

      // Analyze events
      const analysis = this.analyzeEvents(events);
      
      // Generate report
      this.generateReport(analysis);
      
      // Check for alerts
      await this.checkAlerts(analysis);

    } catch (error) {
      console.error('❌ Error monitoring security events:', error);
    }
  }

  analyzeEvents(events) {
    const analysis = {
      totalEvents: events.length,
      eventTypes: {},
      topIPs: {},
      topUsers: {},
      suspiciousPatterns: [],
      timeDistribution: {}
    };

    events.forEach(event => {
      // Count event types
      analysis.eventTypes[event.event] = (analysis.eventTypes[event.event] || 0) + 1;
      
      // Count IPs
      if (event.ip) {
        analysis.topIPs[event.ip] = (analysis.topIPs[event.ip] || 0) + 1;
      }
      
      // Count users
      if (event.user && event.user.uid) {
        analysis.topUsers[event.user.uid] = (analysis.topUsers[event.user.uid] || 0) + 1;
      }
      
      // Check for suspicious patterns
      if (this.isSuspiciousEvent(event)) {
        analysis.suspiciousPatterns.push(event);
      }
      
      // Time distribution
      const hour = new Date(event.timestamp.toDate()).getHours();
      analysis.timeDistribution[hour] = (analysis.timeDistribution[hour] || 0) + 1;
    });

    return analysis;
  }

  isSuspiciousEvent(event) {
    const suspiciousEvents = [
      'blocked_ip_access',
      'suspicious_user_access',
      'rate_limit_exceeded',
      'invalid_headers',
      'request_too_large'
    ];
    
    return suspiciousEvents.includes(event.event);
  }

  generateReport(analysis) {
    console.log('\n📊 Security Report (Last 5 minutes)');
    console.log('=====================================');
    console.log(`Total Events: ${analysis.totalEvents}`);
    
    console.log('\n📈 Event Types:');
    Object.entries(analysis.eventTypes)
      .sort(([,a], [,b]) => b - a)
      .forEach(([type, count]) => {
        console.log(`  ${type}: ${count}`);
      });
    
    console.log('\n🌐 Top IPs:');
    Object.entries(analysis.topIPs)
      .sort(([,a], [,b]) => b - a)
      .slice(0, 5)
      .forEach(([ip, count]) => {
        console.log(`  ${ip}: ${count} requests`);
      });
    
    console.log('\n👥 Top Users:');
    Object.entries(analysis.topUsers)
      .sort(([,a], [,b]) => b - a)
      .slice(0, 5)
      .forEach(([uid, count]) => {
        console.log(`  ${uid}: ${count} requests`);
      });
    
    if (analysis.suspiciousPatterns.length > 0) {
      console.log('\n🚨 Suspicious Events:');
      analysis.suspiciousPatterns.slice(0, 5).forEach(event => {
        console.log(`  ${event.event} from ${event.ip} at ${event.timestamp.toDate()}`);
      });
    }
  }

  async checkAlerts(analysis) {
    const alerts = [];
    
    // Check for high failed auth attempts
    const failedAuth = analysis.eventTypes['authentication_failed'] || 0;
    if (failedAuth > this.alertThresholds.failedAuthAttempts) {
      alerts.push({
        type: 'HIGH_FAILED_AUTH',
        message: `${failedAuth} failed authentication attempts in 5 minutes`,
        severity: 'HIGH'
      });
    }
    
    // Check for rate limit violations
    const rateLimitViolations = analysis.eventTypes['rate_limit_exceeded'] || 0;
    if (rateLimitViolations > this.alertThresholds.rateLimitViolations) {
      alerts.push({
        type: 'HIGH_RATE_LIMIT_VIOLATIONS',
        message: `${rateLimitViolations} rate limit violations in 5 minutes`,
        severity: 'MEDIUM'
      });
    }
    
    // Check for suspicious requests
    if (analysis.suspiciousPatterns.length > this.alertThresholds.suspiciousRequests) {
      alerts.push({
        type: 'SUSPICIOUS_ACTIVITY',
        message: `${analysis.suspiciousPatterns.length} suspicious events detected`,
        severity: 'HIGH'
      });
    }
    
    // Log alerts
    if (alerts.length > 0) {
      console.log('\n🚨 SECURITY ALERTS:');
      alerts.forEach(alert => {
        console.log(`  [${alert.severity}] ${alert.type}: ${alert.message}`);
      });
      
      // Store alerts in Firestore
      await this.storeAlerts(alerts);
    } else {
      console.log('\n✅ No security alerts');
    }
  }

  async storeAlerts(alerts) {
    try {
      const alertDoc = {
        timestamp: admin.firestore.FieldValue.serverTimestamp(),
        alerts: alerts,
        totalAlerts: alerts.length
      };
      
      await db.collection('security_alerts').add(alertDoc);
      console.log('📝 Alerts stored in Firestore');
    } catch (error) {
      console.error('❌ Error storing alerts:', error);
    }
  }

  async getSecurityStats() {
    try {
      const stats = {
        totalBlockedIPs: 0,
        totalSuspiciousUsers: 0,
        totalAPIKeys: 0,
        activeAPIKeys: 0
      };
      
      // Get blocked IPs count
      const blockedIPsDoc = await db.collection('security').doc('blocked_ips').get();
      if (blockedIPsDoc.exists) {
        stats.totalBlockedIPs = blockedIPsDoc.data().ips?.length || 0;
      }
      
      // Get suspicious users count
      const suspiciousUsersDoc = await db.collection('security').doc('suspicious_users').get();
      if (suspiciousUsersDoc.exists) {
        stats.totalSuspiciousUsers = suspiciousUsersDoc.data().users?.length || 0;
      }
      
      // Get API keys count
      const apiKeysSnapshot = await db.collection('api_keys').get();
      stats.totalAPIKeys = apiKeysSnapshot.size;
      apiKeysSnapshot.forEach(doc => {
        if (doc.data().isActive !== false) {
          stats.activeAPIKeys++;
        }
      });
      
      return stats;
    } catch (error) {
      console.error('❌ Error getting security stats:', error);
      return null;
    }
  }
}

// Run monitoring
async function runMonitoring() {
  const monitor = new SecurityMonitor();
  
  console.log('🔒 Security Monitor Started');
  console.log('==========================');
  
  // Get current stats
  const stats = await monitor.getSecurityStats();
  if (stats) {
    console.log('\n📊 Current Security Stats:');
    console.log(`  Blocked IPs: ${stats.totalBlockedIPs}`);
    console.log(`  Suspicious Users: ${stats.totalSuspiciousUsers}`);
    console.log(`  Total API Keys: ${stats.totalAPIKeys}`);
    console.log(`  Active API Keys: ${stats.activeAPIKeys}`);
  }
  
  // Monitor events
  await monitor.monitorSecurityEvents();
  
  console.log('\n✅ Security monitoring completed');
}

// Run if called directly
if (require.main === module) {
  runMonitoring().then(() => {
    process.exit(0);
  }).catch((error) => {
    console.error('❌ Monitoring failed:', error);
    process.exit(1);
  });
}

module.exports = { SecurityMonitor };
