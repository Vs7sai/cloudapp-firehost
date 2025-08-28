# Notification Features & Responsive Text Implementation

## Overview
This document describes the implementation of daily inspirational notifications and responsive text sizing based on screen dimensions in the Android app.

## Features Implemented

### 1. Daily Inspirational Notifications
- **3 notifications per day** at scheduled times:
  - 🌅 **Morning (8:00 AM)**: Start your day with motivation
  - ☀️ **Afternoon (2:00 PM)**: Midday boost
  - 🌙 **Evening (8:00 PM)**: Evening reflection

- **Automatic scheduling**: Notifications are automatically scheduled when the app is first opened
- **Rich content**: Each notification includes an inspirational quote with author attribution
- **Interactive**: Tapping a notification opens the main app

### 2. Responsive Text Sizing
- **Screen size adaptation**: Text automatically adjusts based on device screen dimensions
- **Density awareness**: Considers device pixel density for optimal text sizing
- **Smart categorization**: 
  - **Headings**: Longer text (>20 characters) gets larger sizing
  - **Body**: Medium text (20-50 characters) gets medium sizing  
  - **Captions**: Short text (<20 characters) gets smaller sizing

## Technical Implementation

### Files Created/Modified

#### New Files:
- `app/src/main/java/com/example/m/services/NotificationService.kt` - Handles sending notifications
- `app/src/main/java/com/example/m/receivers/NotificationAlarmReceiver.kt` - Receives scheduled alarms
- `app/src/main/java/com/example/m/utils/QuoteManager.kt` - Manages inspirational quotes
- `app/src/main/java/com/example/m/utils/NotificationScheduler.kt` - Schedules daily notifications
- `app/src/main/java/com/example/m/utils/ResponsiveTextUtils.kt` - Handles responsive text sizing
- `app/src/main/res/layout/notification_settings_dialog.xml` - Settings UI layout

#### Modified Files:
- `app/src/main/java/com/example/m/MainActivity.kt` - Integrated notification and responsive text features
- `app/src/main/AndroidManifest.xml` - Added notification permissions and service declarations

### Key Components

#### NotificationService
- Creates notification channels for Android 8+
- Sends rich notifications with quotes and author information
- Handles notification styling and interaction

#### NotificationScheduler
- Uses AlarmManager to schedule daily repeating notifications
- Handles time calculations and alarm management
- Provides methods to cancel or check notification status

#### ResponsiveTextUtils
- Calculates optimal text sizes based on screen dimensions
- Considers device density and screen size breakpoints
- Provides utility methods for text size conversion

#### QuoteManager
- Curated collection of 20+ inspirational quotes
- Random quote selection for variety
- Daily quote consistency using time-based seeding

## Usage

### For Users:
1. **First Launch**: App automatically requests notification permission and schedules daily notifications
2. **Test Notifications**: Use "Test Notification" button in Profile dialog to send immediate notifications
3. **Automatic**: Daily notifications will be sent automatically at scheduled times

### For Developers:
1. **Custom Quotes**: Add new quotes to `QuoteManager.kt`
2. **Timing**: Modify notification times in `NotificationScheduler.kt`
3. **Text Sizing**: Adjust responsive text parameters in `ResponsiveTextUtils.kt`

## Permissions Required

- `POST_NOTIFICATIONS` - For sending notifications (Android 13+)
- `SCHEDULE_EXACT_ALARM` - For precise notification timing
- `USE_EXACT_ALARM` - For reliable alarm scheduling

## Testing

### Notification Testing:
1. Open Profile dialog (tap profile icon)
2. Tap "Test Notification" button
3. Verify notification appears with quote and author

### Responsive Text Testing:
1. Test on different screen sizes
2. Verify text automatically adjusts
3. Check text readability on various devices

## Future Enhancements

- **Customizable timing**: Allow users to set preferred notification times
- **Quote categories**: Organize quotes by theme (motivation, success, wisdom)
- **User preferences**: Save notification settings and quote preferences
- **Rich media**: Add images or animations to notifications
- **Social sharing**: Allow users to share favorite quotes

## Troubleshooting

### Common Issues:
1. **Notifications not appearing**: Check notification permissions in device settings
2. **Wrong timing**: Verify device timezone and time settings
3. **Text too small/large**: Check device display settings and density

### Debug Information:
- Check logcat for "NotificationScheduler" and "NotificationAlarmReceiver" tags
- Verify notification channel creation in device notification settings
- Test with immediate notifications using the test button
