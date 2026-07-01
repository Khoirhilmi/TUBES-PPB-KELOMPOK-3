package com.raditya.podomoro

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
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

class CalendarFragment : Fragment() {

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var rvEvents: RecyclerView
    private lateinit var tvMonthDate: TextView
    private lateinit var tvSelectedDateDisplay: TextView
    
    // Array untuk menampung TextView tanggal 0-6
    private val tvDays = arrayOfNulls<TextView>(7)
    private val llDays = arrayOfNulls<View>(7)
    
    private var selectedDate = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val monthYearFormatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private val fullDateFormatter = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hubungkan elemen UI
        rvEvents = view.findViewById(R.id.rvEvents)
        tvMonthDate = view.findViewById(R.id.tvMonthDate)
        tvSelectedDateDisplay = view.findViewById(R.id.tvSelectedDateDisplay)
        
        setupDayBar(view)

        val btnPrevDay = view.findViewById<CardView>(R.id.btnPrevDay)
        val btnNextDay = view.findViewById<CardView>(R.id.btnNextDay)
        val fabAddEvent = view.findViewById<CardView>(R.id.fabAddEvent)

        // Setup RecyclerView
        calendarAdapter = CalendarAdapter(emptyList())
        rvEvents.layoutManager = LinearLayoutManager(requireContext())
        rvEvents.adapter = calendarAdapter

        // Tambahkan data sampel jika database masih kosong
        checkAndInsertSampleData()

        updateDateDisplay()

        // Navigasi tanggal
        btnPrevDay.setOnClickListener {
            selectedDate.add(Calendar.DAY_OF_YEAR, -1)
            updateDateDisplay()
        }
        btnNextDay.setOnClickListener {
            selectedDate.add(Calendar.DAY_OF_YEAR, 1)
            updateDateDisplay()
        }

        fabAddEvent.setOnClickListener {
            showAddEventDialog()
        }
    }

    private fun setupDayBar(view: View) {
        // Inisialisasi manual agar lebih aman daripada getIdentifier
        tvDays[0] = view.findViewById(R.id.tvDay0)
        tvDays[1] = view.findViewById(R.id.tvDay1)
        tvDays[2] = view.findViewById(R.id.tvDay2)
        tvDays[3] = view.findViewById(R.id.tvDay3)
        tvDays[4] = view.findViewById(R.id.tvDay4)
        tvDays[5] = view.findViewById(R.id.tvDay5)
        tvDays[6] = view.findViewById(R.id.tvDay6)

        llDays[0] = view.findViewById(R.id.llDay0)
        llDays[1] = view.findViewById(R.id.llDay1)
        llDays[2] = view.findViewById(R.id.llDay2)
        llDays[3] = view.findViewById(R.id.llDay3)
        llDays[4] = view.findViewById(R.id.llDay4)
        llDays[5] = view.findViewById(R.id.llDay5)
        llDays[6] = view.findViewById(R.id.llDay6)

        for (i in 0..6) {
            llDays[i]?.setOnClickListener {
                val weekCalendar = selectedDate.clone() as Calendar
                weekCalendar.set(Calendar.DAY_OF_WEEK, i + 1)
                selectedDate = weekCalendar
                updateDateDisplay()
            }
        }
    }

    private fun updateDateDisplay() {
        tvMonthDate.text = monthYearFormatter.format(selectedDate.time)
        tvSelectedDateDisplay.text = fullDateFormatter.format(selectedDate.time)
        
        updateDaysNumbers()
        loadEventsFromDatabase()
    }

    private fun updateDaysNumbers() {
        val weekCalendar = selectedDate.clone() as Calendar
        weekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        for (i in 0..6) {
            val dayNum = weekCalendar.get(Calendar.DAY_OF_MONTH)
            tvDays[i]?.text = dayNum.toString()
            
            if (isSameDay(weekCalendar, selectedDate)) {
                tvDays[i]?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                llDays[i]?.setBackgroundResource(R.drawable.bg_selected_date)
            } else {
                tvDays[i]?.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                llDays[i]?.setBackgroundColor(0)
            }
            weekCalendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun loadEventsFromDatabase() {
        val dateString = dateFormatter.format(selectedDate.time)
        val db = AppDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            val events = db.calendarDao().getEventsByDate(dateString)
            calendarAdapter.updateEvents(events)
        }
    }

    private fun checkAndInsertSampleData() {
        val db = AppDatabase.getDatabase(requireContext())
        val today = dateFormatter.format(Date())
        
        lifecycleScope.launch(Dispatchers.IO) {
            val currentEvents = db.calendarDao().getEventsByDate(today)
            if (currentEvents.isEmpty()) {
                db.calendarDao().insertEvent(CalendarEvent(title = "Kalkulus Lanjut", location = "Gedung MIPA, 302", type = "Kuliah", time = "08:00", date = today))
                db.calendarDao().insertEvent(CalendarEvent(title = "Kerja Kelompok", location = "Perpustakaan", type = "Diskusi", time = "13:00", date = today))
                withContext(Dispatchers.Main) { loadEventsFromDatabase() }
            }
        }
    }

    private fun showAddEventDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_event, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etEventTitle)
        val etLocation = dialogView.findViewById<EditText>(R.id.etEventLocation)
        val etTime = dialogView.findViewById<EditText>(R.id.etEventTime)
        val spType = dialogView.findViewById<Spinner>(R.id.spEventType)

        etTime.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, hour, minute ->
                etTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute))
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val title = etTitle.text.toString()
                val location = etLocation.text.toString()
                val time = etTime.text.toString()
                val type = spType.selectedItem.toString()
                val date = dateFormatter.format(selectedDate.time)

                if (title.isNotEmpty() && time.isNotEmpty()) {
                    saveEventToDatabase(CalendarEvent(title = title, location = location, type = type, time = time, date = date))
                } else {
                    Toast.makeText(requireContext(), "Harap isi Judul dan Jam", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null).show()
    }

    private fun saveEventToDatabase(event: CalendarEvent) {
        val db = AppDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            withContext(Dispatchers.IO) { db.calendarDao().insertEvent(event) }
            loadEventsFromDatabase()
        }
    }
}