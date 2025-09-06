const admin = require('firebase-admin');

// Initialize Firebase Admin with service account
const serviceAccount = require('./interviewfire-df24e-firebase-adminsdk-fbsvc-f952860de1.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  projectId: 'interviewfire-df24e'
});

module.exports = admin;