package com.example.campuscircleapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.features.home.models.AttendanceHistoryItem

class AttendanceHistoryAdapter(
    private var items: List<AttendanceHistoryItem>
) : RecyclerView.Adapter<AttendanceHistoryAdapter.AttendanceHistoryViewHolder>() {

    class AttendanceHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.attendanceDate)
        val statusText: TextView = itemView.findViewById(R.id.attendanceStatus)
        val markedByText: TextView = itemView.findViewById(R.id.markedBy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance_history, parent, false)
        return AttendanceHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendanceHistoryViewHolder, position: Int) {
        val item = items[position]
        holder.dateText.text = item.date
        holder.statusText.text = item.status.replaceFirstChar { it.uppercase() }
        holder.markedByText.text = "Marked by: ${item.markedBy}"

        val statusLower = item.status.lowercase()
        val statusColor = when (statusLower) {
            "present" -> android.graphics.Color.parseColor("#0F766E")
            "absent" -> android.graphics.Color.parseColor("#B91C1C")
            else -> android.graphics.Color.parseColor("#C27803")
        }
        holder.statusText.setTextColor(statusColor)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<AttendanceHistoryItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
