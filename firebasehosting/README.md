# Firebase Hosting Project

This folder contains both the **Firebase Hosting backend** and **Android frontend** for the Cloud Interview Prep app.

## 📁 Project Structure

```
firebasehosting/
├── backend/                 # Firebase Hosting Backend
│   ├── firebase.json       # Firebase hosting configuration
│   └── public/             # Static JSON API files
│       ├── api/
│       │   ├── topics.json
│       │   ├── health.json
│       │   └── questions/
│       │       ├── aws/
│       │       ├── docker/
│       │       ├── git/
│       │       └── ...
│       └── index.html      # Landing page
├── frontend/               # Android App
│   ├── build.gradle.kts
│   ├── google-services.json
│   ├── debug.keystore
│   ├── proguard-rules.pro
│   └── src/               # Android source code
└── README.md              # This file
```

## 🚀 Quick Start

### Deploy Backend (Firebase Hosting)
```bash
cd backend

```

### Build & Install Frontend (Android)
```bash
cd frontend
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 🔗 API Endpoints

- **Base URL**: `https://interviewfire-df24e.web.app/`
- **Topics**: `GET /api/topics.json`
- **Questions**: `GET /api/questions/{topic}/{difficulty}.json`
- **Health**: `GET /api/health.json`

## ✨ Features

- **Dynamic Content**: Add topics/questions without rebuilding app
- **No Authentication**: Public access to all content
- **Static API**: Fast CDN delivery via Firebase Hosting
- **Cross-Platform**: Works on Android, iOS, Web

## 📱 Android App Features

- Topic selection with difficulty levels
- Markdown rendering for questions/answers
- Code syntax highlighting
- Offline support
- Responsive design

## 🛠️ Development

### Adding New Topics
1. Add topic to `backend/public/api/topics.json`
2. Create question files in `backend/public/api/questions/{topic}/`
3. Deploy: `cd backend && firebase deploy --only hosting`
4. Topic appears in app automatically!

### Adding New Questions
1. Edit question files in `backend/public/api/questions/{topic}/{difficulty}.json`
2. Deploy: `cd backend && firebase deploy --only hosting`
3. Questions update in app automatically!

## 📊 Current Topics

- AWS (beginner, medium, pro)
- Docker (beginner, medium, pro)
- Git (beginner, medium, pro)
- Kubernetes (beginner, medium, pro)
- Terraform (beginner, medium, pro)
- Ansible (beginner, medium, pro)
- GCP (beginner)
- Bitbucket (beginner, medium)
- Alibaba (beginner, medium, pro)
- Vamsi (beginner, medium, pro)
- Sai (beginner, medium, pro)

## 🔧 Configuration

- **Firebase Project**: `interviewfire-df24e`
- **Hosting URL**: `https://interviewfire-df24e.web.app`
- **Android Package**: `com.v7techsolution.interviewfire`

## 📝 Notes

- Backend uses static JSON files for maximum performance
- Frontend dynamically loads content from backend
- No server-side processing required
- Completely free to host and deploy
# cloudapp-firebase-hosting
# cloudappfirebasehost
