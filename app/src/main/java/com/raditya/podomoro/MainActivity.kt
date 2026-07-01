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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupPasswordToggle()
        setupNavigation()
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

    // PERBAIKAN: Membungkus kembali kode navigasi ke dalam fungsinya yang benar
    private fun setupNavigation() {
        val btnMasuk = findViewById<Button>(R.id.btnMasuk)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        btnMasuk.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Cek apakah ada kolom yang dibiarkan kosong
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Kata Sandi tidak boleh kosong!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Panggil database
            val db = AppDatabase.getDatabase(this)
            val userDao = db.userDao()

            lifecycleScope.launch(Dispatchers.IO) {
                // Mencari data user di database
                val user = userDao.loginUser(email, password)

                withContext(Dispatchers.Main) {
                    if (user != null) {
                        // JIKA COCOK: Pindah ke Dashboard dan bawa data aslinya
                        Toast.makeText(
                            this@MainActivity,
                            "Selamat datang, ${user.namaLengkap}!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@MainActivity, DashboardActivity::class.java)

                        // DI SINI POSISI YANG BENAR UNTUK MENGIRIM DATA
                        intent.putExtra("USER_NAME", user.namaLengkap)
                        intent.putExtra("USER_EMAIL", user.email)

                        startActivity(intent)
                        finish()
                    } else {
                        // JIKA TIDAK COCOK
                        Toast.makeText(
                            this@MainActivity,
                            "Akun tidak ditemukan atau sandi salah!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

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