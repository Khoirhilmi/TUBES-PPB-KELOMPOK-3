package com.raditya.podomoro

import android.app.IntentService
import android.content.Intent
import android.util.Log
import android.widget.Toast
import android.os.Handler
import android.os.Looper

/**
 * IntentService untuk mensimulasikan proses sinkronisasi data di background.
 * IntentService berjalan di worker thread sendiri dan berhenti otomatis setelah selesai.
 */
class DataSyncIntentService : IntentService("DataSyncIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        Log.d("SyncService", "Sinkronisasi data dimulai...")

        try {
            Thread.sleep(5000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        Log.d("SyncService", "Sinkronisasi data selesai!")

        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, "Sinkronisasi Data Berhasil!", Toast.LENGTH_SHORT).show()
        }
    }
}
