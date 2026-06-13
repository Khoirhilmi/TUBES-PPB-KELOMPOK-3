package com.raditya.podomoro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    private var isDarkMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = activity?.intent?.getStringExtra("USER_NAME") ?: "Mahasiswa"
        val email = activity?.intent?.getStringExtra("USER_EMAIL") ?: "mahasiswa@up.ac.id"

        val tvProfileName = view.findViewById<TextView>(R.id.tvProfileName)
        val tvProfileEmail = view.findViewById<TextView>(R.id.tvProfileEmail)

        tvProfileName.text = name
        tvProfileEmail.text = email

        val ivBellProfile = view.findViewById<ImageView>(R.id.ivBellProfile)
        ivBellProfile.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }

        val menuNotifikasi = view.findViewById<LinearLayout>(R.id.menuNotifikasi)
        menuNotifikasi.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }

        val menuTema = view.findViewById<LinearLayout>(R.id.menuTema)
        val tvTemaStatus = view.findViewById<TextView>(R.id.tvTemaStatus)

        menuTema.setOnClickListener {
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                tvTemaStatus.text = "Terang (Saat ini)"
                isDarkMode = false
                Toast.makeText(requireContext(), "Tema diubah ke Terang", Toast.LENGTH_SHORT).show()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                tvTemaStatus.text = "Gelap (Saat ini)"
                isDarkMode = true
                Toast.makeText(requireContext(), "Tema diubah ke Gelap", Toast.LENGTH_SHORT).show()
            }
        }

        val menuCadangkan = view.findViewById<LinearLayout>(R.id.menuCadangkan)
        menuCadangkan.setOnClickListener {
            Toast.makeText(requireContext(), "Data berhasil disinkronkan ke server lokal!", Toast.LENGTH_SHORT).show()
        }

        val btnKeluar = view.findViewById<Button>(R.id.btnKeluar)
        btnKeluar.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(requireContext(), "Berhasil keluar dari akun", Toast.LENGTH_SHORT).show()
        }
    }
}