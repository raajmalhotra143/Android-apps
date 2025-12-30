package com.aiclone.app.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NotificationListenerService : NotificationListenerService() {
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    companion object {
        private const val TAG = "AICloneNotificationListener"
        
        // Supported messaging apps
        private val SUPPORTED_PACKAGES = setOf(
            "com.whatsapp",
            "com.instagram.android",
            "com.snapchat.android",
            "com.google.android.gm" // Gmail
        )
    }
    
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        
        sbn?.let { notification ->
            val packageName = notification.packageName
            
            // Only process notifications from supported apps
            if (packageName in SUPPORTED_PACKAGES) {
                processNotification(notification)
            }
        }
    }
    
    private fun processNotification(sbn: StatusBarNotification) {
        serviceScope.launch {
            try {
                val extras = sbn.notification.extras
                val title = extras.getString("android.title") ?: ""
                val text = extras.getCharSequence("android.text")?.toString() ?: ""
                
                if (text.isNotBlank()) {
                    Log.d(TAG, "New message from $title: $text")
                    
                    // TODO: Generate AI response and store in database
                    // This would integrate with MessageRepository to:
                    // 1. Get user's chat history
                    // 2. Generate AI response
                    // 3. Store suggestion for user to review
                    
                    // For now, just log the notification
                    Log.d(TAG, "Platform: ${getPlatformName(sbn.packageName)}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing notification", e)
            }
        }
    }
    
    private fun getPlatformName(packageName: String): String {
        return when (packageName) {
            "com.whatsapp" -> "WhatsApp"
            "com.instagram.android" -> "Instagram"
            "com.snapchat.android" -> "Snapchat"
            "com.google.android.gm" -> "Gmail"
            else -> "Unknown"
        }
    }
    
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        // Handle notification removal if needed
    }
}
