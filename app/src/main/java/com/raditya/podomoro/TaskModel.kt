package com.raditya.podomoro

data class TaskModel(
    val prioritas: String,
    val mataKuliah: String,
    val judulTugas: String,
    val deskripsi: String,
    val tenggatWaktu: String,
    val warnaUtamaHex: String, // Untuk teks dan garis tepi (misal: merah, ungu, biru)
    val warnaBgHex: String     // Untuk latar belakang label (warna pudar)
)