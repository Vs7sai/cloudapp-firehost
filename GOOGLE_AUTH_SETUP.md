# Google Authentication Setup Guide

This guide will help you set up Google Sign-In for your Android app.

## Prerequisites

1. **Google Cloud Console Project**: You need a Google Cloud Console project
2. **Google Services JSON File**: The `google-services.json` file you mentioned
3. **Android Studio**: Latest version recommended

## Step 1: Configure Google Cloud Console

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google Sign-In API:
   - Go to "APIs & Services" > "Library"
   - Search for "Google Sign-In API"
   - Click on it and press "Enable"

## Step 2: Configure OAuth Consent Screen

1. Go to "APIs & Services" > "OAuth consent screen"
2. Choose "External" user type
3. Fill in the required information:
   - App name: Your app name
   - User support email: Your email
   - Developer contact information: Your email
4. Add scopes:
   - `.../auth/userinfo.email`
   - `.../auth/userinfo.profile`
5. Add test users if needed
6. Save and continue

## Step 3: Create OAuth 2.0 Credentials

1. Go to "APIs & Services" > "Credentials"
2. Click "Create Credentials" > "OAuth 2.0 Client IDs"
3. Choose "Android" as application type
4. Fill in the details:
   - Package name: `com.example.m` (your app's package name)
   - SHA-1 certificate fingerprint: Your app's SHA-1 fingerprint
5. Click "Create"

## Step 4: Get SHA-1 Fingerprint

To get your app's SHA-1 fingerprint, run this command in your project directory:

```bash
# For debug builds
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# For release builds (if you have a release keystore)
keytool -list -v -keystore your-release-keystore.jks -alias your-key-alias
```

## Step 5: Download google-services.json

1. In Google Cloud Console, go to "Project Settings"
2. Click on "Cloud Messaging" tab
3. Download the `google-services.json` file
4. Place it in your app directory: `app/google-services.json`

## Step 6: Update Build Files

The build files have already been updated with the necessary dependencies:

- `app/build.gradle.kts` includes Google Play Services Auth
- Google Services plugin is configured

## Step 7: Test the Implementation

1. Build and run your app
2. You should see a "Sign in with Google" button at the top
3. Tap the button to initiate Google Sign-In
4. Choose your Google account
5. Grant permissions when prompted
6. You should see your name and email displayed

## Features Implemented

✅ **Google Sign-In Button**: Styled according to Google's design guidelines
✅ **User Authentication**: Handles sign-in flow and user data
✅ **User Profile Display**: Shows user name and email after sign-in
✅ **Sign Out Functionality**: Allows users to sign out
✅ **Error Handling**: Proper error messages for failed sign-ins
✅ **State Management**: Maintains authentication state across app sessions

## Code Structure

- **`GoogleAuthHelper.kt`**: Handles all Google authentication logic
- **`MainActivity.kt`**: Integrates authentication with the main UI
- **Layout files**: Updated to include sign-in button and user info
- **Drawables**: Custom Google Sign-In button styling
- **Strings**: Localized text for all authentication messages

## Troubleshooting

### Common Issues

1. **"Google Play services is not available"**
   - Ensure Google Play Services is installed on the device
   - Check that the device has internet connectivity

2. **"Sign-in failed"**
   - Verify your OAuth 2.0 credentials are correct
   - Check that the package name matches exactly
   - Ensure SHA-1 fingerprint is correct

3. **"App not verified"**
   - This is normal for development builds
   - Add test users in OAuth consent screen
   - Publish your app to remove this warning

### Debug Tips

- Check Logcat for detailed error messages
- Verify `google-services.json` is in the correct location
- Ensure all dependencies are properly synced

## Security Notes

- Never commit `google-services.json` to public repositories
- Use different OAuth credentials for debug and release builds
- Implement proper token validation in production apps
- Consider implementing additional security measures like token refresh

## Next Steps

After successful setup, you can:

1. **Customize the UI**: Modify colors, fonts, and layout
2. **Add More Scopes**: Request additional user permissions if needed
3. **Implement Backend Integration**: Send authentication tokens to your server
4. **Add User Management**: Store user data locally or in your backend
5. **Implement Offline Support**: Handle authentication state when offline

## Support

If you encounter issues:

1. Check the [Google Sign-In documentation](https://developers.google.com/identity/sign-in/android)
2. Verify your Google Cloud Console configuration
3. Check that all dependencies are properly configured
4. Ensure your device/emulator has Google Play Services

---

**Note**: This implementation provides basic Google Sign-In functionality. For production apps, consider implementing additional security measures and error handling.
