package com.raditya.podomoro

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        
        val mainView = findViewById<View>(R.id.bottomNavigation).parent as View
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Menerima data dari Intent (Data antar-Activity)
        val name = intent.getStringExtra("USER_NAME") ?: "Budi"
        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)
        tvGreeting.text = getString(R.string.greeting_format, name)

        setupImplicitIntent()
    }

    private fun setupImplicitIntent() {
        // Contoh Implicit Intent: Membuka browser untuk "Lihat Semua" jadwal
        val tvSeeAll = findViewById<TextView>(R.id.tvSeeAll)
        tvSeeAll.setOnClickListener {
            val url = "https://www.google.com/calendar"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        // Contoh Implicit Intent: Share tugas
        val ivTasks = findViewById<ImageView>(R.id.ivTasks)
        ivTasks.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Ayo kerjakan tugas Esai Revolusi Industri di aplikasi Podomoro!")
            }
            startActivity(Intent.createChooser(shareIntent, "Bagikan tugas via"))
        }
    }
}