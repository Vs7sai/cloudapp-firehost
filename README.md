# 📱 Cloud Interview Prep - Android App

A comprehensive Android application for cloud computing interview preparation with daily study reminders, AdMob integration, and Firebase backend.

## 🚀 Features

### 📚 Core Features
- **Dynamic Question Loading**: Questions and topics loaded from Firebase Hosting
- **Multiple Difficulty Levels**: Beginner, Medium, Advanced
- **Cloud Topics**: AWS, Azure, Docker, Kubernetes, Git, DevOps, Terraform, and more
- **Firebase Authentication**: Secure user authentication
- **Responsive UI**: Auto-sizing text, 25% height question cards
- **Search Functionality**: Find questions by keywords

### 🔔 Notification System
- **Daily Study Reminders**: 3 notifications per day
  - 🌅 **Morning**: 9:00 AM - "Good morning! Start your day with cloud interview prep!"
  - 🌤️ **Afternoon**: 2:00 PM - "Afternoon break? Perfect time for quick interview prep!"
  - 🌙 **Evening**: 7:00 PM - "Evening study time! End your day with interview prep!"
- **15+ Motivational Messages**: Random inspiring content
- **Persistent Notifications**: Survive device restarts
- **Smart Permissions**: Automatic Android 13+ support

### 💰 Monetization
- **AdMob Integration**: Interstitial ads every 12th question
- **Revenue Ready**: Production-ready ad implementation

## 🏗️ Architecture

```
📦 Project Structure
├── 📱 Android App (app/)
│   ├── 🔐 Firebase Auth Integration
│   ├── 🔔 Notification System
│   ├── 💰 AdMob Integration
│   └── 🎨 Material Design UI
├── ☁️ Firebase Backend (firebasehosting/)
│   ├── 📊 Static JSON API
│   ├── 🌐 Firebase Hosting
│   └── 🔒 Client-side Authentication
└── 📋 Documentation & Config
```

## 🛠️ Setup & Deployment

### Prerequisites
- Android Studio
- Node.js & npm
- Firebase CLI
- Git

### 1. 📥 Clone Repository
```bash
git clone https://github.com/Vs7sai/cloudapp-firehost.git
cd cloudapp-firehost
```

### 2. ☁️ Backend Deployment (Firebase Hosting)

#### Install Firebase CLI
```bash
npm install -g firebase-tools
```

#### Login to Firebase
```bash
firebase login
```

#### Deploy Backend to Firebase Hosting
```bash
# Navigate to Firebase hosting directory
cd firebasehosting

# Initialize Firebase (if not already done)
firebase init hosting

# Deploy to Firebase Hosting
firebase deploy --only hosting
```

**Your backend will be deployed to**: `https://interviewfire-df24e.web.app/`

#### 🔍 Verify Backend Deployment
Test these endpoints after deployment:
```bash
# Test topics endpoint
curl https://interviewfire-df24e.web.app/api/topics.json

# Test questions endpoint
curl https://interviewfire-df24e.web.app/api/questions/aws/beginner.json
```

### 3. 📱 Android App Setup

#### Open in Android Studio
```bash
# Open the project in Android Studio
open -a "Android Studio" /path/to/cloudapp-firehost
```

#### Configure Firebase
1. Place your `google-services.json` in `app/` directory
2. Update Firebase project configuration if needed

#### AdMob Configuration
Update AdMob IDs in `AndroidManifest.xml`:
```xml
<!-- Replace with your AdMob App ID -->
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-9672937825247791~7373622085" />
```

Update Ad Unit ID in `AdMobHelper.kt`:
```kotlin
// Replace with your Ad Unit ID
private val adUnitId = "ca-app-pub-9672937825247791/1234567890"
```

#### 🔨 Build & Install
```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device/emulator
./gradlew installDebug

# Or build and install in one command
./gradlew installDebug
```

## 📂 Project Structure

### 🔙 Backend (`firebasehosting/`)
```
firebasehosting/
├── backend/
│   └── public/
│       └── api/
│           ├── topics.json              # Available topics
│           └── questions/
│               ├── aws/
│               │   ├── beginner.json
│               │   ├── medium.json
│               │   └── advanced.json
│               ├── azure/
│               ├── docker/
│               ├── kubernetes/
│               └── ... (other topics)
├── firebase.json                        # Firebase config
└── README.md
```

