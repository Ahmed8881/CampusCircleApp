package com.example.campuscircleapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.features.home.models.AdminDashboardCourse

class AdminDashboardCourseAdapter(
    private var courses: List<AdminDashboardCourse>
) : RecyclerView.Adapter<AdminDashboardCourseAdapter.AdminCourseViewHolder>() {

    class AdminCourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseCode: TextView = itemView.findViewById(R.id.adminCourseCode)
        val courseName: TextView = itemView.findViewById(R.id.adminCourseName)
        val courseAttendance: TextView = itemView.findViewById(R.id.adminCourseAttendance)
        val courseEnrollments: TextView = itemView.findViewById(R.id.adminCourseEnrollments)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminCourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_dashboard_course, parent, false)
        return AdminCourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminCourseViewHolder, position: Int) {
        val item = courses[position]
        holder.courseCode.text = item.courseCode.trim()
        holder.courseName.text = item.courseName.trim()
        holder.courseAttendance.text = "Attendance: ${item.attendance}%"
        holder.courseEnrollments.text = "Enrollments: ${item.enrollments}"
    }

    override fun getItemCount(): Int = courses.size

    fun updateData(newItems: List<AdminDashboardCourse>) {
        courses = newItems
        notifyDataSetChanged()
    }
}
