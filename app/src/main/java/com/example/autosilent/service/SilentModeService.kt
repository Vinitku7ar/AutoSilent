package com.example.autosilent.service



import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.autosilent.R
import com.example.autosilent.ui.MainActivity
import com.example.autosilent.util.AudioManagerHelper

class SilentModeService : Service() {

    companion object {
        const val ACTION_ENABLE_SILENT = "com.example.autosilent.ENABLE_SILENT"
        const val ACTION_DISABLE_SILENT = "com.example.autosilent.DISABLE_SILENT"

        private const val CHANNEL_ID = "silent_mode_channel"
        private const val NOTIFICATION_ID = 1001
    }

    private lateinit var audioManagerHelper: AudioManagerHelper

    override fun onCreate() {
        super.onCreate()
        audioManagerHelper = AudioManagerHelper(applicationContext)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_ENABLE_SILENT -> {
                audioManagerHelper.enableSilent()
                startForeground(NOTIFICATION_ID, buildNotification("🔕 Silent mode active"))
            }
            ACTION_DISABLE_SILENT -> {
                audioManagerHelper.restoreNormal()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun buildNotification(text: String): android.app.Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Auto Silent")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_lock_silent_mode)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Silent Mode Status",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}