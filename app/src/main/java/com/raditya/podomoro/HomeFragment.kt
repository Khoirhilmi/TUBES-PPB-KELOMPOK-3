package com.raditya.podomoro

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

class HomeFragment : Fragment(), SensorEventListener {

    private lateinit var tvAdvice: TextView
    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(context, "Izin notifikasi ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH

        val name = activity?.intent?.getStringExtra("USER_NAME") ?: "Budi"
        val tvGreeting = view.findViewById<TextView>(R.id.tvGreeting)
        tvGreeting.text = getString(R.string.greeting_format, name)

        tvAdvice = view.findViewById(R.id.tvAdvice)
        fetchDailyAdvice()

        val tvSeeAll = view.findViewById<TextView>(R.id.tvSeeAll)
        tvSeeAll.setOnClickListener {
            val url = "https://www.google.com/calendar"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        val ivNotification = view.findViewById<ImageView>(R.id.ivNotification)
        ivNotification.setOnClickListener {
            val intent = Intent(requireActivity(), NotificationActivity::class.java)
            startActivity(intent)
        }

        val btnNotify = view.findViewById<Button>(R.id.btnNotify)
        btnNotify.setOnClickListener {
            checkAndSendNotification()
        }

        val btnStartMusic = view.findViewById<Button>(R.id.btnStartMusic)
        val btnStopMusic = view.findViewById<Button>(R.id.btnStopMusic)

        btnStartMusic.setOnClickListener {
            val serviceIntent = Intent(requireContext(), FocusMusicService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireContext().startForegroundService(serviceIntent)
            } else {
                requireContext().startService(serviceIntent)
            }
        }

        btnStopMusic.setOnClickListener {
            val serviceIntent = Intent(requireContext(), FocusMusicService::class.java)
            requireContext().stopService(serviceIntent)
        }

        val btnStartSync = view.findViewById<Button>(R.id.btnStartSync)
        btnStartSync.setOnClickListener {
            val syncIntent = Intent(requireContext(), DataSyncIntentService::class.java)
            requireContext().startService(syncIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(this, sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta

            if (acceleration > 12) {
                fetchDailyAdvice()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun checkAndSendNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                sendNotificationBroadcast()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            sendNotificationBroadcast()
        }
    }

    private fun sendNotificationBroadcast() {
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        requireContext().sendBroadcast(intent)
    }

    private fun fetchDailyAdvice() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getRandomAdvice()
                }
                if (response.isSuccessful && response.body() != null) {
                    val advice = response.body()?.slip?.advice
                    tvAdvice.text = "\"$advice\""
                }
            } catch (e: Exception) {
                tvAdvice.text = "Cek koneksi internet Anda."
            }
        }
    }
}
