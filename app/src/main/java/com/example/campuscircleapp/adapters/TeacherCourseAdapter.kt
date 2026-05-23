package com.example.campuscircleapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.features.home.models.AdminCourseResponse

class TeacherCourseAdapter(
    private var items: List<AdminCourseResponse>
) : RecyclerView.Adapter<TeacherCourseAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseName: TextView = itemView.findViewById(R.id.teacherCourseName)
        val courseCode: TextView = itemView.findViewById(R.id.teacherCourseCode)
        val courseCredits: TextView = itemView.findViewById(R.id.teacherCourseCredits)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_teacher_course, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.courseName.text = item.name
        holder.courseCode.text = item.code
        holder.courseCredits.text = "${item.credits} Credit Hours"
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<AdminCourseResponse>) {
        items = newItems
        notifyDataSetChanged()
    }
}
