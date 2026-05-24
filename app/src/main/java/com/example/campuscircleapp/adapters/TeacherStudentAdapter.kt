package com.example.campuscircleapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.features.home.models.EnrollmentByCourseResponse

class TeacherStudentAdapter(
    private var items: List<EnrollmentByCourseResponse>
) : RecyclerView.Adapter<TeacherStudentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentName: TextView = itemView.findViewById(R.id.teacherStudentName)
        val rollNo: TextView = itemView.findViewById(R.id.teacherStudentEmail)
        val statusText: TextView = itemView.findViewById(R.id.teacherStudentAttendance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_teacher_student, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.studentName.text = item.studentName
        holder.rollNo.text = item.rollNo
        holder.statusText.text = item.status
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<EnrollmentByCourseResponse>) {
        items = newItems
        notifyDataSetChanged()
    }
}
