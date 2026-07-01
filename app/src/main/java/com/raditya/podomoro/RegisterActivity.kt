package com.raditya.podomoro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register) // Pastikan nama layoutnya benar

        // 1. Kenalkan elemen UI (Sesuaikan ID-nya dengan yang ada di XML kamu)
        val etNamaLengkap = findViewById<EditText>(R.id.etName) // Misal ID-nya ini
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnDaftarSekarang = findViewById<Button>(R.id.btnRegister)

        // 2. Siapkan Database Room
        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        // 3. Logika saat tombol Daftar diklik
        btnDaftarSekarang.setOnClickListener {
            val nama = etNamaLengkap.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validasi: Cek apakah ada form yang kosong
            if (nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua data harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi: Email harus pakai @ (opsional tapi bagus)
            if (!email.contains("@")) {
                Toast.makeText(this, "Format email tidak valid!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mulai simpan ke Database (harus di background thread)
            lifecycleScope.launch(Dispatchers.IO) {
                // Bungkus data yang diisi ke dalam UserEntity
                val newUser = UserEntity(
                    email = email,
                    kataSandi = password,
                    namaLengkap = nama
                )

                // Simpan ke database menggunakan fungsi daftarUser
                val result = userDao.daftarUser(newUser)

                // Pindah kembali ke Main Thread untuk update UI
                withContext(Dispatchers.Main) {
                    if (result != -1L) {
                        // JIKA SUKSES: ID yang direturn bukan -1
                        Toast.makeText(this@RegisterActivity, "Akun berhasil dibuat! Silakan Login.", Toast.LENGTH_SHORT).show()

                        // Lempar kembali ke halaman Login
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        // Membersihkan tumpukan halaman agar tidak bisa di-back ke halaman daftar lagi
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish() // Tutup halaman register
                    } else {
                        // JIKA GAGAL: Biasanya karena Email sudah pernah dipakai (Primary Key bentrok)
                        Toast.makeText(this@RegisterActivity, "Pendaftaran Gagal. Email mungkin sudah terdaftar!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}