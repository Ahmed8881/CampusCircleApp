package com.example.campuscircleapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.features.home.models.TimetableEntry

class TimetableAdapter(
    private var items: List<TimetableEntry>,
    private val onItemClick: (TimetableEntry) -> Unit = {}
) : RecyclerView.Adapter<TimetableAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseName: TextView = itemView.findViewById(R.id.timetableCourseName)
        val courseCode: TextView = itemView.findViewById(R.id.timetableCourseCode)
        val room: TextView = itemView.findViewById(R.id.timetableRoom)
        val startTime: TextView = itemView.findViewById(R.id.timetableStartTime)
        val endTime: TextView = itemView.findViewById(R.id.timetableEndTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_timetable_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.courseName.text = item.courseName
        holder.courseCode.text = item.courseCode
        holder.room.text = item.room
        holder.startTime.text = item.startTime.take(5)
        holder.endTime.text = item.endTime.take(5)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<TimetableEntry>) {
        items = newItems
        notifyDataSetChanged()
    }
}
