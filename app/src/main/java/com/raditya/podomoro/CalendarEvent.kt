package com.raditya.podomoro

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_events")
data class CalendarEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val location: String,
    val type: String, // Kuliah, Diskusi, Mandiri
    val time: String, // Contoh: "08:00"
    val date: String  // Format: "YYYY-MM-DD"
)