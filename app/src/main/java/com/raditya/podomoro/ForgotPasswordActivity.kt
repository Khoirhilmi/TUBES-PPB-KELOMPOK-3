package com.raditya.podomoro

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val etEmailReset = findViewById<EditText>(R.id.etEmailReset)
        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val btnSimpanSandi = findViewById<Button>(R.id.btnSimpanSandi)

        btnSimpanSandi.setOnClickListener {
            val email = etEmailReset.text.toString().trim()
            val sandiBaru = etNewPassword.text.toString().trim()

            // Validasi kosong
            if (email.isEmpty() || sandiBaru.isEmpty()) {
                Toast.makeText(this, "Email dan Sandi Baru wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Panggil Database
            val db = AppDatabase.getDatabase(this)
            val userDao = db.userDao()

            lifecycleScope.launch(Dispatchers.IO) {
                // Eksekusi fungsi ubahKataSandi yang baru saja kita buat di UserDao
                val barisBerubah = userDao.ubahKataSandi(email, sandiBaru)

                withContext(Dispatchers.Main) {
                    if (barisBerubah > 0) {
                        // Jika hasilnya > 0, berarti email ada dan sukses diubah
                        Toast.makeText(this@ForgotPasswordActivity, "Sandi berhasil diubah! Silakan Login.", Toast.LENGTH_SHORT).show()
                        finish() // Menutup halaman ini dan kembali ke halaman Login (MainActivity)
                    } else {
                        // Jika hasilnya 0, berarti email yang diketik tidak terdaftar di database
                        Toast.makeText(this@ForgotPasswordActivity, "Email tidak terdaftar!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}