### 📱 Android App (`app/`)
```
app/src/main/
├── java/com/v7techsolution/interviewfire/
│   ├── MainActivity.kt                  # Main activity
│   ├── LoginActivity.kt                 # Login screen
│   ├── QuestionActivity.kt              # Question display
│   ├── NotificationHelper.kt            # Notification system
│   ├── AdMobHelper.kt                   # AdMob integration
│   ├── BootReceiver.kt                  # Boot persistence
│   └── api/
│       ├── ApiClient.kt                 # API client
│       └── CloudInterviewApiService.kt  # API service
├── res/
│   ├── layout/                          # UI layouts
│   ├── values/                          # Colors, strings, styles
│   └── ...
└── AndroidManifest.xml                  # App manifest
```

## 🔧 Configuration

### 🌐 API Endpoints
The app uses Firebase Hosting as a static file server:
- **Base URL**: `https://interviewfire-df24e.web.app/`
- **Topics**: `/api/topics.json`
- **Questions**: `/api/questions/{topic}/{difficulty}.json`

### 🔐 Authentication
- **Type**: Firebase Authentication
- **Client-side validation**: Blocks API calls if user not authenticated
- **Login required**: Yes, for accessing questions

### 🔔 Notification Schedule
- **Morning**: 9:00 AM daily
- **Afternoon**: 2:00 PM daily  
- **Evening**: 7:00 PM daily
- **Permissions**: Automatically requested on Android 13+

## 📱 App Usage

### 🔑 Login
1. Open app
2. Sign in with Firebase Authentication
3. Grant notification permissions when prompted

### 📖 Study Flow
1. Browse topics on home screen
2. Select difficulty level
3. Answer questions
4. Get ads every 12th question
5. Receive daily study reminders

### 🔔 Notifications
- Automatic daily reminders
- 15+ different motivational messages
- Tap notification to open app

## 🚀 Deployment Commands Summary

### Quick Deploy Backend
```bash
# One-time setup
npm install -g firebase-tools
firebase login

# Deploy (run from project root)
cd firebasehosting
firebase deploy --only hosting
```

### Quick Build Android
```bash
# From project root
./gradlew installDebug
```

## 📊 Available Topics & Questions

### 📋 Topics Include:
- ☁️ **AWS** (Amazon Web Services)
- 🌐 **Azure** (Microsoft Azure)
- 🐳 **Docker** (Containerization)
- ⚙️ **Kubernetes** (Container Orchestration)
- 📝 **Git** (Version Control)
- 🔧 **DevOps** (Development Operations)
- 🏗️ **Terraform** (Infrastructure as Code)
- 🛡️ **Cloud Security**
- 📈 **Cloud Architecture**

### 📈 Difficulty Levels:
- 🟢 **Beginner**: Entry-level concepts
- 🟡 **Medium**: Intermediate topics
- 🔴 **Advanced**: Expert-level questions

## 🎯 Key Features Implementation

### 🔔 Daily Notifications
- Uses `AlarmManager` for precise scheduling
- `BootReceiver` reschedules after device restart
- Android 13+ permission handling
- Clean, production-ready implementation

### 💰 AdMob Integration
- Interstitial ads every 12th question
- Proper ad lifecycle management
- Error handling and fallbacks
- Revenue optimization

### 🎨 UI/UX
- Material Design principles
- Responsive layouts
- Auto-sizing text
- Consistent theming
- No-scroll login design

## 🔍 Troubleshooting

### Backend Issues
```bash
# Check Firebase deployment status
firebase list

# Re-deploy if needed
firebase deploy --only hosting

# Check logs
firebase functions:log
```

### Android Build Issues
```bash
# Clean and rebuild
./gradlew clean
./gradlew installDebug

# Check for updates
./gradlew --refresh-dependencies
```

### Notification Issues
- Check notification permissions in device settings
- Ensure proper time zone configuration
- Verify AlarmManager permissions

## 📈 Production Checklist

### ✅ Before Going Live:
- [ ] Update AdMob IDs with real production IDs
- [ ] Test on multiple devices and Android versions
- [ ] Verify all API endpoints are working
- [ ] Test notification system thoroughly
- [ ] Update app signing configuration
- [ ] Set up proper analytics
- [ ] Test ad revenue flow

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:
- 📧 Email: support@v7techsolution.com
- 🐛 Issues: [GitHub Issues](https://github.com/Vs7sai/cloudapp-firehost/issues)

---

🚀 **Ready to help users ace their cloud interviews!** 🎯

Made with ❤️ by V7 Tech Solution