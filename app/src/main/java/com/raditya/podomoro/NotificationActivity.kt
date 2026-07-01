package com.raditya.podomoro

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class NotificationActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            sendTestBroadcast()
        } else {
            Toast.makeText(this, "Izin notifikasi ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // Tombol panah kembali (Back) di pojok kiri atas
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Menutup halaman notifikasi dan kembali ke Dashboard
        }

        // Tombol untuk menguji notifikasi
        val btnTestNotification = findViewById<Button>(R.id.btnTestNotification)
        btnTestNotification.setOnClickListener {
            checkPermissionAndSendNotification()
        }
    }

    private fun checkPermissionAndSendNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                sendTestBroadcast()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            sendTestBroadcast()
        }
    }

    private fun sendTestBroadcast() {
        val intent = Intent(this, NotificationReceiver::class.java)
        sendBroadcast(intent)
        Toast.makeText(this, "Broadcast dikirim!", Toast.LENGTH_SHORT).show()
    }
}