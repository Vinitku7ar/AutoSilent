package com.example.autosilent.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.autosilent.receiver.TimeRuleReceiver
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val ACTION_START_SILENT = "com.example.autosilent.START_SILENT"
        const val ACTION_END_SILENT = "com.example.autosilent.END_SILENT"
        const val EXTRA_RULE_ID = "extra_rule_id"
    }
    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }
    fun scheduleRule(ruleId: Long, startTimeMinutes: Int, endTimeMinutes: Int) {
        scheduleAlarm(ruleId, startTimeMinutes, ACTION_START_SILENT, requestCode = (ruleId * 2).toInt())
        scheduleAlarm(ruleId, endTimeMinutes, ACTION_END_SILENT, requestCode = (ruleId * 2 + 1).toInt())
    }

    private fun scheduleAlarm(ruleId: Long, timeMinutes: Int, action: String, requestCode: Int) {
        val triggerTime = nextTriggerTimeMillis(timeMinutes)

        val intent = Intent(context, TimeRuleReceiver::class.java).apply {
            this.action = action
            putExtra(EXTRA_RULE_ID, ruleId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    // Converts "540 minutes since midnight" into the next actual timestamp (today or tomorrow)
    private fun nextTriggerTimeMillis(timeMinutes: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, timeMinutes / 60)
            set(Calendar.MINUTE, timeMinutes % 60)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1) // if time already passed today, schedule for tomorrow
        }

        return calendar.timeInMillis
    }
}