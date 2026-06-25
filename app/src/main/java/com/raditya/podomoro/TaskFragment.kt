package com.raditya.podomoro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TaskFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvTasks = view.findViewById<RecyclerView>(R.id.rvTasks)
        rvTasks.layoutManager = LinearLayoutManager(requireContext())

        val daftarTugas = listOf(
            TaskModel(
                prioritas = "TINGGI",
                mataKuliah = "Kalkulus II",
                judulTugas = "Tugas Kelompok Integral",
                deskripsi = "Selesaikan bab 4 hingga 6 bersama tim, kumpulkan dalam format PDF.",
                tenggatWaktu = "Besok, 23:59",
                warnaUtamaHex = "#D32F2F",
                warnaBgHex = "#FFEBEE"
            ),
            TaskModel(
                prioritas = "SEDANG",
                mataKuliah = "Fisika Dasar",
                judulTugas = "Ujian Tengah Semester",
                deskripsi = "Ruang 304. Materi: Kinematika, Dinamika, dan Usaha-Energi.",
                tenggatWaktu = "3 Hari Lagi",
                warnaUtamaHex = "#7B1FA2",
                warnaBgHex = "#F3E5F5"
            ),
            TaskModel(
                prioritas = "RENDAH",
                mataKuliah = "Sejarah",
                judulTugas = "Baca Jurnal Perang Dunia II",
                deskripsi = "Review 3 jurnal untuk diskusi minggu depan.",
                tenggatWaktu = "7 Hari Lagi",
                warnaUtamaHex = "#5C6BC0",
                warnaBgHex = "#E8EAF6"
            )
        )

        val adapter = TaskAdapter(daftarTugas)
        rvTasks.adapter = adapter
    }
}
