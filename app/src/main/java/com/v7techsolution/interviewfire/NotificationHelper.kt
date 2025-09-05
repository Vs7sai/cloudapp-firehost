package com.v7techsolution.interviewfire

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

class NotificationHelper(private val context: Context) {
    
    companion object {
        private const val TAG = "NotificationHelper"
        private const val CHANNEL_ID = "study_reminders"
        private const val CHANNEL_NAME = "Study Reminders"
        private const val CHANNEL_DESCRIPTION = "Daily reminders to practice interview questions"
        
        // Notification IDs for different times
        private const val MORNING_NOTIFICATION_ID = 1001
        private const val AFTERNOON_NOTIFICATION_ID = 1002
        private const val EVENING_NOTIFICATION_ID = 1003
        
        // Request codes for pending intents
        private const val MORNING_REQUEST_CODE = 2001
        private const val AFTERNOON_REQUEST_CODE = 2002
        private const val EVENING_REQUEST_CODE = 2003
    }
    
    private val notificationManager = NotificationManagerCompat.from(context)
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    // Study reminder messages
    private val morningMessages = listOf(
        "🌅 Good morning! Start your day with cloud interview prep!",
        "☀️ Rise and shine! Time to boost your cloud skills!",
        "🚀 Morning motivation: Practice makes perfect in cloud computing!",
        "💡 Start your day smart! Review AWS, Azure, and GCP concepts.",
        "🎯 Morning goal: Master 5 new interview questions today!"
    )
    
    private val afternoonMessages = listOf(
        "☕ Afternoon break? Perfect time for quick interview prep!",
        "🌤️ Midday momentum! Keep your cloud knowledge sharp!",
        "📚 Lunch break learning: Review Docker and Kubernetes!",
        "💪 Afternoon power-up: Practice DevOps interview questions!",
        "🎓 Quick study session: Terraform and infrastructure concepts!"
    )
    
    private val eveningMessages = listOf(
        "🌙 Evening study time! End your day with interview prep!",
        "✨ Night owl? Perfect time to review cloud architectures!",
        "🏆 Evening achievement: Complete today's practice questions!",
        "🔥 Finish strong! Practice Git and CI/CD questions tonight!",
        "💫 Dream of success! Study cloud security concepts!"
    )
    
    init {
        createNotificationChannel()
    }
    
    // Create notification channel (required for Android 8.0+)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                setShowBadge(true)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created: $CHANNEL_ID")
        }
    }
    
    // Schedule all daily notifications
    fun scheduleAllDailyNotifications() {
        scheduleMorningNotification()
        scheduleAfternoonNotification()
        scheduleEveningNotification()
        Log.d(TAG, "All daily notifications scheduled")
    }
    
    // Schedule morning notification (9:00 AM)
    private fun scheduleMorningNotification() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            
            // If time has passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        
        scheduleNotification(
            calendar.timeInMillis,
            MORNING_REQUEST_CODE,
            MORNING_NOTIFICATION_ID,
            "morning"
        )
        Log.d(TAG, "Morning notification scheduled for: ${calendar.time}")
    }
    
    // Schedule afternoon notification (2:00 PM)
    private fun scheduleAfternoonNotification() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            
            // If time has passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        
        scheduleNotification(
            calendar.timeInMillis,
            AFTERNOON_REQUEST_CODE,
            AFTERNOON_NOTIFICATION_ID,
            "afternoon"
        )
        Log.d(TAG, "Afternoon notification scheduled for: ${calendar.time}")
    }
    
    // Schedule evening notification (7:00 PM)
    private fun scheduleEveningNotification() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 19)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            
            // If time has passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        
        scheduleNotification(
            calendar.timeInMillis,
            EVENING_REQUEST_CODE,
            EVENING_NOTIFICATION_ID,
            "evening"
        )
        Log.d(TAG, "Evening notification scheduled for: ${calendar.time}")
    }
    
    // Schedule a specific notification
    private fun scheduleNotification(timeInMillis: Long, requestCode: Int, notificationId: Int, timeOfDay: String) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("notification_id", notificationId)
            putExtra("time_of_day", timeOfDay)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Schedule repeating daily notification
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
    
    // Cancel all notifications
    fun cancelAllNotifications() {
        cancelNotification(MORNING_REQUEST_CODE)
        cancelNotification(AFTERNOON_REQUEST_CODE)
        cancelNotification(EVENING_REQUEST_CODE)
        Log.d(TAG, "All notifications cancelled")
    }
    
    // Cancel specific notification
    private fun cancelNotification(requestCode: Int) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
    
    // Show notification immediately (for testing)
    fun showTestNotification() {
        showStudyNotification(MORNING_NOTIFICATION_ID, "morning")
    }
    
    // Show study reminder notification
    fun showStudyNotification(notificationId: Int, timeOfDay: String) {
        val message = when (timeOfDay) {
            "morning" -> morningMessages.random()
            "afternoon" -> afternoonMessages.random()
            "evening" -> eveningMessages.random()
            else -> "📚 Time to practice your interview skills!"
        }
        
        // Intent to open the app when notification is tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Cloud Interview Prep")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .build()
        
        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(notificationId, notification)
            Log.d(TAG, "Notification shown: $message")
        } else {
            Log.w(TAG, "Notifications are disabled")
        }
    }
}

// Broadcast receiver to handle scheduled notifications
class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notification_id", 0)
        val timeOfDay = intent.getStringExtra("time_of_day") ?: "morning"
        
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showStudyNotification(notificationId, timeOfDay)
        
        Log.d("NotificationReceiver", "Received notification trigger: $timeOfDay")
    }
}
