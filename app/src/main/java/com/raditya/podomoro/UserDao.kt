package com.raditya.podomoro

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    // 1. Fungsi untuk cek login (Tanpa suspend)
    @Query("SELECT * FROM users WHERE email = :email AND kataSandi = :password LIMIT 1")
    fun loginUser(email: String, password: String): UserEntity?

    // 2. Fungsi untuk mendaftarkan akun baru (Tanpa suspend)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun daftarUser(user: UserEntity): Long

    // 3. Fungsi untuk akun default (Tanpa suspend)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDefaultUser(user: UserEntity)

    // 4. Fungsi untuk mengubah kata sandi berdasarkan email (Tanpa suspend)
    @Query("UPDATE users SET kataSandi = :sandiBaru WHERE email = :email")
    fun ubahKataSandi(email: String, sandiBaru: String): Int
}