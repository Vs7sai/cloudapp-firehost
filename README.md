# Cloud Interview Prep Android App

This Android application fetches interview questions and topics from a Node.js backend API for cloud computing topics like AWS, Docker, Kubernetes, etc.

## Features

- **Topic Selection**: Browse different cloud computing topics (AWS, Docker, Kubernetes, etc.)
- **Difficulty Levels**: Questions available in Beginner, Medium, and Pro levels
- **Real-time Data**: Fetches questions from backend API
- **Navigation**: Navigate between questions with next/previous buttons
- **Search**: Jump to specific question numbers
- **Difficulty Switching**: Switch between difficulty levels within a topic
- **Offline Fallback**: Graceful handling of network issues

## Setup Instructions

### Backend Setup

1. **Start the backend server**:
   ```bash
   cd backend
   npm install
   npm start
   ```
   
   The server will run on port 3000.

2. **Verify backend is running**:
   - Health check: http://localhost:3000/api/health
   - Topics: http://localhost:3000/api/topics
   - Questions: http://localhost:3000/api/questions/{topic}/{difficulty}

### Android App Setup

1. **Open the project** in Android Studio
2. **Build the project**: `./gradlew assembleDebug`
3. **Run on emulator** or device

### Network Configuration

The app is configured to connect to the backend at `http://10.0.2.2:3000` (Android emulator localhost).

For physical devices, update the BASE_URL in `ApiClient.kt`:
```kotlin
private const val BASE_URL = "http://YOUR_LOCAL_IP:3000/"
```

## API Endpoints

- `GET /api/topics` - Get all available topics
- `GET /api/topics/:topicId` - Get specific topic details
- `GET /api/questions/:topicId/:difficulty` - Get questions for a topic and difficulty

## App Flow

1. **MainActivity**: Displays topic cards fetched from backend
2. **TopicActivity**: Shows difficulty selection for selected topic
3. **QuestionActivity**: Displays questions with navigation and difficulty switching

## Data Models

- **Topic**: id, name, color, icon
- **Question**: id, text, textSize, explanation
- **API Response**: success, data, user

## Error Handling

- Network connectivity checks
- API failure fallbacks
- Offline mode with cached data
- User-friendly error messages

## Dependencies

- Retrofit 2.9.0 - HTTP client
- Gson 2.10.1 - JSON parsing
- AndroidX components
- Firebase (for future authentication)

## Troubleshooting

1. **Backend not accessible**: Check if server is running on port 3000
2. **Network errors**: Verify network security config allows cleartext traffic
3. **Build errors**: Ensure all dependencies are properly synced
4. **Emulator issues**: Use `10.0.2.2` for localhost access

## Development Notes

- Authentication is currently bypassed for testing
- Uses public API endpoints
- Network security config allows HTTP traffic for development
- Responsive design with proper error handling
# cloudapp
