package com.example.campuscircleapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.features.home.models.MarkAttendanceStudentUi

class MarkAttendanceStudentAdapter(
    private var students: List<MarkAttendanceStudentUi>
) : RecyclerView.Adapter<MarkAttendanceStudentAdapter.MarkAttendanceViewHolder>() {

    private val statuses = listOf("present", "absent", "leave")

    class MarkAttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentName: TextView = itemView.findViewById(R.id.markStudentName)
        val rollNo: TextView = itemView.findViewById(R.id.markStudentRollNo)
        val statusSpinner: Spinner = itemView.findViewById(R.id.markStatusSpinner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkAttendanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mark_attendance_student, parent, false)
        return MarkAttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarkAttendanceViewHolder, position: Int) {
        val item = students[position]
        holder.studentName.text = item.studentName
        holder.rollNo.text = item.rollNo

        val spinnerAdapter = ArrayAdapter(
            holder.itemView.context,
            android.R.layout.simple_spinner_item,
            statuses
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.statusSpinner.adapter = spinnerAdapter

        val selectedIndex = statuses.indexOf(item.status.lowercase()).coerceAtLeast(0)
        holder.statusSpinner.setSelection(selectedIndex)

        holder.statusSpinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, spinnerPosition: Int, id: Long) {
                item.status = statuses[spinnerPosition]
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        })
    }

    override fun getItemCount(): Int = students.size

    fun updateData(newItems: List<MarkAttendanceStudentUi>) {
        students = newItems
        notifyDataSetChanged()
    }

    fun getCurrentData(): List<MarkAttendanceStudentUi> = students
}
