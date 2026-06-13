package com.raditya.podomoro

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email AND kataSandi = :password LIMIT 1")
    fun loginUser(email: String, password: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun daftarUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDefaultUser(user: UserEntity)

    @Query("UPDATE users SET kataSandi = :sandiBaru WHERE email = :email")
    fun ubahKataSandi(email: String, sandiBaru: String): Int
}