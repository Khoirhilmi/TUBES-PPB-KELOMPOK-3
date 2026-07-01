package com.raditya.podomoro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CalendarAdapter(private var events: List<CalendarEvent>) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewTypeAccent: View = view.findViewById(R.id.viewTypeAccent)
        val tvEventTime: TextView = view.findViewById(R.id.tvEventTime)
        val tvEventTitle: TextView = view.findViewById(R.id.tvEventTitle)
        val tvEventLocation: TextView = view.findViewById(R.id.tvEventLocation)
        val tvEventType: TextView = view.findViewById(R.id.tvEventType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_event, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val event = events[position]
        holder.tvEventTime.text = event.time
        holder.tvEventTitle.text = event.title
        holder.tvEventLocation.text = event.location
        holder.tvEventType.text = event.type

        // Set accent color based on type
        val context = holder.itemView.context
        val color = when (event.type) {
            "Kuliah" -> ContextCompat.getColor(context, R.color.primary_blue)
            "Diskusi" -> ContextCompat.getColor(context, R.color.accent_purple)
            "Ujian" -> ContextCompat.getColor(context, R.color.status_red)
            else -> ContextCompat.getColor(context, R.color.text_grey)
        }
        holder.viewTypeAccent.setBackgroundColor(color)
        holder.tvEventType.setTextColor(color)
        holder.tvEventType.setBackgroundColor(ContextCompat.getColor(context, R.color.tag_blue_bg))
    }

    override fun getItemCount(): Int = events.size

    fun updateEvents(newEvents: List<CalendarEvent>) {
        events = newEvents
        notifyDataSetChanged()
    }
}