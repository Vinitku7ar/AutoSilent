package com.example.autosilent.ui

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.autosilent.R
import com.example.autosilent.data.local.AppDatabase
import com.example.autosilent.data.repository.RuleRepository
import com.example.autosilent.ui.viewmodels.MainViewModel
import com.example.autosilent.util.AlarmScheduler
import com.example.autosilent.ui.permission.PermissionHelper

import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var alarmScheduler: AlarmScheduler

    private var startTimeMinutes: Int? = null
    private var endTimeMinutes: Int? = null

    private lateinit var btnStartTime: TextView
    private lateinit var btnEndTime: TextView
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dao = AppDatabase.getDatabase(applicationContext).ruleDao()
        val repository = RuleRepository(dao)
        alarmScheduler = AlarmScheduler(applicationContext)

        viewModel = ViewModelProvider(
            this,
            MainViewModel.Factory(repository, alarmScheduler)
        )[MainViewModel::class.java]

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        btnStartTime = findViewById(R.id.btnStartTime)
        btnEndTime = findViewById(R.id.btnEndTime)
        tvStatus = findViewById(R.id.tvStatus)
        val btnEnable = findViewById<android.widget.Button>(R.id.btnEnable)

        btnStartTime.setOnClickListener {
            showTimePicker { hour, minute ->
                startTimeMinutes = hour * 60 + minute
                btnStartTime.text = String.format("%02d:%02d", hour, minute)
            }
        }

        btnEndTime.setOnClickListener {
            showTimePicker { hour, minute ->
                endTimeMinutes = hour * 60 + minute
                btnEndTime.text = String.format("%02d:%02d", hour, minute)
            }
        }

        btnEnable.setOnClickListener {
            val start = startTimeMinutes
            val end = endTimeMinutes

            if (start == null || end == null) {
                tvStatus.text = "⚠️ Please select both start and end time"
                return@setOnClickListener
            }

            if (!alarmScheduler.canScheduleExactAlarms()) {
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                tvStatus.text = "⚠️ Please allow 'Alarms & reminders', then tap Enable again"
                return@setOnClickListener
            }

            if (!PermissionHelper.hasDndAccess(applicationContext)) {
                PermissionHelper.requestDndAccess(applicationContext)
                tvStatus.text = "⚠️ Please allow 'Do Not Disturb access', then tap Enable again"
                return@setOnClickListener
            }

            viewModel.saveRule(start, end)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.statusMessage.collect { message ->
                tvStatus.text = message
            }
        }
    }

    private fun showTimePicker(onTimeSelected: (hour: Int, minute: Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            this,
            { _, hour, minute -> onTimeSelected(hour, minute) },
            currentHour,
            currentMinute,
            true
        ).show()
    }
}