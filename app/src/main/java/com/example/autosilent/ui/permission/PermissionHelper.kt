package com.example.autosilent.ui.permission


import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings

object PermissionHelper {

    fun hasDndAccess(context: Context): Boolean {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.isNotificationPolicyAccessGranted
    }

    fun requestDndAccess(context: Context) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        context.startActivity(intent)
    }
}