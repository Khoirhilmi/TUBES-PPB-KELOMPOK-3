package com.raditya.podomoro

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val taskList: List<TaskModel>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewBorder: View = itemView.findViewById(R.id.viewBorder)
        val cvPriority: CardView = itemView.findViewById(R.id.cvPriority)
        val tvPriority: TextView = itemView.findViewById(R.id.tvPriority)
        val tvSubject: TextView = itemView.findViewById(R.id.tvSubject)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
        val tvDesc: TextView = itemView.findViewById(R.id.tvTaskDesc)
        val tvDeadline: TextView = itemView.findViewById(R.id.tvTaskDeadline)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        holder.tvPriority.text = task.prioritas
        holder.tvSubject.text = task.mataKuliah
        holder.tvTitle.text = task.judulTugas
        holder.tvDesc.text = task.deskripsi
        holder.tvDeadline.text = task.tenggatWaktu

        // Mengubah warna secara dinamis sesuai data
        holder.viewBorder.setBackgroundColor(Color.parseColor(task.warnaUtamaHex))
        holder.tvPriority.setTextColor(Color.parseColor(task.warnaUtamaHex))
        holder.tvDeadline.setTextColor(Color.parseColor(task.warnaUtamaHex))
        holder.cvPriority.setCardBackgroundColor(Color.parseColor(task.warnaBgHex))
    }

    override fun getItemCount() = taskList.size
}