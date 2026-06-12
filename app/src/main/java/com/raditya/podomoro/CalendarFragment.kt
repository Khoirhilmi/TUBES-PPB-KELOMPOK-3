package com.raditya.podomoro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class CalendarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Logika ketika tombol tambah (+) diklik
        val fabAddEvent = view.findViewById<CardView>(R.id.fabAddEvent)
        fabAddEvent.setOnClickListener {
            Toast.makeText(requireContext(), "Tambah jadwal baru", Toast.LENGTH_SHORT).show()
        }
    }
}