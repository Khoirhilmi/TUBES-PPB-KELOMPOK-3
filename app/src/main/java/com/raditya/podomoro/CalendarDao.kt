package com.raditya.podomoro

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CalendarDao {
    @Query("SELECT * FROM calendar_events WHERE date = :date ORDER BY time ASC")
    suspend fun getEventsByDate(date: String): List<CalendarEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEvent)

    @Query("DELETE FROM calendar_events WHERE id = :id")
    suspend fun deleteEvent(id: Int)
}