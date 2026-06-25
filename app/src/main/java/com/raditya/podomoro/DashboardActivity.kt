package com.raditya.podomoro

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class DashboardActivity : AppCompatActivity() {

    // Deklarasi variabel komponen menu
    private lateinit var ivToday: ImageView
    private lateinit var tvToday: TextView
    private lateinit var ivCalendar: ImageView
    private lateinit var tvCalendar: TextView
    private lateinit var ivTimer: ImageView
    private lateinit var tvTimer: TextView
    private lateinit var ivTasks: ImageView
    private lateinit var tvTasks: TextView
    private lateinit var ivProfile: ImageView
    private lateinit var tvProfile: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        val mainLayout = findViewById<LinearLayout>(R.id.bottomNavigation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        // Menghubungkan ID Gambar dan Teks
        ivToday = findViewById(R.id.ivToday)
        tvToday = findViewById(R.id.tvToday)
        ivCalendar = findViewById(R.id.ivCalendar)
        tvCalendar = findViewById(R.id.tvCalendar)
        ivTimer = findViewById(R.id.ivTimer)
        tvTimer = findViewById(R.id.tvTimer)
        ivTasks = findViewById(R.id.ivTasks)
        tvTasks = findViewById(R.id.tvTasks)
        ivProfile = findViewById(R.id.ivProfile)
        tvProfile = findViewById(R.id.tvProfile)

        // Deklarasi tombol wadah menu
        val menuToday = findViewById<LinearLayout>(R.id.menu_today)
        val menuCalendar = findViewById<LinearLayout>(R.id.menu_calendar)
        val menuTimer = findViewById<LinearLayout>(R.id.menu_timer)
        val menuTasks = findViewById<LinearLayout>(R.id.menu_tasks)
        val menuProfile = findViewById<LinearLayout>(R.id.menu_profile)


        // Tampilkan HomeFragment saat pertama kali buka dan warnai Today
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            updateNavColors("TODAY")
        }

        // INTEGRASI NAVIGASI
        menuToday.setOnClickListener {
            replaceFragment(HomeFragment())
            updateNavColors("TODAY")
        }
        menuCalendar.setOnClickListener {
            replaceFragment(CalendarFragment())
            updateNavColors("CALENDAR")
        }
        menuTimer.setOnClickListener {
            replaceFragment(TimerFragment())
            updateNavColors("TIMER")
        }
        menuTasks.setOnClickListener {
            replaceFragment(TaskFragment())
            updateNavColors("TASKS")
        }
        menuProfile.setOnClickListener {
            replaceFragment(ProfileFragment())
            updateNavColors("PROFILE")
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun updateNavColors(activeMenu: String) {
        val colorGrey = ContextCompat.getColor(this, R.color.text_grey)
        val colorActive = ContextCompat.getColor(this, R.color.accent_purple)

        // Reset all
        listOf(ivToday, ivCalendar, ivTimer, ivTasks, ivProfile).forEach { it.setColorFilter(colorGrey) }
        listOf(tvToday, tvCalendar, tvTimer, tvTasks, tvProfile).forEach { it.setTextColor(colorGrey) }

        // Set active
        when (activeMenu) {
            "TODAY" -> {
                ivToday.setColorFilter(colorActive)
                tvToday.setTextColor(colorActive)
            }
            "CALENDAR" -> {
                ivCalendar.setColorFilter(colorActive)
                tvCalendar.setTextColor(colorActive)
            }
            "TIMER" -> {
                ivTimer.setColorFilter(colorActive)
                tvTimer.setTextColor(colorActive)
            }
            "TASKS" -> {
                ivTasks.setColorFilter(colorActive)
                tvTasks.setTextColor(colorActive)
            }
            "PROFILE" -> {
                ivProfile.setColorFilter(colorActive)
                tvProfile.setTextColor(colorActive)
            }
        }
    }
}