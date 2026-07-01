package com.raditya.podomoro

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var rvHomeSchedule: RecyclerView
    private lateinit var tvScheduleHeader: TextView
    
    private val tvDayNums = arrayOfNulls<TextView>(5)
    private val llDays = arrayOfNulls<LinearLayout>(5)
    
    private var selectedDate = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val headerFormatter = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Greeting & Quote
        val name = activity?.intent?.getStringExtra("USER_NAME") ?: "Budi"
        view.findViewById<TextView>(R.id.tvGreeting).text = getString(R.string.greeting_format, name)
        fetchDailyQuote(view)

        // 2. Inisialisasi UI Tanggal & Jadwal
        rvHomeSchedule = view.findViewById(R.id.rvHomeSchedule)
        tvScheduleHeader = view.findViewById(R.id.tvScheduleHeader)
        setupDateBar(view)
        setupRecyclerView()
        
        // Cek data sampel dulu sebelum update UI
        checkAndInsertSampleData()
        updateHomeUI()

        // 3. Navigasi
        view.findViewById<TextView>(R.id.tvSeeAll).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/calendar")))
        }
        view.findViewById<ImageView>(R.id.ivNotification).setOnClickListener {
            startActivity(Intent(requireActivity(), NotificationActivity::class.java))
        }
    }

    private fun setupDateBar(view: View) {
        for (i in 0..4) {
            val llId = resources.getIdentifier("llHomeDay$i", "id", requireContext().packageName)
            val tvId = resources.getIdentifier("tvHomeDayNum$i", "id", requireContext().packageName)
            
            llDays[i] = view.findViewById(llId)
            tvDayNums[i] = view.findViewById(tvId)

            llDays[i]?.setOnClickListener {
                val weekCal = Calendar.getInstance()
                weekCal.set(Calendar.DAY_OF_WEEK, i + 2) // Monday to Friday
                selectedDate = weekCal
                updateHomeUI()
            }
        }
    }

    private fun setupRecyclerView() {
        calendarAdapter = CalendarAdapter(emptyList())
        rvHomeSchedule.layoutManager = LinearLayoutManager(requireContext())
        rvHomeSchedule.adapter = calendarAdapter
    }

    private fun checkAndInsertSampleData() {
        val db = AppDatabase.getDatabase(requireContext())
        val today = dateFormatter.format(Date())
        
        lifecycleScope.launch(Dispatchers.IO) {
            val currentEvents = db.calendarDao().getEventsByDate(today)
            if (currentEvents.isEmpty()) {
                db.calendarDao().insertEvent(CalendarEvent(title = "Kalkulus Lanjut", location = "Gedung MIPA, 302", type = "Kuliah", time = "08:00", date = today))
                db.calendarDao().insertEvent(CalendarEvent(title = "Kerja Kelompok", location = "Perpustakaan", type = "Diskusi", time = "13:00", date = today))
                withContext(Dispatchers.Main) { loadEvents() }
            }
        }
    }

    private fun updateHomeUI() {
        val weekCal = Calendar.getInstance()
        weekCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        for (i in 0..4) {
            tvDayNums[i]?.text = weekCal.get(Calendar.DAY_OF_MONTH).toString()
            if (isSameDay(weekCal, selectedDate)) {
                llDays[i]?.setBackgroundResource(R.drawable.bg_selected_date)
                tvDayNums[i]?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                llDays[i]?.setBackgroundColor(0)
                tvDayNums[i]?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
            weekCal.add(Calendar.DAY_OF_YEAR, 1)
        }

        tvScheduleHeader.text = "Jadwal ${headerFormatter.format(selectedDate.time)}"
        loadEvents()
    }

    private fun loadEvents() {
        val dateStr = dateFormatter.format(selectedDate.time)
        val db = AppDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            val events = db.calendarDao().getEventsByDate(dateStr)
            calendarAdapter.updateEvents(events)
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun fetchDailyQuote(view: View) {
        val tvQuote = view.findViewById<TextView>(R.id.tvQuote)
        val tvAuthor = view.findViewById<TextView>(R.id.tvAuthor)
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getQuoteOfDay()
                if (response.isNotEmpty()) {
                    tvQuote.text = "\"${response[0].text}\""
                    tvAuthor.text = "- ${response[0].author}"
                }
            } catch (e: Exception) {
                tvQuote.text = "Tetap semangat belajar hari ini!"
            }
        }
    }
}