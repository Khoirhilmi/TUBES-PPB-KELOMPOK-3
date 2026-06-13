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
        setContentView(R.layout.activity_register)

        val etNamaLengkap = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnDaftarSekarang = findViewById<Button>(R.id.btnRegister)

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        btnDaftarSekarang.setOnClickListener {
            val nama = etNamaLengkap.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua data harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!email.contains("@")) {
                Toast.makeText(this, "Format email tidak valid!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val newUser = UserEntity(
                    email = email,
                    kataSandi = password,
                    namaLengkap = nama
                )

                val result = userDao.daftarUser(newUser)

                withContext(Dispatchers.Main) {
                    if (result != -1L) {
                        Toast.makeText(this@RegisterActivity, "Akun berhasil dibuat! Silakan Login.", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Pendaftaran Gagal. Email mungkin sudah terdaftar!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}