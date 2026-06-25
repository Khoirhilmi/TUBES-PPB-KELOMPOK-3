package com.raditya.podomoro

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.util.Locale

class TimerFragment : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    private lateinit var notificationManager: NotificationManager

    private lateinit var tvCountdown: TextView
    private lateinit var tvTimerStatus: TextView
    private lateinit var etMinutes: EditText
    private lateinit var btnResetTimer: Button

    private var countDownTimer: CountDownTimer? = null
    private var initialTimeInMillis: Long = 25 * 60 * 1000 // Default 25 Menit
    private var timeLeftInMillis: Long = initialTimeInMillis
    private var isTimerRunning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvCountdown = view.findViewById(R.id.tvCountdown)
        tvTimerStatus = view.findViewById(R.id.tvTimerStatus)
        etMinutes = view.findViewById(R.id.etMinutes)
        btnResetTimer = view.findViewById(R.id.btnResetTimer)

        // Inisialisasi Sensor & Notification Manager
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        notificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (proximitySensor == null) {
            tvTimerStatus.text = getString(R.string.sensor_not_available)
        }

        // Listener untuk mengubah durasi secara real-time saat diketik
        etMinutes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isTimerRunning) {
                    val input = s.toString().toLongOrNull() ?: 0L
                    timeLeftInMillis = input * 60 * 1000
                    updateCountDownText()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnResetTimer.setOnClickListener {
            resetTimer()
        }

        updateCountDownText()
    }

    override fun onResume() {
        super.onResume()
        proximitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    // LOGIKA SENSOR: Terpanggil saat ada perubahan jarak
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values[0]
            val isNear = distance < (proximitySensor?.maximumRange ?: 0f)
            
            if (isNear) {
                // HP Facedown (Layar tertutup meja)
                if (!isTimerRunning && timeLeftInMillis > 0) {
                    startTimer()
                    enableDoNotDisturb(true)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun startTimer() {
        isTimerRunning = true
        tvTimerStatus.text = getString(R.string.timer_running)
        etMinutes.isEnabled = false // Kunci input saat berjalan
        
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                isTimerRunning = false
                etMinutes.isEnabled = true
                tvTimerStatus.text = getString(R.string.timer_finished)
                enableDoNotDisturb(false) // Kembalikan mode DND
                
                // Kirim Notifikasi Lokal saat selesai
                val intent = Intent(requireContext(), NotificationReceiver::class.java)
                requireContext().sendBroadcast(intent)
            }
        }.start()
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        etMinutes.isEnabled = true
        enableDoNotDisturb(false)
        
        val inputMinutes = etMinutes.text.toString().toLongOrNull() ?: 25L
        timeLeftInMillis = inputMinutes * 60 * 1000
        
        updateCountDownText()
        tvTimerStatus.text = getString(R.string.timer_ready)
    }

    private fun enableDoNotDisturb(enable: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationManager.isNotificationPolicyAccessGranted) {
                val filter = if (enable) NotificationManager.INTERRUPTION_FILTER_NONE else NotificationManager.INTERRUPTION_FILTER_ALL
                notificationManager.setInterruptionFilter(filter)
                if (enable) Toast.makeText(context, "Mode Fokus: Jangan Diganggu Aktif", Toast.LENGTH_SHORT).show()
            } else if (enable) {
                // Minta izin akses kebijakan notifikasi jika belum ada
                Toast.makeText(context, "Mohon izinkan akses Jangan Diganggu untuk fitur Flip to Focus", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                startActivity(intent)
            }
        }
    }

    private fun updateCountDownText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        tvCountdown.text = timeFormatted
    }
}