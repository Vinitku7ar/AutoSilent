package com.example.autosilent.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "silent_rules")
data class SilentRule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val startTimeMinutes: Int,   // minutes since midnight, e.g. 9:00 AM = 540
    val endTimeMinutes: Int,     // e.g. 5:00 PM = 1020

    val isEnabled: Boolean = true
)
