const admin = require('firebase-admin');

// Initialize Firebase Admin SDK
const serviceAccount = require('./interviewfire-df24e-firebase-adminsdk-fbsvc-f952860de1.json');

// Initialize the app with the service account
if (!admin.apps.length) {
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    projectId: 'interviewfire-df24e'
  });
}

module.exports = admin;