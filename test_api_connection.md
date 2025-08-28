# API Connection Test Guide

## Backend Status Check ✅

The backend is running successfully on port 3000 and responding to API calls:

- **Health Check**: ✅ `http://localhost:3000/api/health`
- **Topics API**: ✅ `http://localhost:3000/api/topics` 
- **Questions API**: ✅ `http://localhost:3000/api/questions/docker/beginner`

## Android App Integration Status ✅

The Android app has been successfully configured to:

1. **API Client Setup**: ✅ `ApiClient.kt` configured with Retrofit
2. **Data Models**: ✅ Updated to match backend response format
3. **API Service**: ✅ `CloudInterviewApiService.kt` with proper endpoints
4. **MainActivity**: ✅ Fetches topics from backend on startup
5. **QuestionActivity**: ✅ Fetches questions from backend with navigation
6. **Network Security**: ✅ Configured to allow localhost connections
7. **Error Handling**: ✅ Graceful fallbacks for network issues

## Testing the App

### Prerequisites
1. Backend server running on port 3000
2. Android emulator or device with internet access
3. App built and installed

### Test Steps

1. **Launch the app** - Should show topic cards fetched from backend
2. **Select a topic** (e.g., Docker) - Should navigate to difficulty selection
3. **Select difficulty** (e.g., Beginner) - Should load questions from backend
4. **Navigate questions** - Use next/previous buttons
5. **Switch difficulties** - Change between Beginner/Medium/Pro
6. **Search questions** - Use the search input to jump to specific questions

### Expected Behavior

- **MainActivity**: Displays topic cards with names, colors, and icons
- **TopicActivity**: Shows difficulty selection buttons
- **QuestionActivity**: Displays questions with explanations and navigation
- **Network Issues**: Shows appropriate error messages and fallback content

### Troubleshooting

If the app doesn't load data:

1. **Check backend**: Ensure server is running on port 3000
2. **Check network**: Verify emulator can reach `10.0.2.2:3000`
3. **Check logs**: Look for API call errors in Android Studio logs
4. **Check permissions**: Ensure INTERNET permission is granted

## API Endpoints Used

- `GET /api/topics` - Loads topic list in MainActivity
- `GET /api/questions/{topic}/{difficulty}` - Loads questions in QuestionActivity

## Data Flow

1. **App Startup** → MainActivity loads topics from `/api/topics`
2. **Topic Selection** → User taps topic card → TopicActivity opens
3. **Difficulty Selection** → User selects difficulty → QuestionActivity opens
4. **Question Loading** → QuestionActivity fetches questions from `/api/questions/{topic}/{difficulty}`
5. **Question Display** → Questions shown with navigation and difficulty switching

## Success Indicators

- ✅ App displays topic cards on startup
- ✅ Tapping topics navigates to difficulty selection
- ✅ Selecting difficulty loads questions from backend
- ✅ Questions display with proper text and explanations
- ✅ Navigation between questions works
- ✅ Difficulty switching loads new questions
- ✅ Search functionality works for jumping to questions
- ✅ Error handling shows appropriate messages
- ✅ Offline fallback works when network is unavailable
