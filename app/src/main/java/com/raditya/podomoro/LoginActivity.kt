package com.raditya.podomoro

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope // Pastikan ini di-import
import com.raditya.podomoro.AppDatabase // Sesuaikan dengan lokasi package database kamu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupPasswordToggle()
        setupNavigation()
        setupLoginAction() // Logika login database ada di sini
    }

    private fun setupPasswordToggle() {
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val ivPasswordToggle = findViewById<ImageView>(R.id.ivPasswordToggle)

        ivPasswordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                ivPasswordToggle.setImageResource(R.drawable.ic_visibility)
            } else {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                ivPasswordToggle.setImageResource(R.drawable.ic_visibility_off)
            }
            etPassword.setSelection(etPassword.text.length)
        }
    }

    private fun setupLoginAction() {
        val btnMasuk = findViewById<Button>(R.id.btnMasuk)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword) // Tambahan: Ambil view password

        // Inisialisasi Database Room
        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        btnMasuk.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // 1. Validasi jika inputan kosong
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Kata Sandi tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Jalankan Coroutine untuk cek database di background thread
            lifecycleScope.launch(Dispatchers.IO) {
                // Mencari user di database berdasarkan email dan password
                val user = userDao.loginUser(email, password)

                // 3. Kembali ke Main Thread untuk update UI/Pindah halaman
                withContext(Dispatchers.Main) {
                    if (user != null) {
                        // Jika berhasil: Berpindah ke DashboardActivity
                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java).apply {
                            // Mengirim nama dari database (bukan lagi potongan email)
                            putExtra("USER_NAME", user.namaLengkap)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        // Jika gagal: Tampilkan pesan error
                        Toast.makeText(this@LoginActivity, "Email atau Kata Sandi salah!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupNavigation() {
        val tvNoAccount = findViewById<TextView>(R.id.tvNoAccount)
        tvNoAccount.text = Html.fromHtml(getString(R.string.no_account), Html.FROM_HTML_MODE_LEGACY)
        tvNoAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}