package com.raditya.podomoro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class TimerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Ingat, sesuaikan dengan nama layout XML masing-masing
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }
}