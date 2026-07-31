package com.example.autosilent.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.autosilent.data.local.SilentRule
import com.example.autosilent.data.repository.RuleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: RuleRepository,
    private val alarmScheduler: com.example.autosilent.util.AlarmScheduler
) : ViewModel() {

    private val _statusMessage = MutableStateFlow("No rule set yet")
    val statusMessage: StateFlow<String> = _statusMessage

    fun saveRule(startTimeMinutes: Int, endTimeMinutes: Int) {
        viewModelScope.launch {
            val rule = SilentRule(
                startTimeMinutes = startTimeMinutes,
                endTimeMinutes = endTimeMinutes
            )
            val id = repository.insertRule(rule)
            alarmScheduler.scheduleRule(id, startTimeMinutes, endTimeMinutes)

            _statusMessage.value = "✅ Rule saved: Silent from " +
                    "${formatTime(startTimeMinutes)} to ${formatTime(endTimeMinutes)}"
        }
    }

    private fun formatTime(minutes: Int): String {
        val h = minutes / 60
        val m = minutes % 60
        return String.format("%02d:%02d", h, m)
    }

    class Factory(
        private val repository: RuleRepository,
        private val alarmScheduler: com.example.autosilent.util.AlarmScheduler
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, alarmScheduler) as T
        }
    }
}