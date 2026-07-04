package com.example.campuscircleapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.features.admin.models.SemesterResponse

class SemesterAdapter(
    private var items: List<SemesterResponse>,
    private val onEdit: (SemesterResponse) -> Unit,
    private val onDelete: (SemesterResponse) -> Unit
) : RecyclerView.Adapter<SemesterAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.semesterName)
        val no: TextView = itemView.findViewById(R.id.semesterNo)
        val dates: TextView = itemView.findViewById(R.id.semesterDates)
        val editBtn: ImageButton = itemView.findViewById(R.id.semesterEditBtn)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.semesterDeleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_semester, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.no.text = "Semester ${item.number}"
        holder.dates.text = "${item.startDate.take(10)} → ${item.endDate.take(10)}"
        holder.editBtn.setOnClickListener { onEdit(item) }
        holder.deleteBtn.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<SemesterResponse>) {
        items = newItems
        notifyDataSetChanged()
    }
}
