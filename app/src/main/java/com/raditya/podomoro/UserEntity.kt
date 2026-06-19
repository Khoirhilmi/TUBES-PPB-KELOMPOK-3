package com.raditya.podomoro

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val kataSandi: String,
    val namaLengkap: String = "Rudi"
)
