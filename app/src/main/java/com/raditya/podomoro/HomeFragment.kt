package com.raditya.podomoro

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    // Gunakan onViewCreated untuk mencari ID (findViewById) di dalam Fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Menerima data Nama dari LoginActivity
        // Karena ini di dalam Fragment, kita pakai "activity?.intent"
        val name = activity?.intent?.getStringExtra("USER_NAME") ?: "Budi"
        val tvGreeting = view.findViewById<TextView>(R.id.tvGreeting)

        // Memasang nama ke layar
        tvGreeting.text = getString(R.string.greeting_format, name)

        // 2. Fitur Implicit Intent: Membuka browser
        val tvSeeAll = view.findViewById<TextView>(R.id.tvSeeAll)
        tvSeeAll.setOnClickListener {
            val url = "https://www.google.com/calendar"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        // 3. Tombol lonceng membuka Notifikasi
        val ivNotification = view.findViewById<ImageView>(R.id.ivNotification)
        ivNotification.setOnClickListener {
            // Berpindah dari Fragment/Activity ini menuju NotificationActivity
            val intent = Intent(requireActivity(), NotificationActivity::class.java)
            startActivity(intent)
        }
    }
}