package com.example.campuscircleapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.features.admin.models.SemesterResponse

class SemesterAdapter(private val items: List<SemesterResponse>) :
    RecyclerView.Adapter<SemesterAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.semesterName)
        val no: TextView = view.findViewById(R.id.semesterNo)
        val dates: TextView = view.findViewById(R.id.semesterDates)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_semester, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.name.text = item.semesterName
        holder.no.text = "Semester ${item.semesterNo}"
        holder.dates.text = "${item.startDate} → ${item.endDate}"
    }
}
