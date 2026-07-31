package com.example.autosilent.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.autosilent.data.local.AppDatabase
import com.example.autosilent.service.SilentModeService
import com.example.autosilent.util.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TimeRuleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            reScheduleRules(context)
            return
        }

        val ruleId = intent.getLongExtra(AlarmScheduler.EXTRA_RULE_ID, -1)
        Log.d("TimeRuleReceiver", "Received action: ${intent.action} for ruleId: $ruleId")

        val serviceIntent = Intent(context, SilentModeService::class.java)

        when (intent.action) {
            AlarmScheduler.ACTION_START_SILENT -> {
                serviceIntent.action = SilentModeService.ACTION_ENABLE_SILENT
            }
            AlarmScheduler.ACTION_END_SILENT -> {
                serviceIntent.action = SilentModeService.ACTION_DISABLE_SILENT
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    private fun reScheduleRules(context: Context) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(context)
                val rules = database.ruleDao().getAllRules().first()
                val alarmScheduler = AlarmScheduler(context)

                rules.filter { it.isEnabled }.forEach { rule ->
                    alarmScheduler.scheduleRule(rule.id, rule.startTimeMinutes, rule.endTimeMinutes)
                }
                Log.d("TimeRuleReceiver", "Re-scheduled ${rules.size} rules after reboot")
            } finally {
                pendingResult.finish()
            }
        }
    }
}