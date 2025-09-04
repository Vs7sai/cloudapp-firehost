# 🚀 Dynamic Interview Preparation App

A fully dynamic Android interview preparation app with Firebase backend that allows adding new topics and questions without rebuilding the app.

## 📱 Features

- **Dynamic Topic Loading** - Topics load from Firebase API in real-time
- **Dynamic Question Loading** - Questions load from Firebase API when selected
- **No App Rebuilds** - Add new topics/questions by just deploying backend
- **Multiple Difficulty Levels** - Beginner, Medium, Pro for each topic
- **Markdown Support** - Rich text formatting for questions and explanations
- **Code Highlighting** - Syntax highlighting for code examples
- **Responsive Design** - Works on phones and tablets
- **Offline Support** - Cached content for better performance

## 🏗️ Architecture

### Frontend (Android)
- **Language**: Kotlin
- **Architecture**: MVVM with Fragments
- **Networking**: Retrofit + OkHttp
- **UI**: Material Design with custom components
- **Markdown**: Custom MarkdownTextView with code highlighting

### Backend (Firebase)
- **Hosting**: Firebase Hosting for static JSON API
- **Storage**: Static JSON files for topics and questions
- **Deployment**: Firebase CLI for easy updates

## 📁 Project Structure

```
cloudapp/
├── app/                          # Current working Android app
│   ├── src/main/java/com/v7techsolution/interviewfire/
│   │   ├── HomeFragment.kt       # Main topics screen
│   │   ├── QuestionActivity.kt   # Questions display
│   │   ├── TopicActivity.kt      # Topic selection
│   │   └── api/                  # API client and models
│   └── src/main/res/             # Android resources
├── firebase-backend/             # Current working Firebase backend
│   ├── public/api/
│   │   ├── topics.json          # List of all topics
│   │   └── questions/           # Question files by topic/difficulty
│   └── firebase.json            # Firebase configuration
├── backend_copy/                 # Original backend backup
├── frontend_copy/                # Original frontend backup
└── backend/                      # Original backend
```

## 🚀 Quick Start

### Prerequisites
- Android Studio
- Firebase CLI
- Git

### Setup
1. **Clone the repository**
   ```bash
   git clone https://github.com/Vs7sai/cloudapp.git
   cd cloudapp
   ```

2. **Setup Firebase Backend**
   ```bash
   cd firebase-backend
   firebase login
   firebase use interviewfire-df24e
   firebase deploy --only hosting
   ```

3. **Build Android App**
   ```bash
   # Open in Android Studio
   # Build and run on emulator/device
   ```

## 📊 Current Topics

| Topic | Beginner | Medium | Pro | Total Questions |
|-------|----------|--------|-----|----------------|
| AWS | 4 | 2 | 2 | 8 |
| Docker | 62 | 3 | 3 | 68 |
| Kubernetes | 3 | 2 | 2 | 7 |
| Git | 3 | 2 | 2 | 7 |
| Terraform | 3 | 2 | 2 | 7 |
| Ansible | 3 | 2 | 2 | 7 |
| Bitbucket | 3 | 2 | 2 | 7 |
| GCP | 3 | 2 | 2 | 7 |
| Alibaba Cloud | 3 | 2 | 2 | 7 |
| Vamsi | 3 | 2 | 2 | 7 |
| Jenkins | 3 | 2 | 2 | 7 |

**Total: 11 topics, 150+ questions**

## 🔧 Adding New Topics

### Step 1: Add Topic to topics.json
```json
{
  "id": "new-topic",
  "name": "New Topic Name",
  "color": "#HEXCOLOR",
  "icon": "🎯"
}
```

### Step 2: Create Question Files
```
firebase-backend/public/api/questions/new-topic/
├── beginner.json
├── medium.json
└── pro.json
```

### Step 3: Deploy Backend
```bash
cd firebase-backend
firebase deploy --only hosting
```

### Step 4: Done! 🎉
- Topic appears automatically in app
- No app rebuild needed
- Questions work immediately

## 📝 Question File Format

```json
{
  "success": true,
  "data": {
    "topic": "docker",
    "difficulty": "beginner",
    "questions": [
      {
        "id": 1,
        "text": "What is Docker?",
        "textSize": 20,
        "explanation": "Docker is a containerization platform..."
      }
    ]
  },
  "user": {
    "uid": "static-user",
    "email": "static@example.com",
    "name": "Static User",
    "emailVerified": true
  }
}
```

## 🌐 API Endpoints

- **Topics**: `GET /api/topics.json`
- **Questions**: `GET /api/questions/{topic}/{difficulty}.json`
- **Health**: `GET /api/health.json`

## 🚀 Deployment Commands

### Deploy Firebase Backend
```bash
cd firebase-backend && firebase deploy --only hosting
```

### Build Android App
```bash
./gradlew assembleDebug
```

### Install on Emulator
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 🔄 Dynamic System

The app is **100% dynamic**:

1. **Topics load from API** - `GET /api/topics.json`
2. **Questions load from API** - `GET /api/questions/{topic}/{difficulty}.json`
3. **Real-time updates** - Just deploy backend changes
4. **No app rebuilds** - Frontend automatically adapts
5. **Unlimited topics** - Add as many as you want

## 📱 App Features

### Home Screen
- Grid of topic cards with colors and icons
- Pull-to-refresh to reload topics
- Responsive design for different screen sizes

### Topic Selection
- Three difficulty levels: Beginner, Medium, Pro
- Visual difficulty indicators
- Smooth transitions

### Question Display
- Markdown support for rich text
- Code syntax highlighting
- Navigation between questions
- Search functionality
- Progress tracking

## 🛠️ Development

### Adding New Features
1. Modify Android app code in `app/`
2. Build and test locally
3. Commit changes to repository

### Updating Backend
1. Modify files in `firebase-backend/public/`
2. Deploy with `firebase deploy --only hosting`
3. Changes appear instantly in app

## 📚 Documentation

- [Firebase Setup Guide](FIREBASE_SETUP_GUIDE.md)
- [Google Auth Setup](GOOGLE_AUTH_SETUP.md)
- [Security Guide](SECURITY_GUIDE.md)
- [Implementation Summary](IMPLEMENTATION_SUMMARY_FINAL.md)

## 🔒 Security

- No authentication required for static API
- Firebase service account keys excluded from repository
- Build files and dependencies properly ignored
- HTTPS enforced for all API calls

## 🌟 Live Demo

- **Firebase Hosting**: https://interviewfire-df24e.web.app
- **Topics API**: https://interviewfire-df24e.web.app/api/topics.json
- **Health Check**: https://interviewfire-df24e.web.app/api/health.json

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👨‍💻 Author

**Vamsi Sai**
- GitHub: [@Vs7sai](https://github.com/Vs7sai)
- Project: [cloudapp](https://github.com/Vs7sai/cloudapp)

## 🙏 Acknowledgments

- Firebase for hosting and backend services
- Android team for the development platform
- Open source community for various libraries

---

**🎯 Ready to add more topics? Just update the backend and deploy!**