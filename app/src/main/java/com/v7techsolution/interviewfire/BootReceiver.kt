package com.v7techsolution.interviewfire

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "BootReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Device boot completed, rescheduling notifications")
            
            try {
                val notificationHelper = NotificationHelper(context)
                notificationHelper.scheduleAllDailyNotifications()
                Log.d(TAG, "Notifications rescheduled successfully after boot")
            } catch (e: Exception) {
                Log.e(TAG, "Error rescheduling notifications after boot", e)
            }
        }
    }
}
